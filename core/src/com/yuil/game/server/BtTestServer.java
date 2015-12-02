package com.yuil.game.server;

import java.net.BindException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.message.ADD_BALL;
import com.yuil.game.entity.message.EntityMessageType;
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.udp.UdpSocket;
import com.yuil.game.util.Log;

public class BtTestServer implements MessageListener{

	NetSocket netSocket;
	BroadCastor broadCastor;
	BtWorld physicsWorld=new BtWorld();
	volatile Thread gameWorldThread;
	
	BtObjectFactory btObjectFactory=new BtObjectFactory(false);
	Map<Integer, MessageHandler> messageHandlerMap=new HashMap<Integer, MessageHandler>();
	MessageProcessor messageProcessor;
	ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	public static void main(String[] args) {
		BtTestServer btTestServer=new BtTestServer();
		btTestServer.start();
	}

	
	public BtTestServer(){
		physicsWorld.addPhysicsObject(btObjectFactory.createGround());
		try {
			netSocket=new UdpSocket(9091);
		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		netSocket.setMessageListener(this);
		broadCastor=new BroadCastor(netSocket);
		messageProcessor=new MessageProcessor();
	}
	public void start(){
		Log.println("start");
		gameWorldThread=new Thread(new WorldLogic());
		gameWorldThread.start();
		netSocket.start();
	}
	
	class WorldLogic implements Runnable{

		int interval=100;
		long lastUpdateTime=0;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				if (System.currentTimeMillis()-lastUpdateTime>interval) {
					lastUpdateTime=System.currentTimeMillis();
					physicsWorld.update(interval);
					//broadCastor.broadCast_GAME_MESSAGE(data, false);
				}
			}
		}
		
	}
	
	class MessageProcessor extends com.yuil.game.net.MessageProcessor{
		public  MessageProcessor() {
			initMessageHandle();
		}
		@Override
		public void run() {
			int typeOrdinal = MessageUtil.getType(data);
			System.out.println("type:" + EntityMessageType.values()[typeOrdinal]);
			byte[] src =MessageUtil.getMessageBytes(data);
		
			messageHandlerMap.get(typeOrdinal).handle(src);
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
					physicsWorld.addPhysicsObject(btObjectFactory.createBtObject(btObjectFactory.getDefaultSphereShape(), 1, message.getX(), message.getY(), message.getZ()));
					broadCastor.broadCast_SINGLE_MESSAGE(message, false);
				}
			});

		}
	}

	@Override
	public void recvMessage(Session session, byte[] data) {

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
		messageProcessor.setSession(session);
		messageProcessor.setData(data);
		threadPool.execute(messageProcessor);
	}
	
	
}
