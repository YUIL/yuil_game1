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
import com.yuil.game.entity.message.RUN_CMD;
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

public class RunCmdServer implements MessageListener {

	NetSocket netSocket;
	BroadCastor broadCastor;

	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();
	MessageProcessor messageProcessor;
	ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	public static void main(String[] args) {
		RunCmdServer runCmdServer = new RunCmdServer();
		runCmdServer.start();
	}

	public RunCmdServer() {
		
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
		netSocket.start();
	}

	
	class MessageProcessor extends com.yuil.game.net.MessageProcessor {
		public MessageProcessor() {
			messageHandlerMap.put(EntityMessageType.RUN_CMD.ordinal(), new MessageHandler() {
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub
					RUN_CMD message=new RUN_CMD(src);
					//System.out.println(message.getId());
					broadCastor.broadCast_SINGLE_MESSAGE(message, false);
	
				}
			});
			
			messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub
					System.out.println("TEST");
					//broadCastor.broadCast_SINGLE_MESSAGE(src, false);
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
