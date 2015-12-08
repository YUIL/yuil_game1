package com.yuil.game.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.Random;

import com.yuil.game.net.MessageListener;
import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.Message;

import io.netty.buffer.ByteBuf;

/**
 * @author i008
 *
 */
public class ClientSocket implements MessageListener {
	volatile String remoteIp = null;
	volatile int remotePort;
	volatile NetSocket netSocket;
	volatile Session session;
	MessageListener listenner = null;

	public ClientSocket() {
		super();
	}

	/**
	 * @param port 本地端口，如已占用会自动尝试其他大于输入参数的端口
	 * @param remoteIp 远程IP地址
	 * @param remotePort 远程端口
	 * @param listener 消息监听器
	 */
	public ClientSocket(int port, String remoteIp, int remotePort, MessageListener listener) {
		super();
		this.remoteIp = remoteIp;
		this.remotePort = remotePort;
		this.listenner = listener;
		if (initUdpServer(port)) {
			netSocket.start();
		}
	}

	private boolean initUdpServer(int port) {
		if (port < 30000) {
			try {
				System.out.println("try start at port:" + port);
				netSocket = new UdpSocket(port);
				netSocket.setMessageListener(this);
				return true;
			} catch (BindException e) {
				System.out.println(port + " exception!");
				port++;
				return initUdpServer(port);
			}
		} else {
			System.err.println("port must <10000!");
			return false;
		}

	}



	public synchronized boolean send(byte[] bytes,boolean isImmediately) {
		if (netSocket == null) {
			System.err.println("updServer==null");
			return false;
		} else {
			if (session == null) {
				System.err.println("session==null");
				session = netSocket.createSession(new Random().nextLong(), new InetSocketAddress(remoteIp, remotePort));
				System.out.println("session id:" + session.getId());
			}
			return netSocket.send(bytes, session,isImmediately);
		}
	}
	public NetSocket getUdpSocket() {
		return this.netSocket;
	}

	public void close() {
		if (netSocket != null) {
			try {
				netSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void recvMessage(Session session, ByteBuf data) {
		// TODO Auto-generated method stub
		if(listenner!=null)
			if (data.array().length > Message.TYPE_LENGTH) {
				listenner.recvMessage(session, data);
			}
	}
}
