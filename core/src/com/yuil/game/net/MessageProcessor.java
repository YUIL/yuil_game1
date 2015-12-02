package com.yuil.game.net;

public abstract class MessageProcessor implements Runnable {

	public Session session=null;
	public byte[] data;
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	
	


	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public abstract void run() ;
}