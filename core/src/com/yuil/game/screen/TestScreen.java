package com.yuil.game.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.yuil.game.MyGame;
import com.yuil.game.entity.BtObject;
import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.PhysicsObject;
import com.yuil.game.entity.PhysicsWorld;
import com.yuil.game.entity.RenderableBtObject;
import com.yuil.game.entity.message.ADD_BALL;
import com.yuil.game.entity.message.EntityMessageType;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.ActorInputListenner;
import com.yuil.game.input.InputManager;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.ClientSocket;
import com.yuil.game.net.udp.Session;
import com.yuil.game.net.udp.UdpMessageListener;
import com.yuil.game.util.Log;

public class TestScreen extends Screen2D implements UdpMessageListener{
	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap=new HashMap<Integer, MessageHandler>();

	
	
	PhysicsWorld physicsWorld;
	Environment lights;


	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch=new ModelBatch();
	BtObjectFactory btObjectFactory=new BtObjectFactory(true);
	
	long interval=50;
	long lastTime=0;
	public TestScreen(MyGame game) {
		super(game);
		clientSocket=new ClientSocket(9092,"127.0.0.1",9091,this);
		initMessageHandle();
		
		// TODO Auto-generated constructor stub
		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/TestScreen.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));
		
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(btObjectFactory.createRenderableGround());

		// Set up the camera
		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		if (width > height)
			camera = new PerspectiveCamera(67f, 3f * width / height, 3f);
		else
			camera = new PerspectiveCamera(67f, 3f, 3f * height / width);
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.update();
		camController = new CameraInputController(camera);
		
		setUpInput();
		InputManager.setInputProcessor(stage,camController);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		

		/*if (System.currentTimeMillis()-lastTime>interval){
			lastTime=System.currentTimeMillis();
			physicsWorld.addPhysicsObject(btObjectFactory.createBall());
		}*/

		physicsWorld.update(delta);

		
		modelBatch.begin(camera);

		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {
			ModelInstance modelInstance=((RenderableBtObject)physicsObject).getInstance();
			((BtObject)physicsObject).getRigidBody().getWorldTransform(modelInstance.transform);
			
			modelBatch.render(modelInstance,lights);
		}
		modelBatch.end();
		super.render(delta);
	}

	@Override
	public void disposeUdpMessage(Session session, byte[] data) {
		// TODO Auto-generated method stub
		if (data.length<Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data);
		//System.out.println("type:" + GameMessageType.values()[typeOrdinal]);
		byte[] src =MessageUtil.getMessageBytes(data);
		
		switch (MessageType.values()[typeOrdinal]) {
		case MESSAGE_ARRAY:
			MESSAGE_ARRAY message_ARRAY=new MESSAGE_ARRAY(src);
			for (byte[] data1:message_ARRAY.gameMessages) {
				disposeSingleMessage(session, data1);
			}
			break;
		case SINGLE_MESSAGE:
			disposeSingleMessage(session, src);
			break;
		default:
			break;
		}		
	}
	
	void disposeSingleMessage(Session session, byte[] data){
		if (data.length<Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data);
		//System.out.println("type:" + GameMessageType.values()[typeOrdinal]);
		byte[] src =MessageUtil.getMessageBytes(data);
		
		messageHandlerMap.get(typeOrdinal).handle(src);
	}
	void setUpInput(){
		stage.getRoot().findActor("A").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				aJustUpAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				aJustPressAction();
				return true;
			}
		});
		stage.getRoot().findActor("D").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				dJustUpAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				dJustPressAction();
				return true;
			}
		});
		stage.getRoot().findActor("Z").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				zJustPressAction();
			}
		});
		stage.getRoot().findActor("X").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

			}
		});
		stage.getRoot().findActor("G").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//gameObjectId = Long.parseLong(((TextArea) stage.getRoot().findActor("userName")).getText());

			}
		});

		stage.getRoot().findActor("W").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});

		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});	
	}

	protected void zJustPressAction() {
		Log.println("zJustPressAction");
		ADD_BALL add_BALL=new ADD_BALL();
		add_BALL.setId(new Random().nextLong());
		add_BALL.setX(10);
		add_BALL.setY(10);
		add_BALL.setZ(10);
		sendSingleMessage(add_BALL);
	}

	protected void dJustPressAction() {
		// TODO Auto-generated method stub
		
	}

	protected void dJustUpAction() {
		// TODO Auto-generated method stub
		
	}

	protected void aJustPressAction() {
		// TODO Auto-generated method stub
		
	}

	protected void aJustUpAction() {
		// TODO Auto-generated method stub
		
	}
	
	
	void sendSingleMessage(Message message){
		clientSocket.send(SINGLE_MESSAGE.getBytes(message.toBytes()), false);
	}
	void sendSingleMessage(byte[] data){
		clientSocket.send(SINGLE_MESSAGE.getBytes(data), false);
	}
	
	void initMessageHandle(){
		messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {
			
			@Override
			public void handle(byte[] src) {
				// TODO Auto-generated method stub
				
			}
		});
		
		messageHandlerMap.put(EntityMessageType.ADD_BALL.ordinal(), new MessageHandler() {
			
			@Override
			public void handle(byte[] src) {
				// TODO Auto-generated method stub
				ADD_BALL message=new ADD_BALL(src);
				physicsWorld.addPhysicsObject(btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, message.getX(), message.getY(), message.getZ()));
			}
		});

	}
}
