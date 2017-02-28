package com.yuil.game.entity.attribute;

public class DamagePoint implements Attribute {

	public final Integer type=AttributeType.DAMAGE_POINT.ordinal();
	public long damagePoint;
	
	
	
	public DamagePoint(long damagePoint) {
		super();
		this.damagePoint = damagePoint;
	}

	public long getDamagePoint() {
		return damagePoint;
	}

	public void setDamagePoint(long damagePoint) {
		this.damagePoint = damagePoint;
	}

	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
