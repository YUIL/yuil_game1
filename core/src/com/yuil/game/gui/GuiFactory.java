package com.yuil.game.gui;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class GuiFactory {
	public static Skin defaultSkin= new Skin(Gdx.files.internal("data/uiskin.json"));
    XmlReader reader;


    public GuiFactory() {
        reader = new XmlReader();

    }
    
    
    /**
     * 从XML中解析一个Stage对象
     */
/*    public Stage getStageFromXml(String guiXmlPath, Skin skin) {
        Stage stage = new Stage(new ScreenViewport());
        setStageFromXml(stage, guiXmlPath, skin);
        return stage;
    }*/

    public void setStage(Stage stage, String guiXmlPath) {
        setStage(stage, guiXmlPath, defaultSkin);
    }

    @SuppressWarnings("unused")
	private Array<Actor> getActors(Element element, Skin skin) {
        Array<Actor> actors = new Array<Actor>();
        Array<?> nodes = element.getChildrenByName("actor");
        for (Iterator<?> it = nodes.iterator(); it.hasNext(); ) {
            Element actorElm = (Element) it.next();

            Actor actor=null;
            String type = actorElm.getAttribute("type");
            if (type.equals("button")) {
                actor=getButton(actorElm, skin);
            }else
            if (type.equals("textButton")) {
                actor=getTextButton(actorElm, skin);
            }else
            if (type.equals("textArea")) {
                actor= getTextArea(actorElm, skin);
            }else

            if (type.equals("label")) {
                actor=getLabel(actorElm, skin);
            }
            actors.add(actor);
        }
        return actors;

    }

    /**
     * 从XML中读取并配置一个Stage对象
     */
    public void setStage(Stage stage, String guiXmlPath, Skin skin) {
        Element root = null;
        try {
            root = reader.parse(Gdx.files.internal(guiXmlPath));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addChildren(root,stage.getRoot(),skin);

    }

    /**
     * 根据Name从XML中得到一个Actor
     */
    public Actor getActor(String guiXmlPath, String name, Skin skin) {

        Element root = null;
        try {
            root = reader.parse(Gdx.files.internal(guiXmlPath));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Array<?> nodes = root.getChildrenByName("actor");
        for (Iterator<?> it = nodes.iterator(); it.hasNext(); ) {
            Element actorElm = (Element) it.next();
            String name2 = actorElm.getAttribute("name");
            if (name.equals(name2)) {
                String type = actorElm.getAttribute("type");
                if (type.equals("button")) {
                    return getButton(actorElm, skin);
                }
                if (type.equals("textButton")) {
                    return getTextButton(actorElm, skin);
                }
                if (type.equals("textArea")) {
                    return getTextArea(actorElm, skin);
                }

                if (type.equals("label")) {
                    return getLabel(actorElm, skin);
                }
            }
        }
        return null;
    }

    /**
     * 从Element中解析一个Drawble对象
     */
    protected Drawable getDrawable(Element elm) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(
                Gdx.files.internal(elm.getChildByName("path").getText())),
                Integer.parseInt(elm.getChildByName("x").getText()),
                Integer.parseInt(elm.getChildByName("y").getText()),
                Integer.parseInt(elm.getChildByName("w").getText()),
                Integer.parseInt(elm.getChildByName("h").getText())));
    }

    /**
     * 从Element中读取并设置actor的一般属性
     */
    protected void setActorAttribute(Actor actor, Element actorElm) {
        actor.setX(Float.parseFloat(actorElm.getChildByName("x").getText()));
        actor.setY(Float.parseFloat(actorElm.getChildByName("y").getText()));
        actor.setName(actorElm.getAttribute("name"));
    }

    /**
     * 从Element中解析一个Button对象
     */
    private Button getButton(Element actorElm, Skin skin) {

        Button button;
        Element skinElm = actorElm.getChildByName("skin");
        if (skinElm != null) {
            button = new Button(skin.get(skinElm.getAttribute("name"),
                    ButtonStyle.class));
        } else {
            Drawable up = getDrawable(actorElm.getChildByName("up"));
            Drawable down = getDrawable(actorElm.getChildByName("down"));
            button = new Button(up, down);
        }
        addChildren(actorElm, button,skin);
        setActorAttribute(button, actorElm);
        return button;
    }

    private void addChildren(Element actorElm, Group group,Skin skin) {

        Array<?> nodes = actorElm.getChildrenByName("actor");
        if (nodes.size>0){
            for (Iterator<?> it = nodes.iterator(); it.hasNext(); ) {
                Element actorElm1 = (Element) it.next();
                String type = actorElm1.getAttribute("type");
                Actor actor = null;
                if (type.equals("button")) {
                    actor = getButton(actorElm1, skin);
                }else if (type.equals("textButton")) {
                    actor = getTextButton(actorElm1, skin);
                }else if (type.equals("textArea")) {
                    actor = getTextArea(actorElm1, skin);
                }else if (type.equals("label")) {
                    actor = getLabel(actorElm1, skin);
                }
                if (actor != null) {
                    group.addActor(actor);
                }
            }
        }
    }

    /**
     * 从Element中解析一个TextButton对象
     */
    private TextButton getTextButton(Element actorElm, Skin skin) {

        TextButton textButton = null;
        Element skinElm = actorElm.getChildByName("skin");
        if (skinElm != null) {
            String s = actorElm.getAttribute("text");
            if (s != null) {
                textButton = new TextButton(actorElm.getAttribute("text"),
                        skin.get(skinElm.getAttribute("name"),
                                TextButtonStyle.class));
            } else {
                textButton = new TextButton(actorElm.getAttribute("name"),
                        skin.get(skinElm.getAttribute("name"),
                                TextButtonStyle.class));
            }
            addChildren(actorElm, textButton,skin);
            setActorAttribute(textButton, actorElm);
            return textButton;
        } else {
            System.err.println("不能缺少skin元素！");
            return null;
        }

    }

    /**
     * 从Element中解析一个TextArea对象
     */
    private TextArea getTextArea(Element actorElm, Skin skin) {

        TextArea textArea;
        Element skinElm = actorElm.getChildByName("skin");
        if (skinElm != null) {
            textArea = new TextArea(actorElm.getAttribute("text"), skin);
            setActorAttribute(textArea, actorElm);
            return textArea;
        } else {
            System.err.println("不能缺少skin元素！");
            return null;
        }

    }

    /**
     * 从Element中解析一个Label对象
     */
    private Label getLabel(Element actorElm, Skin skin) {

        Label lable;
        Element skinElm = actorElm.getChildByName("skin");
        if (skinElm != null) {
            lable = new Label(actorElm.getAttribute("text"), skin.get(skinElm.getAttribute("name"), LabelStyle.class));
            setActorAttribute(lable, actorElm);
            return lable;
        } else {
            System.err.println("不能缺少skin元素！");
            return null;
        }

    }
}
