package test.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class TestServer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		final TestServer testServer=new TestServer();
		
		testServer.run(9091);
	}

	EventLoopGroup group = new NioEventLoopGroup();
	Channel ch;
	

	public void run(int port) throws Exception {

		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
				.handler(new ChannelInitializer<NioDatagramChannel>() {

					@Override
					protected void initChannel(NioDatagramChannel ch) throws Exception {
						ch.pipeline().addLast(
								new TestServerHandler()
						);
					}

				});
		ch=b.bind(port).sync().channel();
		ch.closeFuture().await();
	}

	public void sendBytes(byte[] message) {

	}

	public void stop() {
		group.shutdownGracefully();
	}
}
