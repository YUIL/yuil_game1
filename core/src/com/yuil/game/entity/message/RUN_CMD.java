package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class RUN_CMD implements Message {
	public final int type=EntityMessageType.RUN_CMD.ordinal();
	int id;
	
	long time=0;
	
	
	public RUN_CMD() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public RUN_CMD(int id) {
		super();
		this.setId(id);

		// TODO Auto-generated constructor stub
	}
	
	public RUN_CMD(ByteBuf src) {
		super();
		this.set(src);
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		this.setId(buf.readInt());
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
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(4+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeInt(this.id);
	
		return buf;
	}

	@Override
	public String toString() {
		return "ADD_BALL [type=" + type + ", id=" + id + ", time=" + time + "]";
	}
	
	

}
