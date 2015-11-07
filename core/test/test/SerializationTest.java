package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.yuil.game.entity.message.ADD_BALL;

public class SerializationTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SerializationTest test=new SerializationTest();
		test.test();
	}
	public void test() throws Exception{
		//序列化一个对象(存储到字节数组) 
		ADD_BALL m=new ADD_BALL();
		m.setId(1l);m.setX(2f);m.setY(3f);m.setZ(4f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos2 = new ObjectOutputStream(baos); 
		oos2.writeObject(m); 
		oos2.close(); 

		System.out.println(baos.size());
		
		//反序列化,将该对象恢复(存储到字节数组) 
		ObjectInputStream ois2 = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())); 
		ADD_BALL b = (ADD_BALL)ois2.readObject(); 
		System.out.println(b.getY());
	}
}
