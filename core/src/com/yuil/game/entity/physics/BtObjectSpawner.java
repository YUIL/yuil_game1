package com.yuil.game.entity.physics;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class BtObjectSpawner {
	public long interval;//millionsecond
	public long nextSpawnTime;
	public static Queue<BtObject>  reuseQueue=new ConcurrentLinkedDeque<BtObject>();

	
	
	public BtObjectSpawner(long interval) {
		super();
		this.interval = interval;
	}



	public long getInterval() {
		return interval;
	}



	public void setInterval(long interval) {
		this.interval = interval;
	}



	public long getNextSpawnTime() {
		return nextSpawnTime;
	}



	public void setNextSpawnTime(long nextSpawnTime) {
		this.nextSpawnTime = nextSpawnTime;
	}

	

	public void update(){
		if(System.currentTimeMillis()>=nextSpawnTime){
			nextSpawnTime=System.currentTimeMillis()+interval;
			spawn();
		}
	}
	
	public abstract void spawn();
	
	
}