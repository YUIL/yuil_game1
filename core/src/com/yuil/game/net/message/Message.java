package com.yuil.game.net.message;

import io.netty.buffer.ByteBuf;

public interface Message{

	public static final int TYPE_LENGTH=1;
	
	public abstract Message set(ByteBuf buf);
	public abstract ByteBuf get();

}
