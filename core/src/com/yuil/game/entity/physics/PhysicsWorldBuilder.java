package com.yuil.game.entity.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class PhysicsWorldBuilder {
	public BtObjectFactory btObjectFactory;
	Vector3 tempVector = new Vector3();
	
	public PhysicsWorldBuilder(boolean haveDefaultModel) {
		super();
		btObjectFactory=new BtObjectFactory(haveDefaultModel);
	}
	
	public BtObject createDefaultBall(float x,float y,float z){
		return btObjectFactory.createBtObject(btObjectFactory.getDefaultSphereShape(), 1, x, y, z);
	}
	public RenderableBtObject createDefaultRenderableBall(float x,float y,float z){
		return btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, x, y, z);
	}

	public BtObject createDefaultGround(){
		btCollisionShape collisionShape = new btBoxShape(tempVector.set(20, 0, 200));
		BtObject btObject=new BtObject();
		btObjectFactory.initBtObject(btObject, collisionShape, 0, 0, 0, 0);
		return btObject;
	}
	public RenderableBtObject createDefaultRenderableGround(){
		return btObjectFactory.createRenderableGround();
	}
	
	public BtObject createObstacleBall(float radius ,int mass,Vector3 position){
		return btObjectFactory.createBall(radius, mass, position);
	}
	public RenderableBtObject createObstacleRenderableBall(float radius ,int mass,Vector3 position,Color color){
		return btObjectFactory.createRenderableBall(radius, mass, position, color);
	}

	
	
	public BtObjectFactory getBtObjectFactory() {
		return btObjectFactory;
	}

	public void setBtObjectFactory(BtObjectFactory btObjectFactory) {
		this.btObjectFactory = btObjectFactory;
	}

}
