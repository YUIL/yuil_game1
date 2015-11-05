package com.yuil.game.net.message;

import com.yuil.game.util.DataUtil;

public class SINGLE_MESSAGE implements Message{
	public final int type=MessageType.SINGLE_MESSAGE.ordinal();
	
	public byte[] data;
	
	public SINGLE_MESSAGE() {
		super();
	}
	
	public SINGLE_MESSAGE(byte[] src) {
		super();
		this.init(src);
	}


	@Override
	public void init(byte[] src) {
		// TODO Auto-generated method stub
		this.data=src;
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		int offset=0;
		byte[] dest=new byte[data.length+Message.TYPE_LENGTH];
		byte[] src=DataUtil.intToBytes(this.type);
		System.arraycopy(src, 0, dest, offset, Message.TYPE_LENGTH);offset+=Message.TYPE_LENGTH;
		src=data;
		System.arraycopy(src, 0, dest, offset, src.length);	
		return dest;
	}
	public static byte[] getBytes(byte[] data){
		int offset=0;
		byte[] dest=new byte[data.length+Message.TYPE_LENGTH];
		byte[] src=DataUtil.intToBytes(MessageType.SINGLE_MESSAGE.ordinal());
		System.arraycopy(src, 0, dest, offset, Message.TYPE_LENGTH);offset+=Message.TYPE_LENGTH;
		src=data;
		System.arraycopy(src, 0, dest, offset, src.length);	
		return dest;
	}
}
