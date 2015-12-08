package com.yuil.game.net;

import io.netty.buffer.ByteBuf;

public abstract class MessageProcessor implements Runnable {

	public Session session=null;
	public ByteBuf data;
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	
	


	public ByteBuf getData() {
		return data;
	}

	public void setData(ByteBuf data1) {
		this.data = data1;
	}

	public abstract void run() ;
}