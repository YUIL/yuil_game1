package com.yuil.game.entity.physics;

import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;

public interface BtContactListener extends ContactListener{
	public void contect(btPersistentManifold manifold);
}
