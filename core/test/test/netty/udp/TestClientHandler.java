package test.netty.udp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yuil.game.net.udp.UdpSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class TestClientHandler  extends SimpleChannelInboundHandler<DatagramPacket>{
	public volatile Map<Long, UdpSession> udpSessions = new ConcurrentHashMap<Long, UdpSession>();

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		
		ctx.write(msg, promise);
    }
}
