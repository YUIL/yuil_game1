package com.yuil.game.entity;



import java.util.Map;

import com.badlogic.gdx.math.Vector3;

public abstract class  PhysicsWorld {
	
	
	Vector3 gravity = new Vector3(0, -0.5f, 0);
	//Vector3 gravity = new Vector3(0, -9.81f, 0);
	
	public PhysicsWorld(){
		
	}
	
	public abstract void update(float delta);
	
	public abstract void addPhysicsObject(PhysicsObject physicsObject);
	
	public abstract void removePhysicsObject(PhysicsObject physicsObject);
	
	public abstract Map<Long,? extends PhysicsObject> getPhysicsObjects();
}
