package com.yuil.game.net.message;

import com.yuil.game.util.DataUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class SINGLE_MESSAGE implements Message{
	public final int type=MessageType.SINGLE_MESSAGE.ordinal();
	
	public byte[] data;
	
	public SINGLE_MESSAGE() {
		super();
	}
	
	public SINGLE_MESSAGE(ByteBuf buf) {
		super();
		this.set(buf);
	}


	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		buf.discardReadBytes();
		this.data=buf.array();
		return this;
	}

	@Override
	public ByteBuf get() {
		return get(this.data);
	}
	public static ByteBuf get(byte[] data){
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(data.length+Message.TYPE_LENGTH);
		buf.writeByte(MessageType.SINGLE_MESSAGE.ordinal());
		buf.writeBytes(data);
		return buf;
		
	}
}
