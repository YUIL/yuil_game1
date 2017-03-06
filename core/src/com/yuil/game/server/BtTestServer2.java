package com.yuil.game.server;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.CollisionJNI;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.yuil.game.entity.attribute.Attribute;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.DamagePoint;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.attribute.HealthPoint;
import com.yuil.game.entity.attribute.OwnerPlayerId;
import com.yuil.game.entity.gameobject.GameObjectType;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.physics.BtObject;
import com.yuil.game.entity.physics.BtObjectFactory;
import com.yuil.game.entity.physics.BtObjectSpawner;
import com.yuil.game.entity.physics.BtWorld;
import com.yuil.game.entity.physics.PhysicsWorldBuilder;
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.UdpSocket;
import com.yuil.game.util.Log;

import io.netty.buffer.ByteBuf;

public class BtTestServer2 implements MessageListener {
	final float NO_CHANGE = 1008611;
	NetSocket netSocket;
	BroadCastor broadCastor;
	BtWorld physicsWorld = new BtWorld();
	PhysicsWorldBuilder physicsWorldBuilder = new PhysicsWorldBuilder(false);
	volatile Thread gameWorldThread;

	// Queue<UPDATE_BTOBJECT_MOTIONSTATE> updatePhysicsObjectQueue=new
	// ConcurrentLinkedQueue<UPDATE_BTOBJECT_MOTIONSTATE>();
	// Queue<APPLY_FORCE> applyForceQueue=new
	// ConcurrentLinkedQueue<APPLY_FORCE>();

	Random random = new Random(System.currentTimeMillis());
	List<Player> playerList = new ArrayList<Player>();
	List<BtObject> obstacleBtObjectList = new LinkedList<BtObject>();

	Vector3 tempVector3 = new Vector3(0, 0, -40);
	Matrix4 tempMatrix4 = new Matrix4();

	// BtObjectFactory btObjectFactory = new BtObjectFactory(false);
	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();
	MessageProcessor messageProcessor;
	ExecutorService threadPool = Executors.newSingleThreadExecutor();

	ContactListener contactListener;
	REMOVE_BTOBJECT remove_BTOBJECT_message = new REMOVE_BTOBJECT();
	UPDATE_BTOBJECT_MOTIONSTATE update_BTRIGIDBODY = new UPDATE_BTOBJECT_MOTIONSTATE();
	public static Queue<BtObject> updateBtObjectMotionStateBroadCastQueue = new ConcurrentLinkedDeque<BtObject>();
	public static Queue<BtObject> removeBtObjectQueue = new ConcurrentLinkedDeque<BtObject>();
	public static Queue<BtObject> contactPlayerQueue = new ConcurrentLinkedDeque<BtObject>();

	
	
	BtObjectSpawner obstacleBallSpawner = new BtObjectSpawner(200) {
		Vector3 v3 = new Vector3();
		Color color = new Color();
		S2C_ADD_OBSTACLE message = new S2C_ADD_OBSTACLE();

		@Override
		public void spawn() {
			// TODO Auto-generated method stub
			// physicsWorld.addPhysicsObjectQueue.
			v3.x = -18 + random.nextInt(36);
			v3.y = 10+random.nextInt(50);
			v3.z = -200;
			//float radius = (random.nextInt(10000) / 10000f) * 3;
			float radius = 0.5f+((random.nextInt(10000) / 10000f) * 3);
			BtObject btObject = physicsWorldBuilder.createBall(radius, radius, v3);
			btObject.Attributes.put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.OBSTACLE.ordinal()));
			btObject.Attributes.put(AttributeType.DAMAGE_POINT.ordinal(), new DamagePoint(1));
			color.set(random.nextInt(255) / 255f, random.nextInt(255) / 255f, random.nextInt(255) / 255f, 1);
			btObject.Attributes.put(AttributeType.COLOR.ordinal(), new com.yuil.game.entity.attribute.Color(color));
			
			btObject.getRigidBody().setCollisionFlags((1<<GameObjectType.OBSTACLE.ordinal()));
			btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.PLAYER.ordinal()));

			v3.x = 0;
			v3.y = 0;
			v3.z = 40;
			btObject.getRigidBody().setLinearVelocity(v3);

			long id = random.nextLong();
			btObject.setId(id);
			physicsWorld.addPhysicsObject(btObject);

			message.setId(id);
			message.setRadius(radius);
			message.setR(color.r);
			message.setG(color.g);
			message.setB(color.b);
			message.setA(color.a);
			broadCastor.broadCast_SINGLE_MESSAGE(message, true);
			obstacleBtObjectList.add(btObject);
		}

	};

	public class MyContactListener extends ContactListener {
		Vector3 v3 = new Vector3();
		REMOVE_BTOBJECT remove_BTOBJECT_message=new REMOVE_BTOBJECT();
		
		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody ) {
				BtObject btObject0=(BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1=(BtObject) (((btRigidBody) colObj1).userData);
				
				GameObjectTypeAttribute gameObjectType0=(GameObjectTypeAttribute)(btObject0.Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
				GameObjectTypeAttribute gameObjectType1=(GameObjectTypeAttribute)(btObject1.Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));

				if (gameObjectType0!=null&&gameObjectType1!=null) {
					if(gameObjectType0.getGameObjectType()==GameObjectType.PLAYER.ordinal()&&gameObjectType1.getGameObjectType()==GameObjectType.OBSTACLE.ordinal()){
						HealthPoint healthPoint=((HealthPoint)(btObject0.Attributes.get(AttributeType.HEALTH_POINT.ordinal())));
						int demage=(int) Math.floor((btObject1.getRigidBody().getCollisionShape().getLocalScaling().x*10));
						healthPoint.setHealthPoint(healthPoint.getHealthPoint()-demage);
						//System.out.println("剩余生命："+healthPoint.getHealthPoint());

						if(healthPoint.getHealthPoint()<=0){
							removeBtObjectQueue.add(btObject0);

						}
						//contactPlayerQueue.add(btObject0);
						removeBtObjectQueue.add(btObject1);
					}else if(gameObjectType0.getGameObjectType()==GameObjectType.OBSTACLE.ordinal()&&gameObjectType1.getGameObjectType()==GameObjectType.PLAYER.ordinal()){
						HealthPoint healthPoint=((HealthPoint)(btObject1.Attributes.get(AttributeType.HEALTH_POINT.ordinal())));
						int demage=(int) Math.floor((btObject0.getRigidBody().getCollisionShape().getLocalScaling().x*10));
						healthPoint.setHealthPoint(healthPoint.getHealthPoint()-demage);
						//System.out.println("剩余生命："+healthPoint.getHealthPoint());
						
						if(healthPoint.getHealthPoint()<=0){
							removeBtObjectQueue.add(btObject1);

						}
						removeBtObjectQueue.add(btObject0);
					}
				}
				
				handleBtObject(btObject0);
				handleBtObject(btObject1);
				
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
				// System.out.println(((OwnerPlayerId)(btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal()))).getPlayerId());
				//contactPlayerQueue.add(btObject);

			}
		}
		
		public void onContactProcessed(btCollisionObject colObj0, btCollisionObject colObj1) {
		}

		public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
		}

		/**
		 * @param bullet
		 * @param btObject
		 * @return 是否删除
		 *//*
			 * private boolean beAttack(Bullet bullet ,BtObject btObject){
			 * AliveObject aliveObject=(AliveObject)btObject.userData;
			 * if(aliveObject.getH()>bullet.getAttack()){
			 * aliveObject.setH(aliveObject.getH()-bullet.getAttack());
			 * btObjectBroadCastQueue.add(btObject); return false; }else{
			 * REMOVE_BTOBJECT_message.setId(btObject.getId());
			 * broadCastor.broadCast_SINGLE_MESSAGE(REMOVE_BTOBJECT_message.get(
			 * ),false); removeQueue.add(btObject); return true; } }
			 */
	}

	public static void main(String[] args) {
		BtTestServer2 btTestServer = new BtTestServer2();
		btTestServer.start();
	}

	public BtTestServer2() {
		physicsWorld.addPhysicsObject(physicsWorldBuilder.createDefaultGround());

		contactListener = new MyContactListener();

		try {
			netSocket = new UdpSocket(9091);
		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		netSocket.setMessageListener(this);
		broadCastor = new BroadCastor(netSocket);
		messageProcessor = new MessageProcessor();
	}

	public void start() {
		Log.println("start");
		gameWorldThread = new Thread(new WorldLogic());
		gameWorldThread.start();
		netSocket.start();
	}

	class WorldLogic implements Runnable {

		int interval = 17;
		long nextUpdateTime = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			nextUpdateTime = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() >= nextUpdateTime) {
					obstacleBallSpawner.update();// 刷障碍物体

					while (!removeBtObjectQueue.isEmpty()) {
						BtObject btObject=removeBtObjectQueue.poll();
						if(btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal())!=null){
							System.out.println("remove a player!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						}
						physicsWorld.removePhysicsObject(btObject);
						remove_BTOBJECT_message.setId(btObject.getId());
						broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
						
					}
					
					for (BtObject btObject : physicsWorld.getPhysicsObjects().values()) {
						// System.out.println(btObject.rigidBody.getWorldTransform());

						btObject.getRigidBody().getWorldTransform().getTranslation(tempVector3);
						if (tempVector3.y < -20) {// 所有物体的死亡高度判断

							if(btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal())!=null){
								System.out.println("remove a player!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							}
							physicsWorld.removePhysicsObject(btObject);
							remove_BTOBJECT_message.setId(btObject.getId());
							broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
						} else if (((GameObjectTypeAttribute)(btObject.Attributes.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()))).getGameObjectType() ==GameObjectType.OBSTACLE.ordinal()) {
							// 检查障碍物位置,超过边界则删除
							if (tempVector3.z > -45) {
								physicsWorld.removePhysicsObject(btObject);
								remove_BTOBJECT_message.setId(btObject.getId());
								broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
							}
						}else if (btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal()) != null){
							// 检查playerObjects位置，超过边界返回起始点
							//System.out.println(btObject.getPosition()+" Z:"+tempVector3.z);
							
							/*
							if (tempVector3.z < -199) {
								btObject.getRigidBody().getWorldTransform(tempMatrix4);
								tempMatrix4.setTranslation(btObject.getPosition().x, btObject.getPosition().y, -20);
								btObject.getRigidBody().setWorldTransform(tempMatrix4);
								BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);
							}*/
							btObject.getRigidBody().getWorldTransform().getTranslation(tempVector3);

							if (tempVector3.z>-50) {
								if (!btObject.getRigidBody().isActive()){
									btObject.getRigidBody().activate();
								}
								tempVector3.set(btObject.getRigidBody().getLinearVelocity());
								tempVector3.z = -5;
								btObject.getRigidBody().setLinearVelocity(tempVector3);
								updateBtObjectMotionStateBroadCastQueue.add(btObject);
							}else{

								

								btObject.getRigidBody().getWorldTransform(tempMatrix4);
								tempMatrix4.setTranslation(btObject.getPosition().x, btObject.getPosition().y, -50);
								btObject.getRigidBody().setWorldTransform(tempMatrix4);

								if (!btObject.getRigidBody().isActive()){
									btObject.getRigidBody().activate();
								}
								tempVector3.set(btObject.getRigidBody().getLinearVelocity());
								tempVector3.z = 0;
								btObject.getRigidBody().setLinearVelocity(tempVector3);
								updateBtObjectMotionStateBroadCastQueue.add(btObject);
							}
						}
					}/*
					while(!contactPlayerQueue.isEmpty()){
						BtObject btObject=contactPlayerQueue.poll();
						
					}*/
					
					
					nextUpdateTime += interval;
					physicsWorld.update(interval / 1000f);// 更新物理世界

					// 向连接的客户端发送btObjectMotionstate同步消息
					for (int i = 0; i < updateBtObjectMotionStateBroadCastQueue.size(); i++) {
						BtObject btObject = updateBtObjectMotionStateBroadCastQueue.poll();
						if (btObject.getRigidBody() != null) {
							//System.out.println("btId:"+btObject.getId());

							//System.out.println("send up velocity:"+btObject.getRigidBody().getLinearVelocity());
							// System.out.println("update");
							update_BTRIGIDBODY.set(btObject);
							// System.out.println(update_BTRIGIDBODY.toString());
							broadCastor.broadCast_SINGLE_MESSAGE(update_BTRIGIDBODY, true);
						}
					}
					// broadCastor.broadCast_GAME_MESSAGE(data, false);

				} else {
					try {
						Thread.sleep(nextUpdateTime - System.currentTimeMillis());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	class MessageProcessor extends com.yuil.game.net.MessageProcessor {
		public MessageProcessor() {
			messageHandlerMap.put(EntityMessageType.C2S_ADD_PLAYER.ordinal(), new MessageHandler() {
				C2S_ADD_PLAYER message = new C2S_ADD_PLAYER();
				S2C_ADD_PLAYER s2c_ADD_PLAYER_message = new S2C_ADD_PLAYER();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					// TODO Auto-generated method stub
					long objectId = random.nextLong();
					s2c_ADD_PLAYER_message.setId(message.getId());
					s2c_ADD_PLAYER_message.setObjectId(objectId);

					BtObject btObject = physicsWorldBuilder.createDefaultBall(5, 10, 0);

					btObject.setId(objectId);
					btObject.Attributes.put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.PLAYER.ordinal()));
					btObject.Attributes.put(AttributeType.OWNER_PLAYER_ID.ordinal(),new OwnerPlayerId(message.getId()));
					btObject.Attributes.put(AttributeType.HEALTH_POINT.ordinal(),new HealthPoint(100));
					btObject.getRigidBody().setCollisionFlags(1<<GameObjectType.PLAYER.ordinal());
					btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.OBSTACLE.ordinal()));
					//System.out.println("asd:"+((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.OBSTACLE.ordinal())));
					
					physicsWorld.addPhysicsObject(btObject);

					playerList.add(new Player(message.getId(), objectId));

					tempVector3.set(btObject.getRigidBody().getLinearVelocity().x,
							btObject.getRigidBody().getLinearVelocity().y, -5);
					btObject.getRigidBody().setLinearVelocity(tempVector3);

					broadCastor.broadCast_SINGLE_MESSAGE(s2c_ADD_PLAYER_message, false);
					updateBtObjectMotionStateBroadCastQueue.add(btObject);

					// BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);
				}
			});
			messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub

				}
			});

			messageHandlerMap.put(EntityMessageType.C2S_UPDATE_BTOBJECT_MOTIONSTATE.ordinal(), new MessageHandler() {
				C2S_UPDATE_BTOBJECT_MOTIONSTATE message = new C2S_UPDATE_BTOBJECT_MOTIONSTATE();
				UPDATE_BTOBJECT_MOTIONSTATE update_BTOBJECT_MOTIONSTATE_message = new UPDATE_BTOBJECT_MOTIONSTATE();

				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());
					if (btObject != null) {
						//System.out.println("uuuuu");
						update_BTOBJECT_MOTIONSTATE_message.set(btObject);
						netSocket.send(SINGLE_MESSAGE.get(update_BTOBJECT_MOTIONSTATE_message.get().array()), session,
								false);
					}
				}
			});
			messageHandlerMap.put(EntityMessageType.UPDATE_LINEAR_VELOCITY.ordinal(), new MessageHandler() {
				UPDATE_LINEAR_VELOCITY message = new UPDATE_LINEAR_VELOCITY();
				Vector3 v3=new Vector3();
				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());

					if (btObject != null) {
						
						v3.set(btObject.getRigidBody().getLinearVelocity());
						if (message.getX() != NO_CHANGE) {
							v3.x = message.getX();
						}
						if (message.getY() != NO_CHANGE) {
							v3.y = message.getY();
						}
						if (message.getZ() != NO_CHANGE) {
							v3.z = message.getZ();
						}
					//	System.out.println("btId:"+btObject.getId());
						//System.out.println("v3:"+v3);
						//System.out.println("position:"+btObject.getPosition());
						// btObject.getRigidBody().applyForce(tempVector3,
						// btObject.getPosition());
						if (!btObject.getRigidBody().isActive()){
							btObject.getRigidBody().activate();
						}
						
						btObject.getRigidBody().setLinearVelocity(v3);
						BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);

					}
				}
			});

			messageHandlerMap.put(EntityMessageType.C2S_ENQUIRE_BTOBJECT.ordinal(), new MessageHandler() {
				C2S_ENQUIRE_BTOBJECT message = new C2S_ENQUIRE_BTOBJECT();
				S2C_ADD_PLAYER s2c_ADD_PLAYER_message = new S2C_ADD_PLAYER();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());
					if (btObject != null) {
						Attribute attribute = btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal());
						if (attribute != null) {
							s2c_ADD_PLAYER_message.setId(((OwnerPlayerId) attribute).getPlayerId());
							s2c_ADD_PLAYER_message.setObjectId(message.getId());
							netSocket.send(SINGLE_MESSAGE.get(s2c_ADD_PLAYER_message.get().array()), session, false);
						}
					}
				}
			});

			messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
				TEST message = new TEST();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);

					netSocket.send(SINGLE_MESSAGE.get(message.get().array()), session, false);
				}
			});
		}

		@Override
		public void run() {
			int typeOrdinal = MessageUtil.getType(data.array());
			// System.out.println("type:" +
			// EntityMessageType.values()[typeOrdinal]);
			messageHandlerMap.get(typeOrdinal).handle(data);
		}
	}

	@Override
	public void recvMessage(Session session, ByteBuf data) {

		if (data.array().length < Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		// System.out.println("type:" + MessageType.values()[typeOrdinal]);

		switch (MessageType.values()[typeOrdinal]) {
		case MESSAGE_ARRAY:
			MESSAGE_ARRAY message_ARRAY = new MESSAGE_ARRAY(data);
			for (ByteBuf data1 : message_ARRAY.gameMessages) {
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

	void disposeSingleMessage(Session session, ByteBuf data1) {
		messageProcessor.setSession(session);
		messageProcessor.setData(data1);
		threadPool.execute(messageProcessor);
	}

	/*
	 * public void updatePhysicsObject(UPDATE_BTOBJECT_MOTIONSTATE message) { //
	 * TODO Auto-generated method stub
	 * this.updatePhysicsObjectQueue.add(message); }
	 */
	public void updateLinearVelocity(UPDATE_LINEAR_VELOCITY message) {
		// TODO Auto-generated method stub

	}

	/*
	 * public void updatePhysicsObject(BtObject
	 * btObject,UPDATE_BTOBJECT_MOTIONSTATE message){
	 * tempMatrix4.set(message.getTransformVal());
	 * btObject.getRigidBody().setWorldTransform(tempMatrix4);
	 * 
	 * tempVector3.x=message.getLinearVelocityX();
	 * tempVector3.y=message.getLinearVelocityY();
	 * tempVector3.z=message.getLinearVelocityZ();
	 * btObject.getRigidBody().setLinearVelocity(tempVector3);
	 * 
	 * tempVector3.x=message.getAngularVelocityX();
	 * tempVector3.y=message.getAngularVelocityY();
	 * tempVector3.z=message.getAngularVelocityZ();
	 * btObject.getRigidBody().setAngularVelocity(tempVector3); }
	 */
}
