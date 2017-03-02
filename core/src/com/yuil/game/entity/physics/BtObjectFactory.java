package com.yuil.game.entity.physics;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.gameobject.GameObjectType;

public class BtObjectFactory {
	ModelBuilder modelBuilder = new ModelBuilder();
	Vector3 tempVector = new Vector3();
	Random random=new Random();

	public Model defaultBallModel;
	public Model defaultBoxModel;

	public Model defaultGroundModel;
	public Model defaultPlayerModel;

	public BtObjectFactory(boolean haveDefaultModel) {
		if (haveDefaultModel) {
			defaultBallModel = modelBuilder.createSphere(1f, 1f, 1f, 10,
					10, new Material(ColorAttribute.createDiffuse(new Color(0.7f, 0.1f, 0.1f, 1)),
							ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
					Usage.Position | Usage.Normal);
			
			defaultBoxModel=modelBuilder.createBox(1, 1, 1, new Material(ColorAttribute.createDiffuse(new Color(0.7f, 0.1f, 0.1f, 1)),
							ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
					Usage.Position | Usage.Normal);
			
			defaultGroundModel = modelBuilder.createRect(20f, 0f, -200f, -20f, 0f, -200f, -20f, 0f, 20f, 20f, 0f, 20f, 0,
					1,
					0, new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.4f, 0.6f, 1)),
							ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(16f)),
					Usage.Position | Usage.Normal);
			defaultPlayerModel=modelBuilder.createCylinder(1f, 2f, 1f, 10, new Material(ColorAttribute.createDiffuse(Color.OLIVE)), Usage.Position | Usage.Normal);
		}

	}

	public BtObject createRenderableBall() {
		btCollisionShape collisionShape = new btSphereShape(0.5f);
		return createRenderableBtObject(defaultBallModel, collisionShape, 1, MathUtils.random() * 10,
				MathUtils.random() * 10 + 100, MathUtils.random() * 10);
	}

	public BtObject createBall() {
		btCollisionShape collisionShape = new btSphereShape(0.5f);
		BtObject btObject=new BtObject();
		initBtObject(btObject,collisionShape, 1, MathUtils.random() * 10, MathUtils.random() * 10 + 100,
				MathUtils.random() * 10);
		return btObject;
	}

	public RenderableBtObject createRenderableGround() {
		btCollisionShape collisionShape = new btBoxShape(tempVector.set(20, 0, 200));
		RenderableBtObject ground=createRenderableBtObject(defaultGroundModel, collisionShape, 0, 0, 0, 0);
		ground.Attributes.put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.GROUND.ordinal()));
		return ground;
	}
	/*
	public BtObject createGround() {
		return createGround(20, 0, 200);
	}*//*
	public BtObject createGround(float x, float y,float z) {
		btCollisionShape collisionShape = new btBoxShape(tempVector.set(x,y,z));
		BtObject btObject=new BtObject();
		initBtObject(btObject, collisionShape, 0, 0, 0, 0);
		return btObject;
	}*/

	public RenderableBtObject createRenderableBtObject(Model model, btCollisionShape collisionShape, float mass, float x, float y,
			float z) {

		RenderableBtObject btObject = new RenderableBtObject();
		initBtObject(btObject, collisionShape, mass, x, y, z);
		
		ModelInstance instance = new ModelInstance(model);
		btObject.setInstance(instance);;

		return btObject;

	}

	public void initBtObject(BtObject btObject, btCollisionShape collisionShape, float mass, float x, float y,float z){
		Vector3 inertia = new Vector3();

		collisionShape.calculateLocalInertia(mass, inertia);
		btRigidBodyConstructionInfo rigidBodyConstructionInfo = new btRigidBodyConstructionInfo(mass, null,
				collisionShape, inertia);
		btDefaultMotionState motionState = new btDefaultMotionState();
		motionState.setWorldTransform(new Matrix4(new Vector3(x, y, z), new Quaternion(), new Vector3(1,1,1)));
		btRigidBody rigidBody = new btRigidBody(rigidBodyConstructionInfo);
		rigidBody.setMotionState(motionState);
		rigidBody.userData=btObject;
		//rigidBody.setFriction(0f);
		
		btObject.setId(random.nextLong());
		btObject.setRigidBody(rigidBody);
		btObject.setCollisionShape(collisionShape);
		btObject.setRigidBodyConstructionInfo(rigidBodyConstructionInfo);
	}
/*	public BtObject createBtObject(btCollisionShape collisionShape, float mass, float x, float y, float z) {
		Vector3 inertia = new Vector3();
		collisionShape.calculateLocalInertia(mass, inertia);

		btRigidBodyConstructionInfo rigidBodyConstructionInfo = new btRigidBodyConstructionInfo(mass, null,
				collisionShape, inertia);
		btDefaultMotionState motionState = new btDefaultMotionState();

		motionState.setWorldTransform(new Matrix4(new Vector3(x, y, z), new Quaternion(), new Vector3(1, 1, 1)));
		btRigidBody rigidBody = new btRigidBody(rigidBodyConstructionInfo);
		rigidBody.setMotionState(motionState);

		return new BtObject(random.nextLong(),rigidBody, motionState, collisionShape, rigidBodyConstructionInfo);

	}*/
	
	public btCollisionShape getDefaultSphereShape(){
		return new btSphereShape(0.5f);
	}
	
	public btCollisionShape getDefaultGroundShape(){
		return new btBoxShape(tempVector.set(20, 0, 20));
	}
	
	public btCollisionShape getDefaultCylinderShape(){
		return new btCylinderShape(tempVector.set(0.5f,1f,0.5f));
	}

	public BtObject createBtObject(btCollisionShape collisioneShape, int mass, float x,
			float y, float z) {
		// TODO Auto-generated method stub
		BtObject btObject=new BtObject();
		initBtObject(btObject, collisioneShape, mass, x, y, z);
		return btObject;
	}
	
	
	public RenderableBtObject createRenderableBall(float radius,float mass,Vector3 position,Color color){
	
		Model model = modelBuilder.createSphere(radius*2,radius*2,radius*2, 10,
				10, new Material(ColorAttribute.createDiffuse(color),
						ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
				Usage.Position | Usage.Normal);

		btCollisionShape collisionShape = new btSphereShape(radius);
		RenderableBtObject renderableBtObject=createRenderableBtObject(model, collisionShape, mass, position.x,position.y, position.z);
		//renderableBtObject.getRigidBody().getCollisionShape().setLocalScaling(new Vector3(radius,radius,radius));

		return  renderableBtObject;
	}
	public BtObject createBall(float radius,float mass,Vector3 position){
		BtObject btObject=new BtObject();
		btCollisionShape collisionShape = new btSphereShape(1);
		collisionShape.setLocalScaling(new Vector3(radius,radius,radius));
		initBtObject(btObject,collisionShape, mass, position.x,position.y, position.z);
		return btObject;
	}
}
