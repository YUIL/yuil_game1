package com.yuil.game.server;

import java.net.BindException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.physics.bullet.collision.CollisionJNI;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.yuil.game.entity.BtObject;
import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.message.ADD_BALL;
import com.yuil.game.entity.message.APPLY_FORCE;
import com.yuil.game.entity.message.EntityMessageType;
import com.yuil.game.entity.message.REMOVE_BTOBJECT;
import com.yuil.game.entity.message.TEST;
import com.yuil.game.entity.message.UPDATE_BTOBJECT_MOTIONSTATE;
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

public class BtTestServer implements MessageListener {

	NetSocket netSocket;
	BroadCastor broadCastor;
	BtWorld physicsWorld = new BtWorld();
	volatile Thread gameWorldThread;

	BtObjectFactory btObjectFactory = new BtObjectFactory(false);
	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();
	MessageProcessor messageProcessor;
	ExecutorService threadPool = Executors.newSingleThreadExecutor();

	ContactListener contactListener;
	REMOVE_BTOBJECT REMOVE_BTOBJECT_message=new REMOVE_BTOBJECT();
	UPDATE_BTOBJECT_MOTIONSTATE update_BTRIGIDBODY=new UPDATE_BTOBJECT_MOTIONSTATE();
	public static Queue<BtObject>  btObjectBroadCastQueue=new ConcurrentLinkedDeque<BtObject>();
	
	public static void main(String[] args) {
		BtTestServer btTestServer = new BtTestServer();
		btTestServer.start();
	}

	public BtTestServer() {
		physicsWorld.addPhysicsObject(btObjectFactory.createGround());
		
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

		int interval = 50;
		long nextUpdateTime = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			nextUpdateTime=System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() >= nextUpdateTime  ) {
					nextUpdateTime+=interval;
					physicsWorld.update(interval/1000f);
					for (int i = 0; i < btObjectBroadCastQueue.size(); i++) {
						BtObject btObject=btObjectBroadCastQueue.poll();
						
						if (btObject.getRigidBody()!=null) {
							update_BTRIGIDBODY.set(btObject);
							broadCastor.broadCast_SINGLE_MESSAGE(update_BTRIGIDBODY, false);

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
					BtObject btObject=btObjectFactory.createBtObject(btObjectFactory.getDefaultSphereShape(),1, message.getX(), message.getY(), message.getZ());
					btObject.setId(message.getId());
					physicsWorld.addPhysicsObject(btObject);
					broadCastor.broadCast_SINGLE_MESSAGE(message, false);
				}
			});
			messageHandlerMap.put(EntityMessageType.APPLY_FORCE.ordinal(), new MessageHandler() {
				APPLY_FORCE message=new APPLY_FORCE();
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub
					System.out.println("apppp");
					message.set(src);
					//physicsWorld.applyForce(message);					
					
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

}
