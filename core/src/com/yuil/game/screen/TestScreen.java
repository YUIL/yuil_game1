package com.yuil.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.yuil.game.MyGame;
import com.yuil.game.entity.BtObject;
import com.yuil.game.entity.BtObjectFactory;
import com.yuil.game.entity.BtWorld;
import com.yuil.game.entity.PhysicsObject;
import com.yuil.game.entity.PhysicsWorld;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.InputManager;

public class TestScreen extends Screen2D {
	PhysicsWorld physicsWorld;
	Environment lights;


	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch=new ModelBatch();
	BtObjectFactory btObjectFactory=new BtObjectFactory(true);
	
	long interval=50;
	long lastTime=0;
	public TestScreen(MyGame game) {
		super(game);
		// TODO Auto-generated constructor stub
		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/TestScreen.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));
		
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(btObjectFactory.createGround());

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
		
		InputManager.setInputProcessor(stage,camController);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		

		if (System.currentTimeMillis()-lastTime>interval){
			lastTime=System.currentTimeMillis();
			physicsWorld.addPhysicsObject(btObjectFactory.createBall());
		}

		physicsWorld.update(delta);

		
		modelBatch.begin(camera);

		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {
			ModelInstance modelInstance=(ModelInstance)(((BtObject)physicsObject).getRigidBody().userData);
			((BtObject)physicsObject).getRigidBody().getWorldTransform(modelInstance.transform);
			
			modelBatch.render(modelInstance,lights);
		}
		modelBatch.end();
		super.render(delta);
	}
}
