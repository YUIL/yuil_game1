package com.yuil.game.entity.attribute;

public class Color implements Attribute {
	public final Integer type=AttributeType.COLOR.ordinal();
	com.badlogic.gdx.graphics.Color color;
	

	public Color(com.badlogic.gdx.graphics.Color color) {
		super();
		this.color = color;
	}


	public com.badlogic.gdx.graphics.Color getColor() {
		return color;
	}


	public void setColor(com.badlogic.gdx.graphics.Color color) {
		this.color = color;
	}


	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
