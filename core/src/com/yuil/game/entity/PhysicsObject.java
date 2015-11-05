package com.yuil.game.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract class PhysicsObject {
	Long id;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public abstract Matrix4 getTransform();
	public abstract Vector3 getPosition();
	public abstract void setPosition();
}
