package test;

import java.net.BindException;
import java.util.HashMap;
import java.util.Map;

import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.ClientSocket;
import com.yuil.game.net.udp.Session;
import com.yuil.game.net.udp.UdpMessageListener;
import com.yuil.game.net.udp.UdpSocket;
import com.yuil.game.util.DataUtil;

public class NetSocketTest extends BaseTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		NetSocketTest netSocketTest = new NetSocketTest();

		netSocketTest.startInput();
		System.out.println("stop");
	}

	Server server;
	Client client;
	static long time;

	public NetSocketTest() throws BindException {
		server = new Server();
		client = new Client();

		instructionMap.put("1", new Instruction() {
			@Override
			public void start() {
				// TODO Auto-generated method stub
				client.sendTest();
			}
		});

		instructionMap.put("2", new Instruction() {
			@Override
			public void start() {
				// TODO Auto-generated method stub
				System.out.println(server.udpSocket.sessions.size());
			}
		});
		
		instructionMap.put("3", new Instruction() {
			@Override
			public void start() {
				
				SINGLE_MESSAGE single_MESSAGE=new SINGLE_MESSAGE("asda".getBytes());
				client.clientSocket.send(single_MESSAGE.toBytes(), false);
			}
		});
		
		instructionMap.put("4", new Instruction() {
			@Override
			public void start() {
				
				SINGLE_MESSAGE single_MESSAGE=new SINGLE_MESSAGE("asda".getBytes());
				
				Message[] messages=new Message[2];
				messages[0]=single_MESSAGE;
				messages[1]=single_MESSAGE;
				
				MESSAGE_ARRAY message_ARRAY=new MESSAGE_ARRAY(messages);
				
				client.clientSocket.send(message_ARRAY.toBytes(), false);
			}
		});
		
		
		instructionMap.put("quit", new Instruction() {
			@Override
			public void start() {
				server.udpSocket.close();
				client.clientSocket.close();
				stoped=true;
			}
		});

	}

	public class Server {
		UdpSocket udpSocket;
		Map <Integer,MessageHandler> messageHandlerMap;
		public Server() throws BindException {
			udpSocket = new UdpSocket(9091);
			messageHandlerMap=new HashMap<Integer,MessageHandler>();
			initMessageHandlerMap();
			
			udpSocket.setUdpMessageListener(new UdpMessageListener() {

				@Override
				public void disposeUdpMessage(Session session, byte[] data) {
					// TODO Auto-generated method stub
					int type=MessageUtil.getType(data);
					byte[] src=DataUtil.subByte(data, data.length-Message.TYPE_LENGTH, Message.TYPE_LENGTH);
					System.out.println("server recv:"+MessageType.values()[type]);
					
					messageHandlerMap.get(type).handle(src);
				}
			});
			udpSocket.start();
		}
		
		void initMessageHandlerMap(){
			messageHandlerMap.put(MessageType.MESSAGE_ARRAY.ordinal(), new MessageHandler() {
				@Override
				public void handle(byte[] src) {
					// TODO Auto-generated method stub
					MESSAGE_ARRAY message_ARRAY=new MESSAGE_ARRAY(src);
					for (int i = 0; i < message_ARRAY.messageNum; i++) {
						SINGLE_MESSAGE message=new SINGLE_MESSAGE(message_ARRAY.gameMessages[i]);
						System.out.println(new String (message.data));
					}
				}
			});
			
			messageHandlerMap.put(MessageType.SINGLE_MESSAGE.ordinal(), new MessageHandler() {
				@Override
				public void handle(byte[] src) {
					// TODO Auto-generated method stub
					SINGLE_MESSAGE message=new SINGLE_MESSAGE(src);
					System.out.println(new String(message.data));
				}
			});

		}
	}

	public class Client {
		ClientSocket clientSocket;

		public Client() throws BindException {
			clientSocket = new ClientSocket(9092, "127.0.0.1", 9091, new UdpMessageListener() {
				@Override
				public void disposeUdpMessage(Session session, byte[] data) {
					// TODO Auto-generated method stub
					// System.out.println("data:"+data);
				}
			});
		}

		public void sendTest() {
			System.out.println("send");
			clientSocket.send("asd".getBytes(), false);
		}

	}
}
