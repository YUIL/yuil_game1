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

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.CollisionJNI;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.yuil.game.entity.BtObject;
import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtObjectSpawner;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.PhysicsWorldBuilder;
import com.yuil.game.entity.attribute.Attribute;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.OwnerPlayerId;
import com.yuil.game.entity.message.*;
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
	final float NO_CHANGE=1008611;
	NetSocket netSocket;
	BroadCastor broadCastor;
	BtWorld physicsWorld = new BtWorld();
	PhysicsWorldBuilder physicsWorldBuilder=new PhysicsWorldBuilder(false);
	volatile Thread gameWorldThread;



	Queue<UPDATE_BTOBJECT_MOTIONSTATE> updatePhysicsObjectQueue=new  ConcurrentLinkedQueue<UPDATE_BTOBJECT_MOTIONSTATE>();
	//Queue<APPLY_FORCE> applyForceQueue=new  ConcurrentLinkedQueue<APPLY_FORCE>();
	
	Random random=new Random(System.currentTimeMillis());
	List<Player> playerList=new ArrayList<Player>();
	Vector3 tempVector3=new Vector3(0,0,-20);
	Matrix4 tempMatrix4=new Matrix4();
	
	//BtObjectFactory btObjectFactory = new BtObjectFactory(false);
	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();
	MessageProcessor messageProcessor;
	ExecutorService threadPool = Executors.newSingleThreadExecutor();

	ContactListener contactListener;
	REMOVE_BTOBJECT REMOVE_BTOBJECT_message=new REMOVE_BTOBJECT();
	UPDATE_BTOBJECT_MOTIONSTATE update_BTRIGIDBODY=new UPDATE_BTOBJECT_MOTIONSTATE();
	public static Queue<BtObject>  updateBtObjectMotionStateBroadCastQueue=new ConcurrentLinkedDeque<BtObject>();
	
	BtObjectSpawner obstacleBallSpawner=new BtObjectSpawner(1000) {

		@Override
		public void spawn() {
			// TODO Auto-generated method stub
			//physicsWorld.addPhysicsObjectQueue.
		}
		
		
	};
	
	public class MyContactListener extends ContactListener {
		Queue<BtObject> removeQueue=new LinkedList<BtObject>();
	    @Override
	    public void onContactStarted (btCollisionObject colObj0, btCollisionObject colObj1) {
	    	if(colObj0 instanceof btRigidBody){
	    		
	    		BtObject btObject=(BtObject)(((btRigidBody)colObj0).userData);
	    		if (btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal())!=null){
	    			System.out.println(((OwnerPlayerId)(btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal()))).getPlayerId());
	    		}
	    	}
	    	
	    	if(colObj1 instanceof btRigidBody){
	    	
	    		BtObject btObject=(BtObject)(((btRigidBody)colObj1).userData);
	    		if (btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal())!=null){
	    			System.out.println(((OwnerPlayerId)(btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal()))).getPlayerId());
	    		}
	    	}
	    	
	    	/*
	    	if(colObj0 instanceof btRigidBody&&colObj1 instanceof btRigidBody){
	    		BtObject btObject0=(BtObject)(((btRigidBody)colObj0).userData);
	    		BtObject btObject1=(BtObject)(((btRigidBody)colObj1).userData);
				if (btObject0.userData instanceof Bullet && btObject1.userData instanceof AliveObject) {
					beAttack((Bullet)btObject0.userData, btObject1);
				}
				if (btObject0.userData instanceof AliveObject && btObject1.userData instanceof Bullet) {
					beAttack((Bullet)btObject1.userData, btObject0);
				}
				
	    	}*/
	    	while(!removeQueue.isEmpty()){
				physicsWorld.removePhysicsObject(removeQueue.poll());
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
	    private boolean beAttack(Bullet bullet ,BtObject btObject){
	    	AliveObject aliveObject=(AliveObject)btObject.userData;
			if(aliveObject.getH()>bullet.getAttack()){
				aliveObject.setH(aliveObject.getH()-bullet.getAttack());
				btObjectBroadCastQueue.add(btObject);
				return false;
			}else{
				REMOVE_BTOBJECT_message.setId(btObject.getId());
				broadCastor.broadCast_SINGLE_MESSAGE(REMOVE_BTOBJECT_message.get(),false);
				removeQueue.add(btObject);
				return true;
			}
	    }*/
	}
	public static void main(String[] args) {
		BtTestServer2 btTestServer = new BtTestServer2();
		btTestServer.start();
	}

	public BtTestServer2() {
		physicsWorld.addPhysicsObject(physicsWorldBuilder.createDefaultGround());
		
		contactListener=new MyContactListener();
		
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
			nextUpdateTime=System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() >= nextUpdateTime  ) {
					
					for (Iterator iterator = playerList.iterator(); iterator.hasNext();) {
						Player player = (Player) iterator.next();
						BtObject btObject=physicsWorld.getPhysicsObjects().get(player.btObjectId);
						if (btObject!=null){
							//System.out.println(btObject.getPosition().z);
							//if(btObject.getRigidBody().getLinearVelocity().z>-0.3){
								//tempVector3.set(0,0,-20);
								//btObject.getRigidBody().applyForce(tempVector3, btObject.getPosition());
								//BtTestServer2.btObjectBroadCastQueue.add(btObject);

							//}
							if(btObject.getPosition().z<-199){
								btObject.getRigidBody().getWorldTransform(tempMatrix4);
								tempMatrix4.setTranslation(btObject.getPosition().x,btObject.getPosition().y,-20);
								btObject.getRigidBody().setWorldTransform(tempMatrix4);
								BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);
							}
							if(Math.abs(btObject.getPosition().x)>19){
								final float offset=0.01f;
								if(btObject.getRigidBody().getLinearVelocity().x>0){
									tempMatrix4.set(btObject.getTransform());
									tempMatrix4.setTranslation(19-offset, btObject.getPosition().y, btObject.getPosition().z);
								}else{
									tempMatrix4.set(btObject.getTransform());
									tempMatrix4.setTranslation(-19+offset, btObject.getPosition().y, btObject.getPosition().z);
								}
								btObject.getRigidBody().setWorldTransform(tempMatrix4);
								tempVector3.set(btObject.getRigidBody().getLinearVelocity());
								tempVector3.x=tempVector3.x*-0.5f;
								btObject.getRigidBody().setLinearVelocity(tempVector3);
								
							
								
								BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);

							}
						}
					}
					
					
					for (int i = 0; i < updatePhysicsObjectQueue.size(); i++) {
						UPDATE_BTOBJECT_MOTIONSTATE message=updatePhysicsObjectQueue.poll();
						BtObject btObject=physicsWorld.getPhysicsObjects().get(message.getId());
						if (btObject!=null){
							updatePhysicsObject(btObject,message);
						}	
					}
					
					nextUpdateTime+=interval;
					physicsWorld.update(interval/1000f);//更新物理世界
					
					//向连接的客户端发送btObject同步消息
					for (int i = 0; i < updateBtObjectMotionStateBroadCastQueue.size(); i++) {
						BtObject btObject=updateBtObjectMotionStateBroadCastQueue.poll();
						if (btObject.getRigidBody()!=null) {
							System.out.println("update");
							update_BTRIGIDBODY.set(btObject);
							System.out.println(update_BTRIGIDBODY.toString());
							broadCastor.broadCast_SINGLE_MESSAGE(update_BTRIGIDBODY, true);
						}
					}
					// broadCastor.broadCast_GAME_MESSAGE(data, false);
					
				}else{
					try {
						Thread.sleep(nextUpdateTime-System.currentTimeMillis());
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
				C2S_ADD_PLAYER message=new C2S_ADD_PLAYER();
				S2C_ADD_PLAYER s2c_ADD_PLAYER_message=new S2C_ADD_PLAYER();
				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					// TODO Auto-generated method stub
					long objectId=random.nextLong();
					s2c_ADD_PLAYER_message.setId(message.getId());
					s2c_ADD_PLAYER_message.setObjectId(objectId);
					
					BtObject btObject=physicsWorldBuilder.createDefaultBall(5,10,0);
					
					btObject.setId(objectId);
					btObject.Attributes.put(AttributeType.OWNER_PLAYER_ID.ordinal(), new OwnerPlayerId(message.getId()));
					physicsWorld.addPhysicsObject(btObject);
					
					playerList.add(new Player(message.getId(),objectId));
					
					tempVector3.set(btObject.getRigidBody().getLinearVelocity().x,btObject.getRigidBody().getLinearVelocity().y,-10f);
					btObject.getRigidBody().setLinearVelocity(tempVector3);
					
					broadCastor.broadCast_SINGLE_MESSAGE(s2c_ADD_PLAYER_message, false);
					
					//BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);
				}
			});
			messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub

				}
			});

			messageHandlerMap.put(EntityMessageType.ADD_BALL.ordinal(), new MessageHandler() {
				ADD_BALL message = new ADD_BALL();
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub
					message.set(src);
					broadCastor.broadCast_SINGLE_MESSAGE(message, false);
				}
			});
			messageHandlerMap.put(EntityMessageType.UPDATE_LINEAR_VELOCITY.ordinal(), new MessageHandler() {
				UPDATE_LINEAR_VELOCITY message=new UPDATE_LINEAR_VELOCITY();
				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					BtObject btObject=physicsWorld.getPhysicsObjects().get(message.getId());
					
					if (btObject!=null){
						tempVector3.set(btObject.getRigidBody().getLinearVelocity());
						if(message.getX()!=NO_CHANGE){
							tempVector3.x=message.getX();
						}
						if(message.getY()!=NO_CHANGE){
							tempVector3.y=message.getY();
						}
						if(message.getZ()!=NO_CHANGE){
							tempVector3.z=message.getZ();
						}
						//btObject.getRigidBody().applyForce(tempVector3, btObject.getPosition());
						
						btObject.getRigidBody().setLinearVelocity(tempVector3);
						BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);
						
					}
				}
			});
			
			messageHandlerMap.put(EntityMessageType.C2S_ENQUIRE_BTOBJECT.ordinal(), new MessageHandler() {
				C2S_ENQUIRE_BTOBJECT message =new C2S_ENQUIRE_BTOBJECT();
				S2C_ADD_PLAYER s2c_ADD_PLAYER_message=new S2C_ADD_PLAYER();
				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					BtObject btObject=physicsWorld.getPhysicsObjects().get(message.getId());
					if(btObject!=null){
						Attribute attribute =btObject.Attributes.get(AttributeType.OWNER_PLAYER_ID.ordinal());
						if(attribute!=null){
							s2c_ADD_PLAYER_message.setId(((OwnerPlayerId)attribute).getPlayerId());
							s2c_ADD_PLAYER_message.setObjectId(message.getId());
							netSocket.send(SINGLE_MESSAGE.get(message.get().array()), session, false);
						}
					}
				}
			});
			
			messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
				TEST message =new TEST();
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


	
	public void updatePhysicsObject(UPDATE_BTOBJECT_MOTIONSTATE message) {
		// TODO Auto-generated method stub
		this.updatePhysicsObjectQueue.add(message);
	}

	public void updateLinearVelocity(UPDATE_LINEAR_VELOCITY message) {
		// TODO Auto-generated method stub
	
	}
	
	public void updatePhysicsObject(BtObject btObject,UPDATE_BTOBJECT_MOTIONSTATE message){
		tempMatrix4.set(message.getTransformVal());
		btObject.getRigidBody().setWorldTransform(tempMatrix4);
		
		tempVector3.x=message.getLinearVelocityX();
		tempVector3.y=message.getLinearVelocityY();
		tempVector3.z=message.getLinearVelocityZ();
		btObject.getRigidBody().setLinearVelocity(tempVector3);
		
		tempVector3.x=message.getAngularVelocityX();
		tempVector3.y=message.getAngularVelocityY();
		tempVector3.z=message.getAngularVelocityZ();
		btObject.getRigidBody().setAngularVelocity(tempVector3);
	}
}
