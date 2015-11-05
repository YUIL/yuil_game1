package com.yuil.game.net.udp;

public interface UdpMessageListener {
	public void disposeUdpMessage(Session session,byte[] data);
}
