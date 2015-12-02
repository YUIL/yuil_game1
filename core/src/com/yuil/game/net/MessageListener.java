package com.yuil.game.net;


public interface MessageListener {
	public void recvMessage(Session session,byte[] data);
}
