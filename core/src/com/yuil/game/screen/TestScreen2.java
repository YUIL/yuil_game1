package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
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
import com.yuil.game.input.KeyboardStatus;
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
	
	final float NO_CHANGE=1008611;//代表无效参数的一个值
	
	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap=new HashMap<Integer, MessageHandler>();
	
	PhysicsWorldBuilder physicsWorldBuilder;
	PhysicsWorld physicsWorld;
	Environment lights;
	
	KeyboardStatus keyboardStatus=new KeyboardStatus();
	
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
		clientSocket=new ClientSocket(9092,"uyuil.com",9091,this);
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
		
		setupActorInput();
		InputManager.setInputProcessor(stage,camController);
		
		nextTime=System.currentTimeMillis();
	}

	@Override
	public void render(float delta) {
		checkKeyBoardStatus();
		
		
		
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
		
		if(playerObject==null){
			if (playerId!=0){
				playerObject=(BtObject) physicsWorld.getPhysicsObjects().get(playerId);
			}
		}else{
			//System.out.println("x:"+playerObject.getPosition().x);
			camera.position.set(playerObject.getPosition().x, playerObject.getPosition().y+2f, playerObject.getPosition().z+5);
			//camera.lookAt(playerObject.getPosition().x,playerObject.getPosition().y, playerObject.getPosition().z);
			camera.update();
		}
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
	void checkKeyBoardStatus(){
		if (Gdx.input.isKeyJustPressed(Keys.A)) {
			// game.getScreen().dispose();
			keyboardStatus.setaJustPressed(true);
			aJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.A)==false&& keyboardStatus.isaJustPressed()) {
			keyboardStatus.setaJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.D)){
				dJustPressedAction();;
			}else{
				aJustUppedAction();
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			// game.getScreen().dispose();
			keyboardStatus.setdJustPressed(true);
			dJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.D)==false&& keyboardStatus.isdJustPressed()) {
			keyboardStatus.setdJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.A)){
				aJustPressedAction();;
			}else{
				dJustUppedAction();
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			// game.getScreen().dispose();
			keyboardStatus.setSpaceJustPressed(true);
			spaceJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.SPACE)==false&& keyboardStatus.isdJustPressed()) {
			keyboardStatus.setdJustPressed(false);
			spaceJustUppedAction();
		}
		
	}
	void setupActorInput(){
		stage.getRoot().findActor("A").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				aJustUppedAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				aJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("D").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				dJustUppedAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				dJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("Z").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				zJustPressedAction();
			}
		});
		stage.getRoot().findActor("X").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				delJustPressedAction();
			}
		});
		stage.getRoot().findActor("G").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//gameObjectId = Long.parseLong(((TextArea) stage.getRoot().findActor("userName")).getText());

			}
		});

		stage.getRoot().findActor("W").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				wJustPressedAction() ;
			}
		});
		
		stage.getRoot().findActor("S").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sJustPressedAction() ;
			}
		});

		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});	
	}

	protected void zJustPressedAction() {
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

	protected void dJustPressedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0){
			apply_FORCE.setX(10);
			apply_FORCE.setY(NO_CHANGE);
			apply_FORCE.setZ(NO_CHANGE);
			apply_FORCE.setId(playerId);
			physicsWorld.applyForce(apply_FORCE);
			sendSingleMessage(apply_FORCE);
		}
	}

	protected void dJustUppedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0){
			apply_FORCE.setX(0);
			apply_FORCE.setY(NO_CHANGE);
			apply_FORCE.setZ(NO_CHANGE);
			apply_FORCE.setId(playerId);
			physicsWorld.applyForce(apply_FORCE);
			sendSingleMessage(apply_FORCE);
		}
	}

	protected void aJustPressedAction() {
		// TODO Auto-generated method stub
		//btObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, random.nextFloat(), random.nextFloat()+10 ,random.nextFloat());
//		btObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultPlayerModel,btObjectFactory.getDefaultCylinderShape(), 1, random.nextFloat(), random.nextFloat()+10 ,random.nextFloat());
//
//		btObject.setId(random.nextLong());
//		physicsWorld.addPhysicsObject(btObject);
		if(playerId!=0){
			apply_FORCE.setX(-10);
			apply_FORCE.setY(NO_CHANGE);
			apply_FORCE.setZ(NO_CHANGE);
			apply_FORCE.setId(playerId);
			physicsWorld.applyForce(apply_FORCE);
			sendSingleMessage(apply_FORCE);
		}
	}

	protected void aJustUppedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0){
			apply_FORCE.setX(0);
			apply_FORCE.setY(NO_CHANGE);
			apply_FORCE.setZ(NO_CHANGE);
			apply_FORCE.setId(playerId);
			physicsWorld.applyForce(apply_FORCE);
			sendSingleMessage(apply_FORCE);
		}
	}
	
	protected void wJustPressedAction() {
		spaceJustPressedAction();
//		if(btObject!=null){
//			physicsWorld.updatePhysicsObject(tempMessage);
//		}
	}

	protected void wJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void sJustPressedAction() {
//		if(btObject!=null){
//			physicsWorld.updatePhysicsObject(tempMessage);
//		}
	}
	
	protected void spaceJustPressedAction() {
		if(playerId!=0){
			if(playerObject.getPosition().y<0.7f){
				apply_FORCE.setX(NO_CHANGE);
				apply_FORCE.setY(10);
				apply_FORCE.setZ(NO_CHANGE);
				apply_FORCE.setId(playerId);
				physicsWorld.applyForce(apply_FORCE);
				sendSingleMessage(apply_FORCE);
			}
		}
	}

	protected void spaceJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void delJustPressedAction() {
		
	}

	protected void delJustUppedAction() {
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
					BtObject btObject1=physicsWorldBuilder.createDefaultRenderableBall(1, 0, 10000, 0);
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
