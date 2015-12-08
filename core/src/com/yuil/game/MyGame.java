package com.yuil.game;


import com.badlogic.gdx.Game;
import com.yuil.game.screen.LoginScreen;
import com.yuil.game.screen.TestScreen;


public class MyGame extends Game {
	public static String openId="-1";
	@Override
	public void create() {
		// TODO Auto-generated method stub
		this.setScreen(new LoginScreen(this));
		
	}

	

	
}
