package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BaseTest {
	protected Map<String,Instruction> instructionMap =new HashMap<String, Instruction>();
	boolean stoped=false;
	public void startInput(){
		
		Scanner scanner=new Scanner(System.in);
		
		while(!stoped){
			System.out.print(">>");
			String str=scanner.nextLine();
			
			Instruction instruction=instructionMap.get(str);
			if(instruction!=null){
				instruction.start();
			}else{
				System.out.println("指令不存在！");
			}
			
			if (str.equals("quit")){
				System.out.println("测试结束");
				break;
			}
		}
	}
	
	public interface Instruction{
		public void start();
	}
}
