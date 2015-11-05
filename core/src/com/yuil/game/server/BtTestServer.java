package com.yuil.game.server;

import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;

public class BtTestServer {

	BtWorld physicsWorld=new BtWorld();
	volatile Thread gameWorldThread;
	
	BtObjectFactory btObjectFactory=new BtObjectFactory(false);
	
	
	public static void main(String[] args) {
		BtTestServer btTestServer=new BtTestServer();
		btTestServer.start();
	}

	
	public BtTestServer(){
		physicsWorld.addPhysicsObject(btObjectFactory.createBallNoModel());
	}
	public void start(){
		gameWorldThread=new Thread(new WorldLogic());
		gameWorldThread.start();
	}
	
	class WorldLogic implements Runnable{

		int interval=100;
		long lastUpdateTime=0;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				if (System.currentTimeMillis()-lastUpdateTime>interval) {
					lastUpdateTime=System.currentTimeMillis();
					physicsWorld.update(interval);
				}
			}
		}
		
	}
}
