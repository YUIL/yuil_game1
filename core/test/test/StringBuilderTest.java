package test;

public class StringBuilderTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringBuilder str1=new StringBuilder("A");

		StringBuilder str2=new StringBuilder("B");
		str(str1,str2);
		System.out.println(str1.toString()+str2.toString());
		
	}
	public static void str(StringBuilder str1,StringBuilder str2){
		str1.append(str2);
		str2=str1;
	}


}
