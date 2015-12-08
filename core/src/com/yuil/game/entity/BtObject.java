package com.yuil.game.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Disposable;

public class BtObject extends PhysicsObject implements Disposable{
	//ModelInstance instance;
	btRigidBody rigidBody;
	btDefaultMotionState motionState;
	btCollisionShape collisionShape;
	btRigidBodyConstructionInfo  rigidBodyConstructionInfo;
	public Object userData;
	
	private Vector3 position=new Vector3();

	public BtObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BtObject(long id,btRigidBody rigidBody, btDefaultMotionState motionState,
			btCollisionShape collisionShape, btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
		super();
		this.id=id;
		this.rigidBody = rigidBody;
		this.motionState = motionState;
		this.collisionShape = collisionShape;
		this.rigidBodyConstructionInfo = rigidBodyConstructionInfo;
		
	}

	public void update(float delta){
	}
	public  Matrix4 getTransform(){
		return this.rigidBody.getWorldTransform();
	}

	
	
	public Vector3 getPosition() {
		return this.rigidBody.getWorldTransform().getTranslation(position);
	}

	public void setPosition(Vector3 position) {
		this.rigidBody.getWorldTransform().setTranslation(position);
	}

	public btDefaultMotionState getMotionState() {
		return motionState;
	}




	public void setMotionState(btDefaultMotionState motionState) {
		this.motionState = motionState;
	}




	public btRigidBodyConstructionInfo getRigidBodyConstructionInfo() {
		return rigidBodyConstructionInfo;
	}




	public void setRigidBodyConstructionInfo(btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
		this.rigidBodyConstructionInfo = rigidBodyConstructionInfo;
	}




	public btCollisionShape getCollisionShape() {
		return collisionShape;
	}




	public void setCollisionShape(btCollisionShape collisionShape) {
		this.collisionShape = collisionShape;
	}



	public btRigidBody getRigidBody() {
		return rigidBody;
	}




	public void setRigidBody(btRigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}




	@Override
	public  void dispose() {
		// TODO Auto-generated method stub
		synchronized (this) {
			motionState.dispose();
			rigidBodyConstructionInfo.dispose();
			collisionShape.dispose();
			rigidBody.dispose();
			this.motionState=null;
			this.rigidBodyConstructionInfo=null;
			this.collisionShape=null;
			this.rigidBody=null;
		}
	}

	
	
}
