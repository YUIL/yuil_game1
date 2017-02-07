package com.yuil.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yuil.game.MyGame;
import com.yuil.game.gui.GuiFactory;

public class Screen2D extends BaseScreen{
	SpriteBatch batch;
	Viewport viewport;
	Skin skin;
	Stage stage;
	public Screen2D(MyGame game) {
		super(game);
		batch = new SpriteBatch();
		skin = GuiFactory.defaultSkin;
		stage = new Stage();
		
		viewport = new StretchViewport(800, 480, stage.getCamera());
		stage.setViewport(viewport);
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setProjectionMatrix(stage.getCamera().projection);
	}
	@Override
	public void render(float delta) {
		super.render(delta);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
		stage.getCamera().update();
	}


	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		//skin.dispose();
	}
}
