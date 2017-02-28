package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.yuil.game.MyGame;
import com.yuil.game.entity.message.ADD_BALL;
import com.yuil.game.entity.message.EntityMessageType;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.message.TEST;
import com.yuil.game.entity.message.UPDATE_BTOBJECT_MOTIONSTATE;
import com.yuil.game.entity.physics.BtObject;
import com.yuil.game.entity.physics.BtObjectFactory;
import com.yuil.game.entity.physics.BtWorld;
import com.yuil.game.entity.physics.PhysicsObject;
import com.yuil.game.entity.physics.PhysicsWorld;
import com.yuil.game.entity.physics.RenderableBtObject;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.ActorInputListenner;
import com.yuil.game.input.InputManager;
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.ClientSocket;
import com.yuil.game.util.Log;

import io.netty.buffer.ByteBuf;

public class TestScreen extends Screen2D implements MessageListener{
	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap=new HashMap<Integer, MessageHandler>();
	
	
	PhysicsWorld physicsWorld;
	Environment lights;


	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch=new ModelBatch();
	BtObjectFactory btObjectFactory=new BtObjectFactory(true);
	
	long interval=100;
	long nextTime=0;
	
	Random random=new Random();

	Sound sound=Gdx.audio.newSound(Gdx.files.internal("sound/bee.wav"));
	ADD_BALL add_BALL=new ADD_BALL();
	APPLY_FORCE apply_FORCE=new APPLY_FORCE();
	
	
	BtObject btObject;
	Matrix4 tempMatrix4=new Matrix4();
	UPDATE_BTOBJECT_MOTIONSTATE tempMessage;
	
	boolean isLogin=false;
	public TestScreen(MyGame game) {
		super(game);
		clientSocket=new ClientSocket(9092,"127.0.0.1",9091,this);
		initMessageHandle();
		
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
		
		setupInput();
		InputManager.setInputProcessor(stage,camController);
		
		nextTime=System.currentTimeMillis();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		

		if (System.currentTimeMillis()>=nextTime){
			nextTime+=interval;
			//physicsWorld.update(interval);
			/*physicsWorld.addPhysicsObject(btObjectFactory.createBall());*/
		//	zJustPressAction();
		}
		
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
	public void recvMessage(Session session, ByteBuf data) {
/*		try {
			Runtime.getRuntime().exec("cmd /k start /MIN d:\\bat\\test1.bat" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		// TODO Auto-generated method stub
		if (data.array().length<Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		//System.out.println("type:" + GameMessageType.values()[typeOrdinal]);
		
		switch (MessageType.values()[typeOrdinal]) {
		case MESSAGE_ARRAY:
			MESSAGE_ARRAY message_ARRAY=new MESSAGE_ARRAY(data);
			for (ByteBuf data1:message_ARRAY.gameMessages) {
				disposeSingleMessage(session, data1);
			}
			break;
		case SINGLE_MESSAGE:
			data.skipBytes(Message.TYPE_LENGTH);
			data.discardReadBytes();
			disposeSingleMessage(session, data);
			break;
		default:
			break;
		}		
	}
	
	void disposeSingleMessage(Session session, ByteBuf data){
		if (data.array().length<Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		messageHandlerMap.get(typeOrdinal).handle(data);
	}
	void setupInput(){
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
				delJustPressAction();
			}
		});
		stage.getRoot().findActor("G").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//gameObjectId = Long.parseLong(((TextArea) stage.getRoot().findActor("userName")).getText());

			}
		});

		stage.getRoot().findActor("W").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				wJustPressAction() ;
			}
		});
		
		stage.getRoot().findActor("S").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sJustPressAction() ;
			}
		});

		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});	
	}

	protected void zJustPressAction() {
		if(isLogin){
			//sound.play();
			
			Log.println("zJustPressAction");
			add_BALL.setId(random.nextLong());
			add_BALL.setX(10+random.nextInt(10));
			add_BALL.setY(10+random.nextInt(10));
			add_BALL.setZ(10+random.nextInt(10));
			/*add_BALL.setX(10);
			add_BALL.setY(10);
			add_BALL.setZ(10);
			*/sendSingleMessage(add_BALL);
		}else{
			TEST message=new TEST();
			sendSingleMessage(message);
		}
		
	}

	protected void dJustPressAction() {
		// TODO Auto-generated method stub
		if(btObject!=null){
			apply_FORCE.setX(100);
			apply_FORCE.setId(btObject.getId());
			sendSingleMessage(apply_FORCE);
		}
	}

	protected void dJustUpAction() {
		// TODO Auto-generated method stub
		
	}

	protected void aJustPressAction() {
		// TODO Auto-generated method stub
		//btObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, random.nextFloat(), random.nextFloat()+10 ,random.nextFloat());
//		btObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultPlayerModel,btObjectFactory.getDefaultCylinderShape(), 1, random.nextFloat(), random.nextFloat()+10 ,random.nextFloat());
//
//		btObject.setId(random.nextLong());
//		physicsWorld.addPhysicsObject(btObject);
		if(btObject!=null){
			apply_FORCE.setX(-100);
			apply_FORCE.setId(btObject.getId());
			sendSingleMessage(apply_FORCE);
		}
	}

	protected void aJustUpAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void wJustPressAction() {
		
//		if(btObject!=null){
//			physicsWorld.updatePhysicsObject(tempMessage);
//		}
		if(btObject!=null){
			apply_FORCE.setY(100);
			apply_FORCE.setId(btObject.getId());
			sendSingleMessage(apply_FORCE);
		}
	}

	protected void wJustUpAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void sJustPressAction() {
//		if(btObject!=null){
//			physicsWorld.updatePhysicsObject(tempMessage);
//		}
		if(btObject!=null){
			apply_FORCE.setY(-100);
			apply_FORCE.setId(btObject.getId());
			sendSingleMessage(apply_FORCE);
		}
	}
	
	protected void delJustPressAction() {
		if(btObject!=null){
			tempMessage=new UPDATE_BTOBJECT_MOTIONSTATE(btObject);
		}
		
	}

	protected void delJustUpAction() {
		// TODO Auto-generated method stub
		
	}
	
	
	void sendSingleMessage(Message message){

		clientSocket.send(SINGLE_MESSAGE.get(message.get().array()).array(), false);

	}
	void sendSingleMessage(byte[] data){
		clientSocket.send(SINGLE_MESSAGE.get(data).array(), false);
	}
	
	void initMessageHandle(){
		messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {
			
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				
			}
		});
		
		messageHandlerMap.put(EntityMessageType.ADD_BALL.ordinal(), new MessageHandler() {
			ADD_BALL message=new ADD_BALL();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub

				message.set(src);
				BtObject btObject1=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, message.getX(), message.getY(), message.getZ());
				btObject1.setId(message.getId());
				physicsWorld.addPhysicsObject(btObject1);
				btObject=btObject1;
			}
		});
		
		messageHandlerMap.put(EntityMessageType.APPLY_FORCE.ordinal(), new MessageHandler() {
			APPLY_FORCE message=new APPLY_FORCE();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				
			}
		});
		
		
		messageHandlerMap.put(EntityMessageType.REMOVE_BTOBJECT.ordinal(), new MessageHandler() {
			REMOVE_BTOBJECT message=new REMOVE_BTOBJECT();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				physicsWorld.removePhysicsObject(physicsWorld.getPhysicsObjects().get(message.getId()));
			}
		});
		
		messageHandlerMap.put(EntityMessageType.UPDATE_BTOBJECT_MOTIONSTATE.ordinal(), new MessageHandler() {
			UPDATE_BTOBJECT_MOTIONSTATE message=new UPDATE_BTOBJECT_MOTIONSTATE();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				physicsWorld.updatePhysicsObject(message);
			}
		});
		
		
		
		messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				isLogin=true;
			}
		});

	}
}
