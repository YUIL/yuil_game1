package com.yuil.game.net.message;

import io.netty.buffer.ByteBuf;

public interface MessageHandler {
	public void handle(ByteBuf src);
}
