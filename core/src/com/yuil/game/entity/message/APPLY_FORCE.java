package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class APPLY_FORCE implements Message {
	public final int type=EntityMessageType.APPLY_FORCE.ordinal();
	long id;
	float x;
	float y;
	float z;
	
	long time=0;
	
	
	public APPLY_FORCE() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public APPLY_FORCE(ByteBuf src) {
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

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		this.setId(buf.readLong());
		this.setX(buf.readFloat());
		this.setY(buf.readFloat());
		this.setZ(buf.readFloat());
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
		buf.writeFloat(this.x);
		buf.writeFloat(this.y);
		buf.writeFloat(this.z);
	
		return buf;
	}

	@Override
	public String toString() {
		return "ADD_BALL [type=" + type + ", id=" + id + ", x=" + x + ", y=" + y + ", z=" + z + ", time=" + time + "]";
	}
	
	

}
