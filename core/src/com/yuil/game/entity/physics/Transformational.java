package com.yuil.game.entity.physics;

import com.badlogic.gdx.math.Matrix4;

public interface Transformational {

	public Matrix4 getTransform();
	public void setTransform(Matrix4 transform);
}
