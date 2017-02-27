package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class C2S_ADD_PLAYER implements Message {
	public final int type=EntityMessageType.C2S_ADD_PLAYER.ordinal();
	long id=0;
	
	
	public C2S_ADD_PLAYER() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public C2S_ADD_PLAYER(ByteBuf src) {
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
		buf.readByte();
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
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(8+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.id);
	
		return buf;
	}

	@Override
	public String toString() {
		return "ADD_BALL [type=" + type + ", id=" + id + "]";
	}
	
	

}
