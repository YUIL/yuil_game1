package com.yuil.game.entity.attribute;

public class HealthPoint implements Attribute {
	public final Integer type=AttributeType.HEALTH_POINT.ordinal();
	public long healthPoint;
	
	
	
	public HealthPoint(long healthPoint) {
		super();
		this.healthPoint = healthPoint;
	}
	public long getHealthPoint() {
		return healthPoint;
	}
	public void setHealthPoint(long healthPoint) {
		this.healthPoint = healthPoint;
	}
	
	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
