package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.yuil.game.MyGame;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.DamagePoint;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.attribute.HealthPoint;
import com.yuil.game.entity.attribute.OwnerPlayerId;
import com.yuil.game.entity.gameobject.GameObjectType;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.physics.BtObject;
import com.yuil.game.entity.physics.BtObjectFactory;
import com.yuil.game.entity.physics.BtWorld;
import com.yuil.game.entity.physics.PhysicsObject;
import com.yuil.game.entity.physics.PhysicsWorld;
import com.yuil.game.entity.physics.PhysicsWorldBuilder;
import com.yuil.game.entity.physics.RenderableBtObject;
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
import com.yuil.game.screen.RigidBodyTestScreen.MyContactListener;
import com.yuil.game.util.Log;

import io.netty.buffer.ByteBuf;

public class TestScreen2 extends Screen2D implements MessageListener{
	
	
	Queue<S2C_ADD_OBSTACLE> createObstacleQueue =new  ConcurrentLinkedQueue<S2C_ADD_OBSTACLE>();

	boolean turnLeft=true;
	long nextTurnTime=0;
	
	final float NO_CHANGE=1008611;//代表无效参数的一个值
	
	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap=new HashMap<Integer, MessageHandler>();
	
	PhysicsWorldBuilder physicsWorldBuilder;
	PhysicsWorld physicsWorld;
	Environment lights;
	
	ContactListener contactListener;

	
	KeyboardStatus keyboardStatus=new KeyboardStatus();
	
	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch=new ModelBatch();
	
	long interval=100;
	long nextTime=0;
	
	Random random=new Random(System.currentTimeMillis());
	
	long playerId;
	BtObject playerObject;
	int vinearVelocityX=10;
	
	Sound sound=Gdx.audio.newSound(Gdx.files.internal("sound/bee.wav"));
	
	Matrix4 tempMatrix4=new Matrix4();
	Vector3 tempVector3=new Vector3();
	
	UPDATE_BTOBJECT_MOTIONSTATE temp_update_rigidbody_message;
	UPDATE_LINEAR_VELOCITY temp_update_liner_velocity_message=new UPDATE_LINEAR_VELOCITY();
	DO_ACTION temp_do_action_messge=new DO_ACTION();
	
	
	boolean isLogin=false;
	public TestScreen2(MyGame game) {
		super(game);
		clientSocket=new ClientSocket(9092,"127.0.0.1",9091,this);

		//clientSocket=new ClientSocket(9092,"uyuil.com",9091,this);
		initMessageHandle();
		
		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/TestScreen2.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));
		
		physicsWorldBuilder =new PhysicsWorldBuilder(true);
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(physicsWorldBuilder.btObjectFactory.createRenderableGround());
	
		contactListener=new MyContactListener();

		
		// Set up the camera
		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		if (width > height)
			camera = new PerspectiveCamera(67f, 3f * width / height, 3f);
		else
			camera = new PerspectiveCamera(67f, 3f, 3f * height / width);
		camera.far=200;
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.update();
		camController = new CameraInputController(camera);
		
		setupActorInput();
		InputManager.setInputProcessor(stage,camController);
		
		nextTime=System.currentTimeMillis();
	}
public class MyContactListener extends ContactListener {
		Vector3 v3 = new Vector3();

		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody ) {
				BtObject btObject0=(BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1=(BtObject) (((btRigidBody) colObj1).userData);
				handleBtObject(btObject0);
				handleBtObject(btObject1);
				
				GameObjectTypeAttribute gameObjectType0=(GameObjectTypeAttribute)(btObject0.Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
				GameObjectTypeAttribute gameObjectType1=(GameObjectTypeAttribute)(btObject1.Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));

				if (gameObjectType0!=null&&gameObjectType1!=null) {
					if(gameObjectType0.getGameObjectType()==GameObjectType.PLAYER.ordinal()&&gameObjectType1.getGameObjectType()==GameObjectType.OBSTACLE.ordinal()){
						//System.out.println("coll");
						//sound.play();
					}else if(gameObjectType0.getGameObjectType()==GameObjectType.OBSTACLE.ordinal()&&gameObjectType1.getGameObjectType()==GameObjectType.PLAYER.ordinal()){
						//System.out.println("coll");

					//	sound.play();

					}
				}
				
				
				/*
					if(gameObjectType != null){
						if(gameObjectType.getGameObjectType()==com.yuil.game.entity.gameobject.GameObjectType.OBSTACLE.ordinal()){
							System.out.println("asdasds");
							physicsWorld.removePhysicsObject(btObject);
							remove_BTOBJECT_message.setId(btObject.getId());
							broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
						}
					}*/
				
			}	
		}
		void handleBtObject(BtObject btObject){
			if (btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal()) != null) {
				v3.set(0, btObject.getRigidBody().getLinearVelocity().y, 0);
				btObject.getRigidBody().setLinearVelocity(v3);
			}
		}
		
	    @Override
	    public void onContactEnded (int userValue0, boolean match0, int userValue1, boolean match1) {
	        if (match0) {
	            // collision object 0 (userValue0) matches
	        }
	        if (match1) {
	            // collision object 1 (userValue1) matches
	        }
	        //System.out.println("onContactEnded()");
	    }
	}
	@Override
	public void render(float delta) {
		checkKeyBoardStatus();
		
		while(!createObstacleQueue.isEmpty()){
			S2C_ADD_OBSTACLE message=createObstacleQueue.poll();
			C2S_UPDATE_BTOBJECT_MOTIONSTATE c2s_UPDATE_BTOBJECT_MOTIONSTATE_message=new C2S_UPDATE_BTOBJECT_MOTIONSTATE();
			Color color=new Color();
			Vector3 v3=new Vector3();
				if(physicsWorld.getPhysicsObjects().get(message.getId())==null){
					v3.x=0;
					v3.y=-100;
					v3.z=-100;
					color.set(message.getR(), message.getG(), message.getB(), message.getA());
					//System.out.println("color.g:"+message.getG());
					BtObject btObject=physicsWorldBuilder.createRenderableBall(message.getRadius(), message.getRadius(), v3, color);
					btObject.setId(message.getId());
					btObject.Attributes.put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.OBSTACLE.ordinal()));
					btObject.Attributes.put(AttributeType.DAMAGE_POINT.ordinal(), new DamagePoint(1));
					btObject.Attributes.put(AttributeType.COLOR.ordinal(), new com.yuil.game.entity.attribute.Color(color));
					//btObject.getRigidBody().setContactCallbackFilter(GameObjectType.PLAYER.ordinal());
					btObject.getRigidBody().setContactCallbackFilter(1<<GameObjectType.GROUND.ordinal());

					physicsWorld.addPhysicsObject(btObject);
					c2s_UPDATE_BTOBJECT_MOTIONSTATE_message.setId(message.getId());
					sendSingleMessage(c2s_UPDATE_BTOBJECT_MOTIONSTATE_message);
				}
				
			
		}
/*		if(System.currentTimeMillis()>nextTurnTime){
			aJustUppedAction();
			nextTurnTime=System.currentTimeMillis()+100;
			if (turnLeft) {
				aJustPressedAction();
			}else{
				dJustPressedAction();
			}
			turnLeft=!turnLeft;
		}*/
		
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
			try {
				camera.position.set(playerObject.getPosition().x, playerObject.getPosition().y+2f, playerObject.getPosition().z+10);
				//camera.lookAt(playerObject.getPosition().x,playerObject.getPosition().y, playerObject.getPosition().z);
				camera.update();
			} catch (Exception e) {
				//System.out.println("object已被刪除");
			}
		}
		modelBatch.begin(camera);

		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {
			ModelInstance modelInstance=((RenderableBtObject)physicsObject).getInstance();
			((BtObject)physicsObject).getRigidBody().getWorldTransform(modelInstance.transform);

			GameObjectTypeAttribute gameObjectType=(GameObjectTypeAttribute)(((BtObject)physicsObject).Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
/*
			if (gameObjectType!=null) {
				if (gameObjectType.getGameObjectType()==GameObjectType.GROUND.ordinal()){
					
				}else{
					System.out.println(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
				}
			}*/
			modelInstance.transform.scl(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
			//modelInstance.nodes.first().localTransform.scl(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
			
			if (((GameObjectTypeAttribute)(((BtObject)physicsObject).Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()))).getGameObjectType() ==GameObjectType.OBSTACLE.ordinal()) {
				// 检查障碍物位置,超过边界则删除
				//System.out.println("asdasd");
				if (((BtObject)physicsObject).getPosition().z > -45) {
					physicsWorld.removePhysicsObject(physicsObject);
					//remove_BTOBJECT_message.setId(btObject.getId());
					//broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
				}
			}
			//modelInstance.transform.scl(2);
			//System.out.println(modelInstance);
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
		//System.out.println("typeOrdinal"+typeOrdinal);
		messageHandlerMap.get(typeOrdinal).handle(data);
	}
	void checkKeyBoardStatus(){
		
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			keyboardStatus.setqJustPressed(true);
			System.out.println("getLinearVelocity().z:"+playerObject.getRigidBody().getLinearVelocity().z);
			
			System.out.println();
		}else if (Gdx.input.isKeyPressed(Keys.Q)==false&& keyboardStatus.isqJustPressed()) {
			keyboardStatus.setqJustPressed(false);
			
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.A)) {
			// game.getScreen().dispose();
			keyboardStatus.setaJustPressed(true);
			aJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.A)==false&& keyboardStatus.isaJustPressed()) {
			keyboardStatus.setaJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.D)){
				dJustPressedAction();
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
				aJustPressedAction();
			}else{
				dJustUppedAction();
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			// game.getScreen().dispose();
			keyboardStatus.setSpaceJustPressed(true);
			spaceJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.SPACE)==false&& keyboardStatus.isSpaceJustPressed()) {
			keyboardStatus.setSpaceJustPressed(false);
			spaceJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum0JustPressed(true);
			Num0JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_0)==false&& keyboardStatus.isNum0JustPressed()) {
			keyboardStatus.setNum0JustPressed(false);
			Num0JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum1JustPressed(true);
			Num1JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_1)==false&& keyboardStatus.isNum1JustPressed()) {
			keyboardStatus.setNum1JustPressed(false);
			Num1JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum2JustPressed(true);
			Num2JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_2)==false&& keyboardStatus.isNum2JustPressed()) {
			keyboardStatus.setNum2JustPressed(false);
			Num2JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum3JustPressed(true);
			Num3JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_3)==false&& keyboardStatus.isNum3JustPressed()) {
			keyboardStatus.setNum3JustPressed(false);
			Num3JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum4JustPressed(true);
			Num4JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_4)==false&& keyboardStatus.isNum4JustPressed()) {
			keyboardStatus.setNum4JustPressed(false);
			Num4JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum5JustPressed(true);
			Num5JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_5)==false&& keyboardStatus.isNum5JustPressed()) {
			keyboardStatus.setNum5JustPressed(false);
			Num5JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_6)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum6JustPressed(true);
			Num6JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_6)==false&& keyboardStatus.isNum6JustPressed()) {
			keyboardStatus.setNum6JustPressed(false);
			Num6JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_7)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum7JustPressed(true);
			Num7JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_7)==false&& keyboardStatus.isNum7JustPressed()) {
			keyboardStatus.setNum7JustPressed(false);
			Num7JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum8JustPressed(true);
			Num8JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_8)==false&& keyboardStatus.isNum8JustPressed()) {
			keyboardStatus.setNum8JustPressed(false);
			Num8JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum9JustPressed(true);
			Num9JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_9)==false&& keyboardStatus.isNum9JustPressed()) {
			keyboardStatus.setNum9JustPressed(false);
			Num9JustUppedAction();
		}
		
	}
	private void Num9JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num9JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num8JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num8JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num7JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num7JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num6JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num6JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num5JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num5JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num4JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num4JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num3JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num3JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num2JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num2JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num1JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num1JustPressedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0&&playerObject!=null){
			temp_do_action_messge.setPlayerId(playerId);
			temp_do_action_messge.setActionId(1);
			sendSingleMessage(temp_do_action_messge);   
		}
	}

	private void Num0JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	private void Num0JustPressedAction() {
		// TODO Auto-generated method stub
		System.out.println("asd");
	}


	protected void zJustPressedAction() {
		if(isLogin){
			//sound.play();
			if (playerId==0){
				playerId=random.nextLong();
				System.out.println("new id:"+playerId);
				C2S_ADD_PLAYER add_player=new C2S_ADD_PLAYER();
				add_player.setId(playerId);
				sendSingleMessage(add_player);
			}
		}else{
			TEST message=new TEST();
			sendSingleMessage(message);
		}
		
	}

	protected void dJustPressedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0&&playerObject!=null){

			temp_update_liner_velocity_message.setX(vinearVelocityX);
			temp_update_liner_velocity_message.setY(NO_CHANGE);
			temp_update_liner_velocity_message.setZ(NO_CHANGE);
			temp_update_liner_velocity_message.setId(playerObject.getId());
			sendSingleMessage(temp_update_liner_velocity_message);

			tempVector3.set(playerObject.getRigidBody().getLinearVelocity());
			tempVector3.x=vinearVelocityX;
			playerObject.getRigidBody().setLinearVelocity(tempVector3);
			
		}
	}

	protected void dJustUppedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0&&playerObject!=null){
			temp_update_liner_velocity_message.setX(0);
			temp_update_liner_velocity_message.setY(NO_CHANGE);
			temp_update_liner_velocity_message.setZ(NO_CHANGE);
			temp_update_liner_velocity_message.setId(playerObject.getId());
			sendSingleMessage(temp_update_liner_velocity_message);
			

			tempVector3.set(playerObject.getRigidBody().getLinearVelocity());
			tempVector3.x=0;
			playerObject.getRigidBody().setLinearVelocity(tempVector3);
		}
	}

	protected void aJustPressedAction() {
		// TODO Auto-generated method stub
		//btObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, random.nextFloat(), random.nextFloat()+10 ,random.nextFloat());
//		btObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultPlayerModel,btObjectFactory.getDefaultCylinderShape(), 1, random.nextFloat(), random.nextFloat()+10 ,random.nextFloat());
//
//		btObject.setId(random.nextLong());
//		physicsWorld.addPhysicsObject(btObject);
		if(playerId!=0&&playerObject!=null){
			temp_update_liner_velocity_message.setX(vinearVelocityX*-1);
			temp_update_liner_velocity_message.setY(NO_CHANGE);
			temp_update_liner_velocity_message.setZ(NO_CHANGE);
			temp_update_liner_velocity_message.setId(playerObject.getId());
			sendSingleMessage(temp_update_liner_velocity_message);
			

			tempVector3.set(playerObject.getRigidBody().getLinearVelocity());
			tempVector3.x=vinearVelocityX*-1;
			playerObject.getRigidBody().setLinearVelocity(tempVector3);
		}
	}

	protected void aJustUppedAction() {
		// TODO Auto-generated method stub
		if(playerId!=0&&playerObject!=null){
			temp_update_liner_velocity_message.setX(0);
			temp_update_liner_velocity_message.setY(NO_CHANGE);
			temp_update_liner_velocity_message.setZ(NO_CHANGE);
			temp_update_liner_velocity_message.setId(playerObject.getId());
			sendSingleMessage(temp_update_liner_velocity_message);
			

			tempVector3.set(playerObject.getRigidBody().getLinearVelocity());
			tempVector3.x=0;
			playerObject.getRigidBody().setLinearVelocity(tempVector3);
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
		if(playerId!=0&&playerObject!=null){
			System.out.println("playerId:"+playerId);
			System.out.println("ObjectId:"+playerObject.getId());
			if(playerObject.getPosition().y<0.7f){
				temp_update_liner_velocity_message.setX(NO_CHANGE);
				temp_update_liner_velocity_message.setY(10);
				temp_update_liner_velocity_message.setZ(NO_CHANGE);
				temp_update_liner_velocity_message.setId(playerObject.getId());
				sendSingleMessage(temp_update_liner_velocity_message);
				

				tempVector3.set(playerObject.getRigidBody().getLinearVelocity());
				tempVector3.y=10;
				playerObject.getRigidBody().setLinearVelocity(tempVector3);
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
	
	void sendSingleMessage(Message message){

		clientSocket.send(SINGLE_MESSAGE.get(message.get().array()).array(), false);

	}
	void sendSingleMessage(byte[] data){
		clientSocket.send(SINGLE_MESSAGE.get(data).array(), false);
	}
	
	void initMessageHandle(){
		messageHandlerMap.put(EntityMessageType.S2C_ADD_PLAYER.ordinal(), new MessageHandler() {
			S2C_ADD_PLAYER message=new S2C_ADD_PLAYER();
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				hideGameOver();
				System.out.println("recv addplayer");
				if(physicsWorld.getPhysicsObjects().get(message.getObjectId())==null){
					RenderableBtObject btObject=physicsWorldBuilder.createDefaultRenderableBall(5,10,0);
					ColorAttribute ca=(ColorAttribute)(((RenderableBtObject)btObject).getInstance().nodes.get(0).parts.get(0).material.get(ColorAttribute.Diffuse));
					ca.color.set(0.9f, 0.2f, 0.1f, 1);
					btObject.setId(message.getObjectId());
					btObject.Attributes.put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.PLAYER.ordinal()));
					btObject.Attributes.put(AttributeType.OWNER_PLAYER_ID.ordinal(), new OwnerPlayerId(message.getId()));
					btObject.getRigidBody().setContactCallbackFilter(1<<GameObjectType.GROUND.ordinal());
					//System.out.println(1<<GameObjectType.GROUND.ordinal());
					physicsWorld.addPhysicsObject(btObject);
					if(message.getId()==playerId){
						playerObject=btObject;
					}
				}
			}
		});
		messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {
			
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				
			}
		});
		
		messageHandlerMap.put(EntityMessageType.S2C_ADD_OBSTACLE.ordinal(), new MessageHandler() {
			S2C_ADD_OBSTACLE message=new S2C_ADD_OBSTACLE();
			
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub

				message.set(src);
				if(physicsWorld.getPhysicsObjects().get(message.getId())==null){
					createObstacleQueue.add(message);
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
				BtObject btObject=(BtObject) physicsWorld.getPhysicsObjects().get(message.getId());
				if(btObject!=null){

					OwnerPlayerId ownerPlayerId=(OwnerPlayerId) btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal());
					if(ownerPlayerId!=null&&ownerPlayerId.getPlayerId()==playerId){
						//System.out.println("remove myself");
						showGameOver();
						sound.play(1,0.5f,0);

						playerId=0;
						playerObject=null;
					}
					System.out.println("remove position:"+btObject.getPosition());
					physicsWorld.removePhysicsObject(physicsWorld.getPhysicsObjects().get(message.getId()));
				}
			}
		});
		
		messageHandlerMap.put(EntityMessageType.UPDATE_BTOBJECT_MOTIONSTATE.ordinal(), new MessageHandler() {
			UPDATE_BTOBJECT_MOTIONSTATE message=new UPDATE_BTOBJECT_MOTIONSTATE();
			C2S_ENQUIRE_BTOBJECT c2s_ENQUIRE_BTOBJECT_message=new C2S_ENQUIRE_BTOBJECT();

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				BtObject btObject=(BtObject) physicsWorld.getPhysicsObjects().get(message.getId());
				if(btObject==null){
					c2s_ENQUIRE_BTOBJECT_message.setId(message.getId());
					sendSingleMessage(c2s_ENQUIRE_BTOBJECT_message);
				}else{

					if (!btObject.getRigidBody().isActive()){
						btObject.getRigidBody().activate();
					}
					if(playerObject!=null){
						if(message.getId()==playerObject.getId()){
							//System.out.println(message.getLinearVelocityX());
						}
					}
					//System.out.println("mmm:"+message.getLinearVelocityZ());
					updatePhysicsObject(btObject,message);
					//System.out.println("nnn:"+btObject.getRigidBody().getLinearVelocity().z);

				}
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
	
	public void updatePhysicsObject(BtObject btObject,UPDATE_BTOBJECT_MOTIONSTATE message){
		//btMotionState tempMotionState =new btMotionState();
	
		//btObject.getRigidBody().setMotionState(tempMotionState);
		tempMatrix4.set(message.getTransformVal());
		btObject.getRigidBody().setWorldTransform(tempMatrix4);
		//btObject.getMotionState().setWorldTransform(tempMatrix4);
		
		tempVector3.x=message.getLinearVelocityX();
		tempVector3.y=message.getLinearVelocityY();
		tempVector3.z=message.getLinearVelocityZ();
		btObject.getRigidBody().setLinearVelocity(tempVector3);
		
		tempVector3.x=message.getAngularVelocityX();
		tempVector3.y=message.getAngularVelocityY();
		tempVector3.z=message.getAngularVelocityZ();
		btObject.getRigidBody().setAngularVelocity(tempVector3);
	}
	
	void showGameOver(){
		Label console=stage.getRoot().findActor("console");
		console.setPosition(320, 230);
		console.setText("Game Over");
	}
	void hideGameOver(){
		Label console=stage.getRoot().findActor("console");
		console.setPosition(-300, 200);
	}
}

