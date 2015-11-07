package com.yuil.game.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract class PhysicsObject {
	long id;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public abstract Matrix4 getTransform();
	public abstract Vector3 getPosition();
	public abstract void setPosition(Vector3 position);
}
