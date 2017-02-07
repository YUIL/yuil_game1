package com.yuil.game.server;

import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.SINGLE_MESSAGE;

import io.netty.buffer.ByteBuf;

public class BroadCastor {
	NetSocket netSocket;
	public BroadCastor(NetSocket netSocket){
		this.netSocket=netSocket;
	}

	
	public  void broadCast_SINGLE_MESSAGE(Message message, boolean isImmediately) {
		broadCast(SINGLE_MESSAGE.get(message.get().array()), isImmediately);
	}
	public  void broadCast_SINGLE_MESSAGE(ByteBuf message, boolean isImmediately) {
		broadCast(SINGLE_MESSAGE.get(message.array()), isImmediately);
		message.release();
	}
	
	public  void broadCast_SINGLE_MESSAGE(byte[] data, boolean isImmediately) {
		broadCast(SINGLE_MESSAGE.get(data), isImmediately);
	}

	public  void broadCast_MESSAGE_ARRAY(Message[] messages, boolean isImmediately) {
		broadCast(new MESSAGE_ARRAY(messages).get(), isImmediately);
	}
	
	public  void broadCast(ByteBuf data, boolean isImmediately) {
		System.out.println("broadcast");

		for (Session session:netSocket.getSessions()) {
			data.retain();
			netSocket.send(data, session, isImmediately);
		}
		if(netSocket.getSessions().size()>1){
			data.release();
		}
	}
}
