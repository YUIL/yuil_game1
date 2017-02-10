package com.yuil.game.entity;

public class PhysicsWorldBuilder {
	public BtObjectFactory btObjectFactory;
	
	public PhysicsWorldBuilder() {
		super();
		btObjectFactory=new BtObjectFactory(true);
	}
	
	public BtObject createDefaultBall(int mass,float x,float y,float z){
		return btObjectFactory.createBtObject(btObjectFactory.getDefaultSphereShape(), mass, x, y, z);
	}
	public RenderableBtObject createDefaultRenderableBall(int mass,float x,float y,float z){
		return btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), mass, x, y, z);
	}

	public BtObjectFactory getBtObjectFactory() {
		return btObjectFactory;
	}

	public void setBtObjectFactory(BtObjectFactory btObjectFactory) {
		this.btObjectFactory = btObjectFactory;
	}

}
