package com.yuil.game.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.MessageListener;
import com.yuil.game.util.DataUtil;
import com.yuil.game.util.Log;

public class UdpSocket implements NetSocket{

	int maxSessionDelayTime = 30000;
	public DatagramSocket datagramSocket;
	public volatile boolean stoped = false;
	public volatile Map<Long, UdpSession> sessions = new ConcurrentHashMap<Long, UdpSession>();
	ReceiveServicer receiveServicer = null;
	ExecutorService sendThreadPool;
	Thread reciveThread;
	Thread guardThread;
	MessageListener messageListener;
	private static final UdpMessage CLOSE_MESSAGE=new UdpMessage((byte)0);
	
	
	volatile long recvCount = 0;
	volatile long sendCount = 0;
	volatile long resendCount = 0;
	volatile long recvDataLength = 0;
	volatile long sendDataLength = 0;
	volatile long resendDataLength = 0;

	public MessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	

	public synchronized UdpSession findSession(long sessionId) {
		return sessions.get(sessionId);
	}
	
	public  Session createSession(long sessionId, InetSocketAddress address) {
		return (Session)createUdpSession(sessionId, address);
	}

	public  UdpSession createUdpSession(long sessionId, InetSocketAddress address) {
		UdpSession session = new UdpSession(sessionId);
		session.setContactorAddress(address);
		session.setSendThread(new SendServicer(session));
		sessions.put(session.getId(), session);
		return session;
	}

	public UdpSocket(int port) throws BindException {
		init(port, 1);
	}

	public UdpSocket(int port, int maximumConections) throws BindException {
		init(port, maximumConections);
	}

	public void init(int port, int maximumConections) throws BindException {
		try {
			datagramSocket = new DatagramSocket(port);

		} catch (BindException e) {
			throw e;
		} catch (SocketException e) {

			e.printStackTrace();
		}
		sendThreadPool = Executors.newFixedThreadPool(maximumConections);
		// sendThreadPool=Executors.newSingleThreadExecutor();
	}

	public synchronized void removeSession(long sessionId) {
		Session session = findSession(sessionId);
		removeSession(session);

	}

	public synchronized void removeSession(Session session) {
		if (session != null) {
			/*
			 * if (session.currentSendUdpMessage(null) != null) {
			 * currentSendMessageNum--; }
			 */
			sessions.remove(session.getId());
		}

	}

	public void start() {

		receiveServicer = new ReceiveServicer();
		GuardThread guard = new GuardThread();
		guard.nextCheckTime = System.currentTimeMillis() + guard.interval;

		reciveThread = new Thread(receiveServicer);
		guardThread = new Thread(guard);

		reciveThread.start();
		guardThread.start();

	}
	
	@Override
	public void close() {
		System.out.println("close socket");

		for (UdpSession session:sessions.values()) {
			if (send(CLOSE_MESSAGE, session, false)) {
				try {
					Thread.currentThread();
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		sendThreadPool.shutdown();
		stoped = true;
		datagramSocket.close();
	}

	@Override
	public boolean send(byte[] data, Session session, boolean isImmediately) {
		// System.out.println("udpserver send");
		UdpMessage message = new UdpMessage();
		message.setSessionId(session.getId());
		message.setType((byte) 1);
		message.setLength(data.length);
		message.setData(data);
		return send(message, (UdpSession)session, isImmediately);
	}

	public boolean send(UdpMessage message, UdpSession session, boolean isImmediately) {

		if (isImmediately) {
			if (session.getSendMessageBuffer().size() != 0) {
				return false;
			} else {
				return send(message, session);
			}
		} else {
			if (session.getSendMessageBuffer().size() <= session.getSendMessageBufferMaxSize()) {
				return send(message, session);
			} else {
				System.out.println("sendBuffer满了满了……");
				return false;
			}
		}

	}

	public boolean send(UdpMessage message, UdpSession session) {
		session.getSendMessageBuffer().offer(message);
		if (!session.isSending) {
			session.isSending = true;
			sendThreadPool.execute(session.getSendThread());
		}
		return true;
	}

	public class GuardThread implements Runnable {
		int interval = 10000;
		long nextCheckTime;
		long reportTimes;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// nextReportTime = System.currentTimeMillis();
			while (!stoped) {
				if (System.currentTimeMillis() >= nextCheckTime) {
					nextCheckTime += interval;
					Iterator<Map.Entry<Long, UdpSession>> entries = sessions.entrySet().iterator();  
					while (entries.hasNext()) {  
						 Session session=entries.next().getValue();
						/*if(System.currentTimeMillis()-session.getLastSendTime()>session.maxUnusedTime
								&&System.currentTimeMillis()-session.getLastReceiveTime()>session.maxUnusedTime){
							if(!session.isSending){
								removeSession(session);
							}
							
						}*/
						 send("".getBytes(), session, false);
					}  

					// report();
					
				}
				try {
					Thread.currentThread();
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		@SuppressWarnings("unused")
		private void report() {
			reportTimes++;
			Log.print(reportTimes);
			System.out.print("{");
			System.out.print("sessionArray.size():" + sessions.size());
			// System.out.print(" | currentSendMessageNum:" +
			// currentSendMessageNum);
			System.out.print("  |  recvCount:" + recvCount);
			System.out.print("  |  sendCount:" + sendCount);
			System.out.print("  |  resendCount:" + resendCount);
			System.out.print("  |  recvDataLength:" + recvDataLength);
			System.out.print("  |  sendDataLength:" + sendDataLength);
			System.out.print("  |  resendDataLength:" + resendDataLength);
			System.out.print("}");
			System.out.println();
		}

	}

	public class SendServicer implements Runnable {
		volatile UdpSession session;

		public SendServicer(UdpSession session) {
			this.session = session;
		}

		@Override
		public void run() {
			// System.out.println("线程："+Thread.currentThread().getName());
			// System.out.println("send Thread run");
			// System.out.println("session.getSendMessageBuffer().size():" +
			// session.getSendMessageBuffer().size());
			// sendUdpMessage();
			// int count=0;
			UdpMessage message;
			while (!session.getSendMessageBuffer().isEmpty()) {
				message = session.sendMessageBuffer.poll();
				try {
					message.setSessionId(session.getId());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println("message" + message);
				}

				message.setSequenceId(session.lastSendSequenceId + 1);
				// System.out.println("send Message:"+message.toString());
				// System.out.println(session.lastSendSequenceId);
				if (!sendUdpMessage(message)) {// 如果发送失败了，就不再发送后面的了
					System.err.println("发送失败了");
					session.isSending = false;
					break;
				}
			}
			session.isSending = false;
		}

		private boolean sendUdpMessage(UdpMessage message) {

			boolean isSendSuccess = true;
			// while ((session.getCurrentSendMessage() != null)) {
			while (message.getSequenceId() == session.lastSendSequenceId + 1) {// 如果对方还没收到这条消息
				if (session.resendTimes > session.maxResendTimes) {// 如果单条消息重发次数超过maxResendTimes，删掉session
					removeSession(session);
					isSendSuccess = false;
					System.err.println("send_______________________________timeOutMultiple:" + session.resendTimes);
					System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					break;
				}
				if (System.currentTimeMillis() - session.lastSendTime < session.getTimeOut() * session.resendTimes) {
					try {
						Thread.sleep(session.getTimeOut() * session.resendTimes);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					// 统计信息{
					sendCount++;
					sendDataLength += message.getLength();
					if (session.resendTimes > 0) {// 是否之前发过这条消息
						resendCount++;
						resendDataLength += message.getLength();
					}
					// }

					session.resendTimes += 1;
					sendUdpMessage(session, message);// 发送消息

				}

			}
			session.resendTimes = 0;
			return isSendSuccess;
		}

		public synchronized void sendResponseUdpMessage(UdpMessage message) {
			// System.out.println("timeOutMulti:"+session.timeOutMultiple);
			session.setLastSendTime(System.currentTimeMillis());
			sendUdpMessage(datagramSocket, session.getContactorAddress(), message);
		}

		public synchronized void sendUdpMessage(UdpSession session, UdpMessage message) {
			// System.out.println("timeOutMulti:"+session.timeOutMultiple);
			session.setLastSendTime(System.currentTimeMillis());
			sendUdpMessage(datagramSocket, session.getContactorAddress(), message);
		}

		public synchronized void sendUdpMessage(DatagramSocket sendSocket, SocketAddress address, UdpMessage message) {
			// System.out.println("Udp send, message:"+message.toString());

			try {
				byte[] temp = message.toBytes();
				DatagramPacket sendPacket = new DatagramPacket(temp, temp.length, address);
				try {
					sendSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (NullPointerException e) {
				// TODO: handle exception
			}

		}

	}

	public class ReceiveServicer implements Runnable {

		volatile UdpSession session;
		final int bytesLength = 65515;
		// byte[] bytes1 = new byte[bytesLength];
		final byte[] recvBuf = new byte[bytesLength];
		final DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
		final UdpMessage recvMessageBuf = new UdpMessage();
		final UdpMessage responseMessage = new UdpMessage();

		// UdpMessage responseMessage;
		@Override
		public void run() {
			while (true) {
				if (stoped) {
					break;
				}

				if (datagramSocket == null) {
					// System.out.println("serverSocket == null!");
					break;
				}
				// System.arraycopy(bytes1, 0, recvBuf, 0,
				// bytesLength);//因为只收UdpMessage所以没用了

				

				try {
					// System.out.println("recv...");
					datagramSocket.receive(recvPacket);
					recvCount++;

				} catch (IOException e) {
					// System.out.println("recvTread终止！");
					break;
				}

				if (DataUtil.bytesToInt(DataUtil.subByte(recvPacket.getData(), 4, 13)) > 65515) {
					System.out.println("data too long");

				} else {
					recvMessageBuf.setData(null);
					recvMessageBuf.initUdpMessageByDatagramPacket(recvPacket);
					//System.out.println("udp recv:" + recvMessageBuf.toString()+"  thread:"+Thread.currentThread().getName());
					// UdpMessage recvMessageBuf = new UdpMessage(recvPacket);
					recvDataLength += recvMessageBuf.getData().length;

					session = findSession(recvMessageBuf.getSessionId());
					if (session == null) {
						session = createUdpSession(recvMessageBuf.getSessionId(),
								new InetSocketAddress(recvPacket.getAddress(), recvPacket.getPort()));
						// System.out.println(session.toString());
					}

					responseMessage.setSessionId(session.getId());
					switch (recvMessageBuf.getType()) {
					case 0:
						removeSession(recvMessageBuf.getSessionId());
						
					case 1:
						if (recvMessageBuf.getSequenceId() == session.lastRecvSequenceId + 1) {
							// session.getRecvMessageQueue().add(recvMessageBuf);
							session.lastRecvSequenceId = recvMessageBuf.getSequenceId();
							if (messageListener != null&&recvMessageBuf.length>0) {
								messageListener.recvMessage(session, recvMessageBuf.getData());
							}
							responseMessage.setSequenceId(recvMessageBuf.getSequenceId());
							responseMessage.setType((byte) 2);
							session.getSendThread().sendResponseUdpMessage(responseMessage);
							session.setLastReceiveTime(System.currentTimeMillis());
						} else if (recvMessageBuf.getSequenceId() == session.lastRecvSequenceId) {
							responseMessage.setSequenceId(session.lastRecvSequenceId);
							responseMessage.setType((byte) 3);
							session.getSendThread().sendResponseUdpMessage(responseMessage);
						}
						break;
					case 2:
						if (session != null && session.isSending) {
							if (recvMessageBuf.getSequenceId() == session.lastSendSequenceId + 1) {
								//System.out.println("发送成功");
								session.lastSendSequenceId++;

							}
						} else {
							// System.out.println("回的啥，跟我没关系！");
						}
						break;
					case 3:
						// System.out.println("消息SequenceId不对");
						if (session != null && session.isSending) {
							if (recvMessageBuf.getSequenceId() == session.lastSendSequenceId + 1) {
								// System.out.println("发送成功");
								session.lastSendSequenceId++;

							} else {
								// System.out.println("真不对！");
							}
						} else {
							// System.out.println("回你妹，早发完了");

						}
						break;
					}

					// System.out.println("recv:"+session.getLastresponseMessage());
				}

			}
			
		}

	}

	@Override
	public Collection<? extends  Session> getSessions() {
		// TODO Auto-generated method stub
		return sessions.values();
	}
}
