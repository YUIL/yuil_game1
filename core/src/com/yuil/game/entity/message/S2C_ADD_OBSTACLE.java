package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class S2C_ADD_OBSTACLE implements Message {
	public final int type=EntityMessageType.S2C_ADD_OBSTACLE.ordinal();
	long id=0;
	float radius;
	long time=0;
	float r;
	float g;
	float b;
	float a;
	
	public S2C_ADD_OBSTACLE() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public S2C_ADD_OBSTACLE(ByteBuf src) {
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

	

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	
	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	
	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getType() {
		return type;
	}

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		this.setId(buf.readLong());
		this.setRadius(buf.readFloat());
		this.setR(buf.readFloat());
		this.setG(buf.readFloat());
		this.setB(buf.readFloat());
		this.setA(buf.readFloat());
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
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(8+4+4+4+4+4+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.id);
		buf.writeFloat(this.radius);
		buf.writeFloat(this.r);
		buf.writeFloat(this.g);
		buf.writeFloat(this.b);
		buf.writeFloat(this.a);
		return buf;
	}

	@Override
	public String toString() {
		return "S2C_ADD_OBSTACLE [type=" + type + ", id=" + id +  ", time=" + time + "]";
	}
	
	

}
