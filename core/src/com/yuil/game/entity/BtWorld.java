package com.yuil.game.entity;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Matrix4;
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
import com.yuil.game.entity.message.APPLY_FORCE;
import com.yuil.game.entity.message.UPDATE_BTOBJECT_MOTIONSTATE;
import com.yuil.game.server.BtTestServer2;

public class BtWorld extends PhysicsWorld implements Disposable{
	public Queue<BtObject> addPhysicsObjectQueue=new  ConcurrentLinkedQueue<BtObject>();
	public Queue<BtObject> removePhysicsObjectQueue=new  ConcurrentLinkedQueue<BtObject>();
	public btCollisionConfiguration getCollisionConfiguration() {
		return collisionConfiguration;
	}

	public void setCollisionConfiguration(btCollisionConfiguration collisionConfiguration) {
		this.collisionConfiguration = collisionConfiguration;
	}

	public btCollisionDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(btCollisionDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public btBroadphaseInterface getBroadphase() {
		return broadphase;
	}

	public void setBroadphase(btBroadphaseInterface broadphase) {
		this.broadphase = broadphase;
	}

	public btConstraintSolver getSolver() {
		return solver;
	}

	public void setSolver(btConstraintSolver solver) {
		this.solver = solver;
	}

	public btDynamicsWorld getCollisionWorld() {
		return collisionWorld;
	}

	public void setCollisionWorld(btDynamicsWorld collisionWorld) {
		this.collisionWorld = collisionWorld;
	}

	public BtContactListener getContactListener() {
		return contactListener;
	}

	public void setContactListener(BtContactListener contactListener) {
		this.contactListener = contactListener;
	}

	Map<Long, BtObject> physicsObjects;

	
	btCollisionConfiguration collisionConfiguration;
	btCollisionDispatcher dispatcher;
	btBroadphaseInterface broadphase;
	btConstraintSolver solver;
	btDynamicsWorld collisionWorld;
	Vector3 tempVector3=new Vector3();
	Matrix4 tempMatrix4=new Matrix4();
	BtContactListener contactListener=null;
	
	
	public BtWorld() {
		super();
		initBtWorld(this.gravity);
	}
	
	public BtWorld(Vector3 gravity) {
		super();
		initBtWorld(gravity);
	}
	
	private void initBtWorld(Vector3 gravity){
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

		for (BtObject btObject : physicsObjects.values()) {
			//System.out.println(btObject.rigidBody.getWorldTransform());
			btObject.update(delta);
			/*btObject.rigidBody.getWorldTransform().getTranslation(tempVector3);
			if (tempVector3.y<-100){//死亡高度判断
				removePhysicsObject(btObject);
			}*/
		}
		for (int i = 0; i < addPhysicsObjectQueue.size(); i++) {
			BtObject btObject=addPhysicsObjectQueue.poll();
			getPhysicsObjects().put(btObject.getId(),btObject);
			getCollisionWorld().addRigidBody(btObject.getRigidBody());
		}
		for (int i = 0; i <  removePhysicsObjectQueue.size(); i++) {
			BtObject btObject= removePhysicsObjectQueue.poll();
			if(getPhysicsObjects().get(btObject.getId())!=null){
				getCollisionWorld().removeRigidBody(btObject.getRigidBody());
				getPhysicsObjects().remove(btObject.getId());
				btObject.dispose();
			}
		}
		collisionDetect();
		collisionWorld.stepSimulation(delta,5);
		

		
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
	private void collisionDetect(){
			
		for (int i = 0; i < dispatcher.getNumManifolds(); i++) {
			if (contactListener!=null){
				contactListener.contect(dispatcher.getManifoldByIndexInternal(i));
			}
		}
		
	}

	@Override
	public void setContactListener(ContactListener contactListener) {
		// TODO Auto-generated method stub
		this.contactListener=(BtContactListener) contactListener;
	}


	@Override
	public void updatePhysicsObject(UPDATE_BTOBJECT_MOTIONSTATE message) {
		// TODO Auto-generated method stub
		
	}

	public void addPhysicsObject(BtObject btObject){
		addPhysicsObjectQueue.add(btObject);
		
	}

	public void removePhysicsObject(BtObject btObject){
		if(btObject!=null){
			removePhysicsObjectQueue.add(btObject);
		}
	}

}
