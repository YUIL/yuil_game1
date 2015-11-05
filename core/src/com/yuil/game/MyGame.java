package com.yuil.game;


import com.badlogic.gdx.Game;
import com.yuil.game.screen.TestScreen;


public class MyGame extends Game {

	@Override
	public void create() {
		// TODO Auto-generated method stub
		this.setScreen(new TestScreen(this));
		
	}

	

	
}
