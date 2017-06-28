package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class DO_ACTION implements Message {
	public final int type=EntityMessageType.DO_ACTION.ordinal();
	long playerId;
	long actionId=0;
	
	
	public DO_ACTION() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public DO_ACTION(ByteBuf src) {
		super();
		this.set(src);
		// TODO Auto-generated constructor stub
	}




	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public long getActionId() {
		return actionId;
	}

	public void setActionId(long actionId) {
		this.actionId = actionId;
	}

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		this.setPlayerId(buf.readLong());
		this.setActionId(buf.readLong());
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
		buf.writeLong(this.playerId);
		buf.writeLong(this.actionId);
	
		return buf;
	}

	@Override
	public String toString() {
		return "DO_ACTION [type=" + type + ", playerId=" + playerId + ", actionId=" + actionId + "]";
	}

	

}
