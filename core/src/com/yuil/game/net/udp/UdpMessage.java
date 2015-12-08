package com.yuil.game.net.udp;

import java.net.DatagramPacket;
import java.util.Arrays;

import com.yuil.game.util.DataUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class UdpMessage {
/*	public long sessionId;
	public int sequenceId;
	public byte type;// 0：退出，1：順序消息，2：确认,3：错误
	public int length;
	public byte[] data;
*/
	
	public static final int HEADER_LENGTH=8+4+1+4;
	
	
	public static long getSessionId(ByteBuf buf){
		return buf.getLong(0);
	}
	public static void setSessionId(ByteBuf buf,long sessionId){
		buf.setLong(0, sessionId);
	}
	//====================
	public static int getSequenceId(ByteBuf buf){
		return buf.getInt(8);
	}
	public static void setSequenceId(ByteBuf buf,int sequenceId){
		buf.setInt(8, sequenceId);
	}
	//============
	public static byte getType(ByteBuf buf){
		return buf.getByte(12); 
	}
	public static void setType(ByteBuf buf ,byte type){
		buf.setByte(12, type);
	}
	//=============
	public static int getLength(ByteBuf buf){
		return buf.getInt(13);
	}
	public static void setLength(ByteBuf buf,int length){
		buf.setInt(13, length);
	}
	//===========
	public static ByteBuf getData(ByteBuf buf){
		return buf.copy(HEADER_LENGTH,buf.array().length-HEADER_LENGTH);
	}
	public static void setData(ByteBuf buf,ByteBuf data){
		buf.setBytes(HEADER_LENGTH, data);
	}
	public static void setData(ByteBuf buf,byte[] data){
		buf.setBytes(HEADER_LENGTH, data);
	}
	
	
	
	/*
	public UdpMessage() {

	}
	public UdpMessage(long sessionId,int sequenceId) {
		this.sessionId=sessionId;
		this.sequenceId=sequenceId;
	}
	
	public UdpMessage(byte type) {
		this.type=type;
	}
	public UdpMessage(DatagramPacket recvPacket) {
		initUdpMessageByDatagramPacket(this, recvPacket);
	}

	public void initUdpMessageByDatagramPacket(UdpMessage message,
			DatagramPacket recvPacket) {
		byte[] data = recvPacket.getData();
		initUdpMessageByDatagramPacket(message, data);
	}
	
	public void initUdpMessageByDatagramPacket(UdpMessage message,
			byte[] data) {
		int offset = 0;
		message.setSessionId(DataUtil.bytesToLong(DataUtil
				.subByte(data, 8, offset)));
		offset+=8;
		message.setSequenceId(DataUtil.bytesToInt(DataUtil
				.subByte(data, 4, offset)));
		offset+=4;
		message.setType(DataUtil.subByte(
				data, 1, offset)[0]);
		offset+=1;
		message.setLength(DataUtil.bytesToInt(DataUtil
				.subByte(data, 4, offset)));
		offset+=4;
		
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(message.length);
		buf.writeBytes(DataUtil.subByte(data,message.length,offset));
		message.setData(buf.array());
		buf.release();
	}

	public void initUdpMessageByDatagramPacket(DatagramPacket recvPacket) {
		byte[] data = recvPacket.getData();
		int offset = 0;
		this.setSessionId(DataUtil.bytesToLong(DataUtil
				.subByte(data, 8, offset)));
		offset+=8;
		this.setSequenceId(DataUtil.bytesToInt(DataUtil
				.subByte(recvPacket.getData(), 4, offset)));
		offset+=4;
		this.setType(DataUtil.subByte(
				recvPacket.getData(), 1, offset)[0]);
		offset+=1;
		this.setLength(DataUtil.bytesToInt(DataUtil
				.subByte(recvPacket.getData(), 4, offset)));
		offset+=4;
		//if(this.length>0){
			//System.out.println("offset:"+offset);
			this.initDateFromUdpbytes(DataUtil.subByte(recvPacket.getData(),this.length,offset));
		//}
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int lenth) {
		this.length = lenth;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	

	public void initDateFromUdpbytes(byte[] data) {
		//System.out.println("initdate:"+this.toString());
		//System.out.println("data.length:"+data.length);

		this.data=UnpooledByteBufAllocator.DEFAULT.heapBuffer(data.length);
		this.data.writeBytes(data);
		this.data = new byte[this.length];
		System.arraycopy(data, 0, this.data, 0, this.length);
	}

	public byte[] toBytes() {
		byte[] dest = new byte[17 + length];
		System.arraycopy(DataUtil.longToBytes(sessionId), 0, dest, 0,8);		
		System.arraycopy(DataUtil.intToBytes(sequenceId), 0, dest, 8,4);
		System.arraycopy(DataUtil.intToBytes(type), 0, dest, 12, 1);
		System.arraycopy(DataUtil.intToBytes(length), 0, dest, 13, 4);
		if (data!=null) {
			System.arraycopy(data, 0, dest, 17, length);
		}
		return dest;
	}
	@Override
	public String toString() {
		return "UdpMessage [sessionId=" + sessionId + ", sequenceId="
				+ sequenceId + ", type=" + type + ", length=" + length
				+ ", data=" + Arrays.toString(data) + "]";
	}*/

}
