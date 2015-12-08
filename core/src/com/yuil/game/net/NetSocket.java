package com.yuil.game.net;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.Collection;

import io.netty.buffer.ByteBuf;


/**
 * @author i008
 *
 */
public interface NetSocket extends Closeable {

	
	
	/**
	 * 开始接受消息
	 */
	public void start();
	
	
	/**
	 * @param data 要发送的消息
	 * @param session 消息对应的会话
	 * @param isImmediately 是否立即发送
	 * @return 是否成功添加到发送队列
	 */
	public boolean send(byte[] data, Session session, boolean isImmediately);
	
	
	/**
	 * @param data 要发送的消息
	 * @param session 消息对应的会话
	 * @param isImmediately 是否立即发送
	 * @return 是否成功添加到发送队列
	 */
	public boolean send(ByteBuf data, Session session, boolean isImmediately);
	
	/**
	 * @return 活动中的会话集合
	 */
	public Collection<? extends Session> getSessions();
	
	
	/**
	 * @param messageListener 接收消息的监听器
	 */
	public void setMessageListener(MessageListener messageListener);
	
	
	/**
	 * @param sessionId 顾名思义
	 * @param address 顾名思义
	 * @return 创建的会话
	 */
	public Session createSession(long sessionId, InetSocketAddress address);
}
