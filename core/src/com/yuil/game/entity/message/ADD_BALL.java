package com.yuil.game.entity.message;

import java.nio.ByteBuffer;

import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.util.DataUtil;

public class ADD_BALL implements Message {
	public final int type=EntityMessageType.ADD_BALL.ordinal();
	long id;
	float x;
	float y;
	float z;
	
	long time=0;
	
	
	public ADD_BALL() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ADD_BALL(byte[] src) {
		super();
		this.init(src);
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
	public void init(byte[] src) {
		// TODO Auto-generated method stub
		int offset=0;
		this.setId(DataUtil.bytesToLong(DataUtil.subByte(src, 8, offset)));offset+=8;
		this.setX(DataUtil.bytesToFloat(DataUtil.subByte(src, 4, offset)));offset+=4;
		this.setY(DataUtil.bytesToFloat(DataUtil.subByte(src, 4, offset)));offset+=4;
		this.setZ(DataUtil.bytesToFloat(DataUtil.subByte(src, 4, offset)));
	}

	@Override
	public byte[] toBytes() {
		byte[] dest=new byte[8+4+4+4+Message.TYPE_LENGTH];		
		MessageUtil.bytesAppendType(dest, this.type);
		
		int offset=Message.TYPE_LENGTH;
		
		offset=DataUtil.appendBytes(dest, this.id, offset);
		offset=DataUtil.appendBytes(dest, this.x, offset);
		offset=DataUtil.appendBytes(dest, this.y, offset);
		offset=DataUtil.appendBytes(dest, this.z, offset);
		
		return dest;
		
		
	/*	ByteBuffer byteBuffer=ByteBuffer.allocate(8+4+4+4+Message.TYPE_LENGTH);
		byteBuffer.put((byte)this.type);
		byteBuffer.putLong(this.id);
		byteBuffer.putFloat(this.x);
		byteBuffer.putFloat(this.y);
		byteBuffer.putFloat(this.z);
	
		return byteBuffer.array();*/
	}

}
