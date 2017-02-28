package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.yuil.game.MyGame;
import com.yuil.game.entity.BtObject;
import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.PhysicsObject;
import com.yuil.game.entity.PhysicsWorld;
import com.yuil.game.entity.PhysicsWorldBuilder;
import com.yuil.game.entity.RenderableBtObject;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.message.UPDATE_BTOBJECT_MOTIONSTATE;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.ActorInputListenner;
import com.yuil.game.input.InputManager;
import com.yuil.game.input.KeyboardStatus;

public class RigidBodyTestScreen extends Screen2D{
	
	boolean turnLeft=true;
	long nextTurnTime=0;
	
		
	PhysicsWorldBuilder physicsWorldBuilder;
	PhysicsWorld physicsWorld;
	Environment lights;
	
	KeyboardStatus keyboardStatus=new KeyboardStatus();
	
	BtObject testBtObject;
	
	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch=new ModelBatch();
	
	Random random=new Random();
	
	Sound sound=Gdx.audio.newSound(Gdx.files.internal("sound/bee.wav"));
	
	Matrix4 tempMatrix4=new Matrix4();
	Vector3 tempVector3=new Vector3();
	UPDATE_BTOBJECT_MOTIONSTATE temp_update_rigidbody_message;
	UPDATE_LINEAR_VELOCITY temp_update_liner_velocity_message=new UPDATE_LINEAR_VELOCITY();
	boolean isLogin=false;
	public RigidBodyTestScreen(MyGame game) {
		super(game);
		
		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/RigidBodyTestScreen.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));
		
		physicsWorldBuilder =new PhysicsWorldBuilder(true);
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(physicsWorldBuilder.btObjectFactory.createRenderableGround());
	
		// Set up the camera
		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		if (width > height)
			camera = new PerspectiveCamera(67f, 3f * width / height, 3f);
		else
			camera = new PerspectiveCamera(67f, 3f, 3f * height / width);
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.update();
		camController = new CameraInputController(camera);
		
		setupActorInput();
		InputManager.setInputProcessor(stage,camController);
		
	}

	@Override
	public void render(float delta) {
		checkKeyBoardStatus();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		physicsWorld.update(delta);
		modelBatch.begin(camera);

		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {
			ModelInstance modelInstance=((RenderableBtObject)physicsObject).getInstance();
			((BtObject)physicsObject).getRigidBody().getWorldTransform(modelInstance.transform);
			modelBatch.render(modelInstance,lights);
		}
		modelBatch.end();
		super.render(delta);
	}

	
	void checkKeyBoardStatus(){
		
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			
		}else if (Gdx.input.isKeyPressed(Keys.Q)==false&& keyboardStatus.isqJustPressed()) {
			keyboardStatus.setqJustPressed(false);
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.A)) {
			// game.getScreen().dispose();
			keyboardStatus.setaJustPressed(true);
			aJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.A)==false&& keyboardStatus.isaJustPressed()) {
			keyboardStatus.setaJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.D)){
				dJustPressedAction();
			}else{
				aJustUppedAction();
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			// game.getScreen().dispose();
			keyboardStatus.setdJustPressed(true);
			dJustPressedAction();
		}else if (Gdx.input.isKeyPressed(Keys.D)==false&& keyboardStatus.isdJustPressed()) {
			keyboardStatus.setdJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.A)){
				aJustPressedAction();
			}else{
				dJustUppedAction();
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			// game.getScreen().dispose();
			keyboardStatus.setSpaceJustPressed(true);
			spaceJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.SPACE)==false&& keyboardStatus.isdJustPressed()) {
			keyboardStatus.setSpaceJustPressed(false);
			spaceJustUppedAction();
		}
		
	}
	void setupActorInput(){
		stage.getRoot().findActor("A").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				aJustUppedAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				aJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("D").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				dJustUppedAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				dJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("Z").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				zJustPressedAction();
			}
		});
		stage.getRoot().findActor("X").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				delJustPressedAction();
			}
		});
		stage.getRoot().findActor("G").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//gameObjectId = Long.parseLong(((TextArea) stage.getRoot().findActor("userName")).getText());

			}
		});

		stage.getRoot().findActor("W").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				wJustPressedAction() ;
			}
		});
		
		stage.getRoot().findActor("S").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sJustPressedAction() ;
			}
		});

		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});	
	}

	protected void zJustPressedAction() {
		testBtObject=physicsWorldBuilder.createObstacleRenderableBall(1, 1, new Vector3(0,0,0),new Color(0f,1f,0f,1f));
		physicsWorld.addPhysicsObject(testBtObject);
	}

	protected void dJustPressedAction() {
		//ColorAttribute ca=ColorAttribute.createDiffuse(new Color(0f, 0f, 0f, 1));
		ColorAttribute ca=(ColorAttribute)(((RenderableBtObject)testBtObject).getInstance().nodes.get(0).parts.get(0).material.get(ColorAttribute.Diffuse));
		ca.color.set(0, 0, 0, 1);
	//System.out.println(material.size());
	}

	protected void dJustUppedAction() {
	}

	protected void aJustPressedAction() {
		System.out.println(testBtObject.getRigidBody().getInvMass());
	}

	protected void aJustUppedAction() {
	}
	
	protected void wJustPressedAction() {
		spaceJustPressedAction();
	}

	protected void wJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void sJustPressedAction() {
//		if(btObject!=null){
//			physicsWorld.updatePhysicsObject(tempMessage);
//		}
	}
	
	protected void spaceJustPressedAction() {
		tempVector3.set(testBtObject.getRigidBody().getLinearVelocity());
		tempVector3.y=10;
		testBtObject.getRigidBody().setLinearVelocity(tempVector3);
		
	}

	protected void spaceJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	protected void delJustPressedAction() {
		
	}

	protected void delJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	public void updatePhysicsObject(BtObject btObject,UPDATE_BTOBJECT_MOTIONSTATE message){
		//btMotionState tempMotionState =new btMotionState();
	
		//btObject.getRigidBody().setMotionState(tempMotionState);
		tempMatrix4.set(message.getTransformVal());
		btObject.getRigidBody().setWorldTransform(tempMatrix4);
		//btObject.getMotionState().setWorldTransform(tempMatrix4);
		
		tempVector3.x=message.getLinearVelocityX();
		tempVector3.y=message.getLinearVelocityY();
		tempVector3.z=message.getLinearVelocityZ();
		btObject.getRigidBody().setLinearVelocity(tempVector3);
		
		tempVector3.x=message.getAngularVelocityX();
		tempVector3.y=message.getAngularVelocityY();
		tempVector3.z=message.getAngularVelocityZ();
		btObject.getRigidBody().setAngularVelocity(tempVector3);
	}
}
