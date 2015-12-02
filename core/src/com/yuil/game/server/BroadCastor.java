package com.yuil.game.server;

import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.SINGLE_MESSAGE;

public class BroadCastor {
	NetSocket netSocket;
	public BroadCastor(NetSocket netSocket){
		this.netSocket=netSocket;
	}

	public  void broadCast_SINGLE_MESSAGE(Message message, boolean isImmediately) {
		broadCast(SINGLE_MESSAGE.getBytes(message.toBytes()), isImmediately);
	}
	
	public  void broadCast_SINGLE_MESSAGE(byte[] data, boolean isImmediately) {
		broadCast(SINGLE_MESSAGE.getBytes(data), isImmediately);
	}

	public  void broadCast_MESSAGE_ARRAY(Message[] messages, boolean isImmediately) {
		broadCast(new MESSAGE_ARRAY(messages).toBytes(), isImmediately);
	}
	
	public  void broadCast(byte[] bytes, boolean isImmediately) {
		for (Session session:netSocket.getSessions()) {
			netSocket.send(bytes, session, isImmediately);
		}
	}
}
