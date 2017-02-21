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
import com.yuil.game.entity.message.UPDATE_BTRIGIDBODY;
import com.yuil.game.server.BtTestServer2;

public class BtWorld extends PhysicsWorld implements Disposable{
	
	Map<Long, BtObject> physicsObjects;
	Queue<BtObject> addPhysicsObjectQueue=new  ConcurrentLinkedQueue<BtObject>();
	Queue<BtObject> removePhysicsObjectQueue=new  ConcurrentLinkedQueue<BtObject>();
	Queue<UPDATE_BTRIGIDBODY> updatePhysicsObjectQueue=new  ConcurrentLinkedQueue<UPDATE_BTRIGIDBODY>();
	Queue<APPLY_FORCE> applyForceQueue=new  ConcurrentLinkedQueue<APPLY_FORCE>();

	
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

		collisionDetect();
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
			physicsObjects.put(btObject.id,btObject);
			collisionWorld.addRigidBody(btObject.getRigidBody());
		}
		for (int i = 0; i < removePhysicsObjectQueue.size(); i++) {
			BtObject btObject=removePhysicsObjectQueue.poll();
			if(physicsObjects.get(btObject.getId())!=null){
				collisionWorld.removeRigidBody(btObject.getRigidBody());
				physicsObjects.remove(btObject.getId());
				btObject.dispose();
			}
		}

		for (int i = 0; i < applyForceQueue.size(); i++) {
			APPLY_FORCE message=applyForceQueue.poll();
			BtObject btObject=physicsObjects.get(message.getId());
			
			if (btObject!=null){
				tempVector3.x=message.getX();
				tempVector3.y=message.getY();
				tempVector3.z=message.getZ();
				btObject.getRigidBody().applyForce(tempVector3, btObject.getPosition());
				BtTestServer2.btObjectBroadCastQueue.add(btObject);
				
			}
				
		}
		
		
		for (int i = 0; i < updatePhysicsObjectQueue.size(); i++) {
			UPDATE_BTRIGIDBODY message=updatePhysicsObjectQueue.poll();
			BtObject btObject=physicsObjects.get(message.getId());
			if (btObject!=null){
				tempMatrix4.set(message.getTransformVal());
				btObject.getRigidBody().setWorldTransform(tempMatrix4);
				
				tempVector3.x=message.getLinearVelocityX();
				tempVector3.y=message.getLinearVelocityY();
				tempVector3.z=message.getLinearVelocityZ();
				btObject.getRigidBody().setLinearVelocity(tempVector3);
				
				tempVector3.x=message.getAngularVelocityX();
				tempVector3.y=message.getAngularVelocityY();
				tempVector3.z=message.getAngularVelocityZ();
				btObject.getRigidBody().setAngularVelocity(tempVector3);
			}	
		}
		collisionWorld.stepSimulation(delta,5);

		
	}
	
	public void addPhysicsObject(BtObject btObject){
		addPhysicsObjectQueue.add(btObject);
		
	}

	public void removePhysicsObject(BtObject btObject){
		if(btObject!=null){
			removePhysicsObjectQueue.add(btObject);
		}
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
	public void updatePhysicsObject(UPDATE_BTRIGIDBODY message) {
		// TODO Auto-generated method stub
		this.updatePhysicsObjectQueue.add(message);
	}

	@Override
	public void applyForce(APPLY_FORCE message) {
		// TODO Auto-generated method stub
		this.applyForceQueue.add(message);
	}

}
