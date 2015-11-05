package com.yuil.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputManager {
	public static void setInputProcessor(Stage stage) {
		setInputProcessor((InputProcessor)stage);
	}
	public static void setInputProcessor(InputProcessor inputProcessor) {
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(multiplexer);
	}

	public static void setInputProcessor(InputProcessor... inputProcessor) {
		InputMultiplexer multiplexer = new InputMultiplexer();
		for (int i = 0; i < inputProcessor.length; i++) {
			multiplexer.addProcessor(inputProcessor[i]);
		}
		Gdx.input.setInputProcessor(multiplexer);
	}
}
