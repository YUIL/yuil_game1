package com.yuil.game.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Disposable;

public class BtWorld extends PhysicsWorld implements Disposable{
	
	Map<Long, BtObject> physicsObjects;
	btCollisionConfiguration collisionConfiguration;
	btCollisionDispatcher dispatcher;
	btBroadphaseInterface broadphase;
	btConstraintSolver solver;
	btDynamicsWorld collisionWorld;
	
	
	public BtWorld() {
		super();
		Bullet.init();
		// Create the bullet world
		collisionConfiguration = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfiguration);
		broadphase = new btDbvtBroadphase();
		solver = new btSequentialImpulseConstraintSolver();
		collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		collisionWorld.setGravity(gravity);
		this.physicsObjects=new ConcurrentHashMap<Long, BtObject>();
		
	}
	
	public void update(float delta){
		collisionWorld.stepSimulation(delta, 5);
		Vector3 v3=new Vector3();

		for (BtObject btObject : physicsObjects.values()) {
			btObject.update();
			btObject.rigidBody.getWorldTransform().getTranslation(v3);
			if (v3.y<-100){
				removePhysicsObject(btObject);
			}
		}
	}
	
	public void addPhysicsObject(BtObject btObject){
		physicsObjects.put(btObject.id,btObject);
		collisionWorld.addRigidBody(btObject.rigidBody);
	}

	public void removePhysicsObject(BtObject btObject){
		physicsObjects.remove(btObject.id);
		collisionWorld.removeRigidBody(btObject.rigidBody);
		btObject.dispose();
	}
	

	

	public Map<Long, BtObject> getPhysicsObjects() {
		return physicsObjects;
	}

	public void setPhysicsObjects(Map<Long, BtObject> physicsObjects) {
		this.physicsObjects = physicsObjects;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		for (BtObject btObject : physicsObjects.values()) {
			btObject.dispose();
		}
		collisionWorld.dispose();
		solver.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfiguration.dispose();
	}

	@Override
	public void addPhysicsObject(PhysicsObject physicsObject) {
		this.addPhysicsObject((BtObject)physicsObject);
	}

	@Override
	public void removePhysicsObject(PhysicsObject physicsObject) {
		this.removePhysicsObject((BtObject)physicsObject);
	}

}
