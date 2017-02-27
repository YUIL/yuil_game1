package com.yuil.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class PhysicsWorldBuilder {
	public BtObjectFactory btObjectFactory;
	
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
		return btObjectFactory.createGround();
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
