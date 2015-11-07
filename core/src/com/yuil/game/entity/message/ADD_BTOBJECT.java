package com.yuil.game.entity.message;

import com.yuil.game.net.message.Message;

public class ADD_BTOBJECT implements Message {
	public final int type=EntityMessageType.ADD_BTOBJECT.ordinal();

	@Override
	public void init(byte[] src) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
