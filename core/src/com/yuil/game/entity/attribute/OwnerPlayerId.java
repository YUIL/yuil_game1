package com.yuil.game.entity.attribute;

public class OwnerPlayerId implements Attribute {
	public final Integer type=AttributeType.OWNER_PLAYER_ID.ordinal();
	public long playerId;
	
	
	public OwnerPlayerId(long playerId) {
		super();
		this.playerId = playerId;
	}
	
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
