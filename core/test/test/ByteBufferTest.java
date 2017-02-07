package test;

import java.nio.ByteBuffer;

import com.yuil.game.net.udp.UdpMessage;
import com.yuil.game.util.DataUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;

public class ByteBufferTest extends BaseTest{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ByteBufferTest().test4();
		
	}
	
	public void test4(){
		byte[] data=new byte[1];
		data[0]=33;
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(100);
		UdpMessage.setSequenceId(buf, 123);
		System.out.println(UdpMessage.getSequenceId(buf));
		
		UdpMessage.setData(buf, data);
		System.out.println(UdpMessage.getData(buf).array()[0]);
		
	}
	
	public void test3(){
		ByteBuf byteBuf=new PooledByteBufAllocator(false).heapBuffer(65515);
		byteBuf.retain();
		byteBuf.release();
		System.out.println(byteBuf.toString());
	}

	
	public void test2(){
		ByteBuf byteBuf=new UnpooledByteBufAllocator(false).heapBuffer(48);
		byteBuf.writeLong(1);
		byteBuf.writeInt(1);
		byteBuf.writeByte((byte)1);
		byteBuf.writeInt(1);
		byteBuf.writeByte((byte)1);
		
		System.out.println(byteBuf.readLong());
		System.out.println(byteBuf.toString());

	}
	
	public void test1(){
		ByteBuffer byteBuffer=ByteBuffer.allocate(18);
		byteBuffer.putLong(1);
		byteBuffer.putInt(1);
		byteBuffer.put((byte)1);
		byteBuffer.putInt(1);
		byteBuffer.put((byte)1);
		byteBuffer.flip();
		
		System.out.println(byteBuffer.getLong());
		System.out.println(byteBuffer.toString());
	
	}
}
