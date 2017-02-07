package com.yuil.game.entity.message;

import com.yuil.game.net.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class TEST implements Message {
	public final int type = EntityMessageType.TEST.ordinal();

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public ByteBuf get() {
		ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		return buf;
	}

}
