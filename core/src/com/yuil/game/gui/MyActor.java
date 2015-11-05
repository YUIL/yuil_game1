package com.yuil.game.gui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class MyActor extends Button {
	public String id;
	TextureRegion region;
	String buttonName="button";
	BitmapFont bf;
	public MyActor(Skin skin){
		
		super(skin);
		bf=new BitmapFont();
		//bf.setScale(1.3f, 1.3f);
		bf.setColor(new Color(0.5f, 0.5f, 0.5f, 1));
	}
	public MyActor(String id,Drawable up,Drawable down,String buttonName){
		super(up,down);
		this.id=id;
		this.buttonName=buttonName;
		bf=new BitmapFont();
		
	}
	public MyActor(String id,Drawable up,Drawable down){
		super(up,down);
		this.id=id;
		bf=new BitmapFont();
		//bf.setScale(1.3f, 1.3f);
		bf.setColor(new Color(0.5f, 0.5f, 0.5f, 1));
		
	}
	public MyActor(Drawable up,Drawable down){
		super(up,down);
		bf=new BitmapFont();
		//bf.setScale(1.3f, 1.3f);
		bf.setColor(new Color(0.5f, 0.5f, 0.5f, 1));
	}
	
	
	@Override
	public void draw(Batch batch,float parentAlpha){
		
		super.draw(batch, parentAlpha);
		bf.draw(batch, buttonName, this.getX()+this.getWidth()/4, this.getY()+this.getHeight()/1.5f);
		
	}
	
	@Override
	
	public Actor hit (float x, float y, boolean touchable) {
	    if (touchable && getTouchable() != Touchable.enabled) return null;
	    return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
}
