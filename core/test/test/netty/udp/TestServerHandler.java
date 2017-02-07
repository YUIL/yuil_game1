package test.netty.udp;



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yuil.game.net.udp.UdpSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class TestServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	long time=0;
	
	public volatile Map<Long, UdpSession> udpSessions = new ConcurrentHashMap<Long, UdpSession>();
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		// TODO Auto-generated method stub
		time=System.nanoTime();
		System.out.println(msg.content());
	}

	
}
