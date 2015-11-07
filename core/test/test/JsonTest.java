package test;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.yuil.game.entity.message.ADD_BALL;

public class JsonTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonTest test=new JsonTest();
		test.test();
	}
	
	JsonReader jsonReader =new JsonReader();
	JsonWriter jsonWriter;
	Json json =new Json();
	
	public void test(){
		ADD_BALL m=new ADD_BALL();
		m.setId(1l);m.setX(2.123123f);m.setY(3f);m.setZ(4f);
		
		String s=json.toJson(m);
		System.out.println(s.length());
	}
	

}
