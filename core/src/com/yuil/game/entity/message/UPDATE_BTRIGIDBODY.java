package com.yuil.game.entity.message;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.yuil.game.entity.BtObject;
import com.yuil.game.net.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class UPDATE_BTRIGIDBODY implements Message {
	public final int type = EntityMessageType.UPDATE_BTRIGIDBODY.ordinal();

	long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	float[] transformVal = new float[16];
	float linearVelocityX;
	float linearVelocityY;
	float linearVelocityZ;
	float angularVelocityX;
	float angularVelocityY;
	float angularVelocityZ;

	public UPDATE_BTRIGIDBODY() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UPDATE_BTRIGIDBODY(BtObject btObject) {
		super();
		this.set(btObject);
	}

	public Message set(BtObject btObject) {
		this.setId(btObject.getId());
		btRigidBody rigidBody = btObject.getRigidBody();

		if(rigidBody==null){
			System.out.println("!@#!@#!@#!@#!@#");
		}
		float[] val = rigidBody.getWorldTransform().val;
		for (int i = 0; i < transformVal.length; i++) {
			transformVal[i] = val[i];
		}
		this.setLinearVelocityX(rigidBody.getLinearVelocity().x);
		this.setLinearVelocityY(rigidBody.getLinearVelocity().y);
		this.setLinearVelocityZ(rigidBody.getLinearVelocity().z);

		this.setAngularVelocityX(rigidBody.getAngularVelocity().x);
		this.setAngularVelocityY(rigidBody.getAngularVelocity().y);
		this.setAngularVelocityZ(rigidBody.getAngularVelocity().z);

		return this;
	}

	@Override
	public Message set(ByteBuf buf) {
		buf.readByte();
		this.id = buf.readLong();
		for (int i = 0; i < transformVal.length; i++) {
			this.transformVal[i] = buf.readFloat();
		}

		this.linearVelocityX = buf.readFloat();
		this.linearVelocityY = buf.readFloat();
		this.linearVelocityZ = buf.readFloat();

		this.angularVelocityX = buf.readFloat();
		this.angularVelocityY = buf.readFloat();
		this.angularVelocityZ = buf.readFloat();

		return this;
	}

	@Override
	public ByteBuf get() {
		ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(64 + 12 + 12 + Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.getId());

		for (int i = 0; i < transformVal.length; i++) {
			buf.writeFloat(transformVal[i]);
		}
		buf.writeFloat(linearVelocityX);
		buf.writeFloat(linearVelocityY);
		buf.writeFloat(linearVelocityZ);

		buf.writeFloat(angularVelocityX);
		buf.writeFloat(angularVelocityY);
		buf.writeFloat(angularVelocityZ);

		return buf;
	}

	public float[] getTransformVal() {
		return transformVal;
	}

	public void setTransformVal(float[] transformVal) {
		this.transformVal = transformVal;
	}

	public float getLinearVelocityX() {
		return linearVelocityX;
	}

	public void setLinearVelocityX(float linearVelocityX) {
		this.linearVelocityX = linearVelocityX;
	}

	public float getLinearVelocityY() {
		return linearVelocityY;
	}

	public void setLinearVelocityY(float linearVelocityY) {
		this.linearVelocityY = linearVelocityY;
	}

	public float getLinearVelocityZ() {
		return linearVelocityZ;
	}

	public void setLinearVelocityZ(float linearVelocityZ) {
		this.linearVelocityZ = linearVelocityZ;
	}

	public float getAngularVelocityX() {
		return angularVelocityX;
	}

	public void setAngularVelocityX(float angularVelocityX) {
		this.angularVelocityX = angularVelocityX;
	}

	public float getAngularVelocityY() {
		return angularVelocityY;
	}

	public void setAngularVelocityY(float angularVelocityY) {
		this.angularVelocityY = angularVelocityY;
	}

	public float getAngularVelocityZ() {
		return angularVelocityZ;
	}

	public void setAngularVelocityZ(float angularVelocityZ) {
		this.angularVelocityZ = angularVelocityZ;
	}

	public int getType() {
		return type;
	}

}
