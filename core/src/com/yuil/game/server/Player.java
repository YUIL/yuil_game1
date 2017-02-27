package com.yuil.game.server;

public class Player {
	long id;
	long btObjectId;
	
	
	
	public Player() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Player(long id, long btObjectId) {
		super();
		this.id = id;
		this.btObjectId = btObjectId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBtObjectId() {
		return btObjectId;
	}
	public void setBtObjectId(long btObjectId) {
		this.btObjectId = btObjectId;
	}
	
	
}
