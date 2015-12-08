package com.yuil.game.net;

import io.netty.buffer.ByteBuf;

public interface MessageListener {
	public void recvMessage(Session session,ByteBuf buf);
}
