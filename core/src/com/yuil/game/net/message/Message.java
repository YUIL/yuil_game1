package com.yuil.game.net.message;

public interface Message{

	public static final int TYPE_LENGTH=1;
	
	public abstract void init(byte[] src);
	public abstract byte[] toBytes();

}
