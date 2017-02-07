package com.yuil.game.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;

public class Log {
	static Date date=new Date();
	static SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
	
	private static void PrintlnWithTime(String str){
		date.setTime(System.currentTimeMillis());
		System.out.print(timeFormat.format(date)+"  ");
		System.out.println(str);
		//Gdx.app.log("hjk","hj");
	}
	
	private static void PrintWithTime(String str){
		date.setTime(System.currentTimeMillis());
		System.out.print(timeFormat.format(date)+"  ");
		System.out.print(str);
	}
	
	public static void println(String str){
		PrintlnWithTime(str);
	}
	
	public static void println(int i){
		PrintlnWithTime(String.valueOf(i));
	}
	
	public static void println(Object o){
		PrintlnWithTime(o.toString());
	}
	
	public static void print(String str){
		PrintWithTime(str);
	}
	
	public static void print(int i){
		PrintWithTime(String.valueOf(i));
	}
	
	public static void print(Object o){
		PrintWithTime(o.toString());
	}
}
