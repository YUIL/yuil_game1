package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.yuil.game.MyGame;
import com.yuil.game.entity.message.EntityMessageType;
import com.yuil.game.entity.message.*;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.ActorInputListenner;
import com.yuil.game.input.InputManager;
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MESSAGE_ARRAY;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.ClientSocket;

import io.netty.buffer.ByteBuf;

public class RunCmdScreen extends Screen2D implements MessageListener {
	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();
	
	boolean runGm=true;
	


	long interval = 100;
	long nextTime = 0;



	boolean isLogin = false;
	
	
	//几个固定消息的初始化
	RUN_CMD buttonA_message=new RUN_CMD(1);
	RUN_CMD buttonB_message=new RUN_CMD(2);
	RUN_CMD buttonC_message=new RUN_CMD(3);
	RUN_CMD buttonD_message=new RUN_CMD(4);
	RUN_CMD buttonE_message=new RUN_CMD(5);
	RUN_CMD buttonF_message=new RUN_CMD(6);
	RUN_CMD buttonG_message=new RUN_CMD(7);
	RUN_CMD buttonH_message=new RUN_CMD(8);


	public RunCmdScreen(MyGame game) {
		super(game);
		clientSocket = new ClientSocket(9092, "123.57.14.122", 9091, this);
		initMessageHandle();

		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/RunCmdScreen.xml";
		guiFactory.setStage(stage, guiXmlPath);

		setupInput();
		InputManager.setInputProcessor(stage);

		sendSingleMessage(new TEST());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		super.render(delta);
	}

	@Override
	public void recvMessage(Session session, ByteBuf data) {
		/*
		 * try { Runtime.getRuntime().exec(
		 * "cmd /k start /MIN d:\\bat\\test1.bat" ); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// TODO Auto-generated method stub
		if (data.array().length < Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		// System.out.println("type:" + GameMessageType.values()[typeOrdinal]);

		switch (MessageType.values()[typeOrdinal]) {
		case MESSAGE_ARRAY:
			MESSAGE_ARRAY message_ARRAY = new MESSAGE_ARRAY(data);
			for (ByteBuf data1 : message_ARRAY.gameMessages) {
				disposeSingleMessage(session, data1);
			}
			break;
		case SINGLE_MESSAGE:
			data.skipBytes(Message.TYPE_LENGTH);
			data.discardReadBytes();
			disposeSingleMessage(session, data);
			break;
		default:
			break;
		}
	}

	void disposeSingleMessage(Session session, ByteBuf data) {
		if (data.array().length < Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		messageHandlerMap.get(typeOrdinal).handle(data);
	}

	void setupInput() {
		stage.getRoot().findActor("ButtonA").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//sendSingleMessage(new TEST());
				sendSingleMessage(buttonA_message);
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonB").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sendSingleMessage(buttonB_message);

			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonC").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sendSingleMessage(buttonC_message);

			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonD").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sendSingleMessage(buttonD_message);

			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonE").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//sendSingleMessage(new TEST());
				sendSingleMessage(buttonE_message);
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonF").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sendSingleMessage(buttonF_message);

			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonG").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sendSingleMessage(buttonG_message);

			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		stage.getRoot().findActor("ButtonH").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				sendSingleMessage(buttonH_message);

			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
	}

	void sendSingleMessage(Message message) {

		clientSocket.send(SINGLE_MESSAGE.get(message.get().array()).array(), false);

	}

	void sendSingleMessage(byte[] data) {
		clientSocket.send(SINGLE_MESSAGE.get(data).array(), false);
	}

	void initMessageHandle() {
		messageHandlerMap.put(EntityMessageType.RUN_CMD.ordinal(), new MessageHandler() {

			@Override
			public void handle(ByteBuf src) {
				RUN_CMD message= new RUN_CMD(src);
				((Label)(stage.getRoot().findActor("console"))).setText(Integer.toString(message.getId()));
				if (runGm){
					String path=null;
					switch (message.getId()) {
					case 1:
						path="d:\\bat\\remote\\click_a.bat";
						break;
					case 2:
						path="d:\\bat\\remote\\click_b.bat";
						break;
					case 3:
						path="d:\\bat\\remote\\click_c.bat";
						break;
					case 4:
						path="d:\\bat\\remote\\click_d.bat";
						break;
						
					case 5:

						path="d:\\bat\\remote\\click_jiaobenstart.bat";
						break;
						
					case 8:

						path="d:\\bat\\remote\\sdr.bat";
						break;

					default:
						break;
					}
					if(path!=null){
						try {
							Runtime.getRuntime().exec("cmd /k start /MIN "+path );
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
				}

			}
		});

		messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				isLogin = true;
			}
		});

	}
}
