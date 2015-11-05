package com.yuil.game.net.udp;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.Random;

import com.yuil.game.net.message.Message;
import com.yuil.game.net.udp.Session;
import com.yuil.game.net.udp.UdpMessageListener;

public class ClientSocket implements UdpMessageListener {
	volatile String remoteIp = null;
	volatile int remotePort;
	volatile UdpSocket udpSocket;
	volatile Session session;
	UdpMessageListener listenner = null;

	public ClientSocket() {
		super();
	}

	public ClientSocket(int port, String remoteIp, int remotePort, UdpMessageListener listener) {
		super();
		this.remoteIp = remoteIp;
		this.remotePort = remotePort;
		this.listenner = listener;
		if (initUdpServer(port)) {
			udpSocket.start();
		}
	}

	private boolean initUdpServer(int port) {
		if (port < 30000) {
			try {
				System.out.println("try start at port:" + port);
				udpSocket = new UdpSocket(port);
				udpSocket.setUdpMessageListener(this);
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
		if (udpSocket == null) {
			System.err.println("updServer==null");
			return false;
		} else {
			if (session == null) {
				System.err.println("session==null");
				session = udpSocket.createSession(new Random().nextLong(), new InetSocketAddress(remoteIp, remotePort));
				System.out.println("session id:" + session.getId());
			}
			return udpSocket.send(bytes, session,isImmediately);
		}
	}
	public UdpSocket getUdpSocket() {
		return this.udpSocket;
	}

	public void close() {
		if (udpSocket != null) {
			udpSocket.close();
		}
	}

	@Override
	public void disposeUdpMessage(Session session, byte[] data) {
		// TODO Auto-generated method stub
		if(listenner!=null)
			if (data.length > Message.TYPE_LENGTH) {
				listenner.disposeUdpMessage(session, data);
			}
	}
}
