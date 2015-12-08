package com.yuil.game.entity;

import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;

public interface BtContactListener extends ContactListener{
	public void contect(btPersistentManifold manifold);
}
