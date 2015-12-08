package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class REMOVE_BTOBJECT implements Message {
	public final int type=EntityMessageType.REMOVE_BTOBJECT.ordinal();
	long id;
	
	long time=0;
	
	
	public REMOVE_BTOBJECT() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public REMOVE_BTOBJECT(ByteBuf src) {
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


	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		this.setId(buf.readLong());
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
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(8+4+4+4+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.id);
	
		return buf;
	}

	
	

}
