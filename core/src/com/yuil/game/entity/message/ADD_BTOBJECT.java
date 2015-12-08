package com.yuil.game.entity.message;

import com.yuil.game.net.message.Message;

import io.netty.buffer.ByteBuf;

public class ADD_BTOBJECT implements Message {
	public final int type=EntityMessageType.ADD_BTOBJECT.ordinal();

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public ByteBuf get() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
