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
import com.yuil.game.entity.BtObject;
import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.PhysicsObject;
import com.yuil.game.entity.PhysicsWorld;
import com.yuil.game.entity.PhysicsWorldBuilder;
import com.yuil.game.entity.RenderableBtObject;
import com.yuil.game.entity.message.ADD_BALL;
import com.yuil.game.entity.message.EntityMessageType;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.message.TEST;
import com.yuil.game.entity.message.UPDATE_BTRIGIDBODY;
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

public class TestScreen2 extends Screen2D implements MessageListener{
	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap=new HashMap<Integer, MessageHandler>();
	
	PhysicsWorldBuilder physicsWorldBuilder;
	PhysicsWorld physicsWorld;
	Environment lights;


	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch=new ModelBatch();
	
	long interval=100;
	long nextTime=0;
	
	Random random=new Random();
	
	long playerId;
	BtObject playerObject;
	
	Sound sound=Gdx.audio.newSound(Gdx.files.internal("sound/bee.wav"));
	APPLY_FORCE apply_FORCE=new APPLY_FORCE();
	
	
	Matrix4 tempMatrix4=new Matrix4();
	UPDATE_BTRIGIDBODY tempMessage;
	
	boolean isLogin=false;
	public TestScreen2(MyGame game) {
		super(game);
		clientSocket=new ClientSocket(9092,"127.0.0.1",9091,this);
		initMessageHandle();
		
		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/TestScreen2.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));
		
		physicsWorldBuilder =new PhysicsWorldBuilder();
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(physicsWorldBuilder.btObjectFactory.createRenderableGround());
	
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
		
		if(playerObject==null){
			if (playerId!=0){
				playerObject=(BtObject) physicsWorld.getPhysicsObjects().get(playerId);
			}
		}else{
			//System.out.println("x:"+playerObject.getPosition().x);
			camera.position.set(playerObject.getPosition().x, 15f, playerObject.getPosition().z+20);
			//camera.lookAt(playerObject.getPosition().x,playerObject.getPosition().y, playerObject.getPosition().z);
			camera.update();
		}
		
		
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
			if (playerId==0){
				ADD_PLAYER add_player=new ADD_PLAYER();
				sendSingleMessage(add_player);
			}
		}else{
			TEST message=new TEST();
			sendSingleMessage(message);
		}
		
	}

	protected void dJustPressAction() {
		// TODO Auto-generated method stub
		if(playerId!=0){
			apply_FORCE.setX(10);
			apply_FORCE.setId(playerId);
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
		if(playerId!=0){
			apply_FORCE.setX(-10);
			apply_FORCE.setId(playerId);
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
	}

	protected void wJustUpAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void sJustPressAction() {
//		if(btObject!=null){
//			physicsWorld.updatePhysicsObject(tempMessage);
//		}
	}
	
	protected void delJustPressAction() {
		
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
		messageHandlerMap.put(EntityMessageType.ADD_PLAYER.ordinal(), new MessageHandler() {
			ADD_PLAYER message=new ADD_PLAYER();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				System.out.println("added player");
				playerId=message.getId();
			}
		});
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
				if(physicsWorld.getPhysicsObjects().get(message.getId())==null){
					RenderableBtObject btObject1=physicsWorldBuilder.createDefaultRenderableBall(1, 0, 10000, 0);
					btObject1.setId(message.getId());
					
					physicsWorld.addPhysicsObject(btObject1);
				}
				
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
		
		messageHandlerMap.put(EntityMessageType.UPDATE_BTRIGIDBODY.ordinal(), new MessageHandler() {
			UPDATE_BTRIGIDBODY message=new UPDATE_BTRIGIDBODY();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				if(physicsWorld.getPhysicsObjects().get(message.getId())==null){
					BtObject btObject1=physicsWorldBuilder.createDefaultBall(1, 0, 10000, 0);
					btObject1.setId(message.getId());
					physicsWorld.addPhysicsObject(btObject1);
				}
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
