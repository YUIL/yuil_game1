package com.yuil.game.entity.physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

public class RenderableBtObject extends BtObject {
	ModelInstance instance;
	
	
	public RenderableBtObject() {
		super();
	}

	public RenderableBtObject(long id,ModelInstance instance,btRigidBody rigidBody, btDefaultMotionState motionState,
			btCollisionShape collisionShape, btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
		super(id, rigidBody,  motionState, collisionShape,  rigidBodyConstructionInfo);
	
	}

	public ModelInstance getInstance() {
		return instance;
	}

	public void setInstance(ModelInstance instance) {
		this.instance = instance;
	}
	
	
}
