package com.yuil.game.entity.message;

import com.yuil.game.net.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class ADD_BTOBJECT implements Message {
	public final int type=EntityMessageType.ADD_BTOBJECT.ordinal();
	long id;
	int btObjectType;
	
	public ADD_BTOBJECT() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ADD_BTOBJECT(ByteBuf src) {
		super();
		this.set(src);
		// TODO Auto-generated constructor stub
	}
	@Override
	public Message set(ByteBuf buf) {
		buf.readByte();
		this.setId(buf.readLong());
		this.setBtObjectType(buf.readInt());
		return this;
	}

	@Override
	public ByteBuf get() {
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(8+4+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.id);
		buf.writeFloat(this.btObjectType);
	
		return buf;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBtObjectType() {
		return btObjectType;
	}

	public void setBtObjectType(int btObjectType) {
		this.btObjectType = btObjectType;
	}

	public int getType() {
		return type;
	}

	

}
