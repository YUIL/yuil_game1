package com.yuil.game.net;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.Collection;

public interface NetSocket extends Closeable {

	public void start();
	public boolean send(byte[] data, Session session, boolean isImmediately);
	public Collection<? extends Session> getSessions();
	public void setMessageListener(MessageListener messageListener);
	public Session createSession(long sessionId, InetSocketAddress address);
}
