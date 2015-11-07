package com.yuil.game.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ActorInputListenner extends InputListener {
	public Actor actor;
	public ActorInputListenner(Actor actor){
		super();
		this.actor=actor;
	}
	public ActorInputListenner(){
		super();
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
		return true;
		
	}
	public Actor getActor() {
		return actor;
	}
	public void setActor(Actor actor) {
		this.actor = actor;
	}
	

}
