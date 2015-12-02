package test;

import com.yuil.game.net.udp.UdpSession;
import com.yuil.game.net.udp.UdpMessage;

public class MemoryLeakTest {
	long time;
	int interval=0;
	long temp=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MemoryLeakTest test=new MemoryLeakTest();
		test.test();
		
	}

	public void test(){
		while(true){
			//temp=Runtime.getRuntime().totalMemory()- Runtime.getRuntime().freeMemory();
			//System.out.println("o");


		}
	}
}
