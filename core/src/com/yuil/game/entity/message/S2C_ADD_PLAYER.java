package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class S2C_ADD_PLAYER implements Message {
	public final int type=EntityMessageType.S2C_ADD_PLAYER.ordinal();
	long id=0;
	long objectId;
	long time=0;
	
	
	public S2C_ADD_PLAYER() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public S2C_ADD_PLAYER(ByteBuf src) {
		super();
		this.set(src);
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		this.setId(buf.readLong());
		this.setObjectId(buf.readLong());
		return this;
	}

	@Override
	public ByteBuf get() {
	/*	byte[] dest=new byte[8+4+4+4+Message.TYPE_LENGTH];		
		MessageUtil.bytesAppendType(dest, this.type);
		
		int offset=Message.TYPE_LENGTH;
		
		offset=DataUtil.appendBytes(dest, this.id, offset);
		offset=DataUtil.appendBytes(dest, this.x, offset);
		offset=DataUtil.appendBytes(dest, this.y, offset);
		offset=DataUtil.appendBytes(dest, this.z, offset);
		
		return dest;*/
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(8+8+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.id);
		buf.writeLong(this.objectId);
		return buf;
	}

	@Override
	public String toString() {
		return "ADD_BALL [type=" + type + ", id=" + id +  ", time=" + time + "]";
	}
	
	

}
