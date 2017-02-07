package test.netty.udp;

import java.net.InetSocketAddress;

import com.yuil.game.net.udp.UdpMessage;

import test.BaseTest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class TestClient extends BaseTest{

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		final TestClient testClient=new TestClient();
		
		testClient.run(0);
		testClient.startInput();
		
		
		testClient.ch.closeFuture().await();

	}
	
	
	

	EventLoopGroup group = new NioEventLoopGroup();
	Channel ch;
	long time=0;
	
	public TestClient(){
		this.instructionMap.put("1", new Instruction() {
			
			@Override
			public void start() {
				
				DatagramPacket datagramPacket=new DatagramPacket(Unpooled.copiedBuffer("asd", CharsetUtil.UTF_8),
						new InetSocketAddress("255.255.255.255", 9091));
				try {
					time=System.nanoTime();

					ch.writeAndFlush(datagramPacket).sync();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(time);
			}
		});
	}

	public void run(int port) throws Exception {

		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
				.handler(new ChannelInitializer<NioDatagramChannel>() {

					@Override
					protected void initChannel(NioDatagramChannel ch) throws Exception {
						ch.pipeline().addLast(
								new TestClientHandler()
						);
					}

				});
		ch=b.bind(port).sync().channel();
	}

	public void sendBytes(byte[] message) {

	}

	public void stop() {
		group.shutdownGracefully();
	}
}
