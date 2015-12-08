package com.yuil.game.net.message;

import com.yuil.game.util.DataUtil;

import io.netty.buffer.ByteBuf;

public class MessageUtil {
	
	public static byte [] getMessageBytes(byte[] data){
		return  DataUtil.subByte(data, data.length - Message.TYPE_LENGTH, Message.TYPE_LENGTH);
	}
	
	public static ByteBuf getMessageByteBuf(ByteBuf data){		
		return  data.copy(Message.TYPE_LENGTH, data.array().length-Message.TYPE_LENGTH);
	}
	
	public static int getType(byte[] data){
		return DataUtil.bytesToInt(DataUtil.subByte(data, Message.TYPE_LENGTH, 0)); 
	}
	
	public static void bytesAppendType(byte[] dest ,int type){
		System.arraycopy(DataUtil.intToBytes(type), 0, dest, 0, Message.TYPE_LENGTH);
		

	}
}
