package com.yuil.game.net.udp;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.yuil.game.net.Session;
import com.yuil.game.net.udp.UdpSocket.SendServicer;

import io.netty.buffer.ByteBuf;


public class UdpSession extends Session{
	
	volatile boolean isSending=false;
	int timeOut=10;//millisecond
	int maxUnusedTime=30000;//millisecond
	int maxResendTimes=20;
	SendServicer sendThread;
	volatile short resendTimes=0;
	volatile long lastSendTime;
	volatile long lastReceiveTime;
	InetSocketAddress contactorAddress;
	volatile UdpMessage currentSendMessage;
	public   int lastSendSequenceId;
	public  int lastRecvSequenceId;
	volatile Queue<ByteBuf> sendMessageBuffer = new LinkedBlockingQueue<ByteBuf>();
	public volatile int sendMessageBufferMaxSize=100;
	
	public UdpSession(){
		init(new Random().nextLong());
	}
	
	public UdpSession(long id){
		init(id);
	}
	private void init(long id){
		//System.out.println("new session,id:"+id);
		this.id=id;
		lastSendSequenceId=-1;
		lastRecvSequenceId=-1;
	}

	public SendServicer getSendThread() {
		return sendThread;
	}

	public void setSendThread(SendServicer sendThread) {
		this.sendThread = sendThread;
	}

	public short getTimeOutMultiple() {
		return resendTimes;
	}

	public void setTimeOutMultiple(short timeOutMultiple) {
		this.resendTimes = timeOutMultiple;
	}

	
	public int getSendMessageBufferMaxSize() {
		return sendMessageBufferMaxSize;
	}

	public void setSendMessageBufferMaxSize(int sendMessageBufferMaxSize) {
		this.sendMessageBufferMaxSize = sendMessageBufferMaxSize;
	}

	public Queue<ByteBuf> getSendMessageBuffer() {
		return sendMessageBuffer;
	}

	public void setSendMessageBuffer(Queue<ByteBuf> sendMessageBuffer) {
		this.sendMessageBuffer = sendMessageBuffer;
	}

	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public InetSocketAddress getContactorAddress() {
		return contactorAddress;
	}
	public void setContactorAddress(InetSocketAddress contactorAddress) {
		this.contactorAddress = contactorAddress;
	}

	public long getLastSendTime() {
		return lastSendTime;
	}
	public void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime;
	}

	
	public long getLastReceiveTime() {
		return lastReceiveTime;
	}

	public void setLastReceiveTime(long lastReceiveTime) {
		this.lastReceiveTime = lastReceiveTime;
	}

	@Override
	public String toString() {
		return "Session [id=" + id + ", timeOut=" + timeOut + ", maxResendTimes=" + maxResendTimes + ", sendThread="
				+ sendThread + ", timeOutMultiple=" + resendTimes + ", lastSendTime=" + lastSendTime
				+ ", contactorAddress=" + contactorAddress + ", currentSendMessage=" + currentSendMessage
				+ ", lastSendSequenceId=" + lastSendSequenceId + ", lastRecvSequenceId=" + lastRecvSequenceId + "]";
	}

}
