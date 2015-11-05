package com.yuil.game.net.message;

import com.yuil.game.util.DataUtil;

public enum MessageType {
	SINGLE_MESSAGE,
	MESSAGE_ARRAY;
	
	
	public static int getType(byte[] src){
		return DataUtil.bytesToInt(DataUtil.subByte(src, Message.TYPE_LENGTH, 0)); 
	}
}
