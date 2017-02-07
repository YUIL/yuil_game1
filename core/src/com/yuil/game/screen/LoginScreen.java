package com.yuil.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.yuil.game.MyGame;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.ActorInputListenner;
import com.yuil.game.input.InputManager;
public class LoginScreen extends Screen2D {

	public LoginScreen(MyGame game) {
		super(game);
		// TODO Auto-generated constructor stub
		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/Login.xml";
		guiFactory.setStage(stage, guiXmlPath);
		inputProcess();

		InputManager.setInputProcessor(stage);
	}



	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
				Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight());
				Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				super.render(delta);

	}
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		//System.out.println("resize");
		super.resize(width, height);
		
		
	}
	private void inputProcess() {
		// TODO Auto-generated method stub
		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				//(MyGdxGame)game).openId;
				//MyGame.openId=((TextArea) stage.getRoot().findActor("userName")).getText();
				game.setScreen(new TestScreen2(game));
				
			}
		});
	}
}
