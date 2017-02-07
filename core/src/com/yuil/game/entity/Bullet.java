package com.yuil.game.entity;

public class Bullet extends AliveObject{
	int attack=1;
	
	

	public Bullet() {
		super();
		this.h=20;
		// TODO Auto-generated constructor stub
	}

	public Bullet(int attack) {
		super();
		this.attack = attack;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}
	
}
