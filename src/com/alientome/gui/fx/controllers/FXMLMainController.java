package com.alientome.gui.fx.controllers;

import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.events.QuitRequestEvent;
import com.alientome.editors.level.LevelEditor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;

import java.io.IOException;

import static com.alientome.core.events.GameEventType.*;

public class FXMLMainController extends FXMLController {

    @Override
    public void init(Scene scene) throws IOException {

        scene.getRoot().lookupAll("*").forEach(node -> {
            if (node instanceof Labeled) {
                Labeled labeled = (Labeled) node;
                if (labeled.getId().equals("version-label"))
                    context.getI18N().applyBindTo(labeled, context.getConfig().getProperty("version"));
                else
                    context.getI18N().applyBindTo(labeled);
            }
        });

        GameEventDispatcher dispatcher = context.getDispatcher();

        dispatcher.register(GAME_START, e -> context.getSoundManager().stopPlaying("music.main"));
        dispatcher.register(GAME_EXIT, e -> context.getSoundManager().playLooping("music.main"));
        dispatcher.register(QUIT, e -> {
            manager.exit();
            LevelEditor.exit();
        });

        context.getSoundManager().playLooping("music.main");
    }

    @FXML
    private Button play, options, quit;

    @FXML
    private void onButtonAction(ActionEvent e) {

        Object s = e.getSource();

             if (s == play) manager.pushScene("PLAY");
        else if (s == options) manager.pushScene("OPTIONS");
        else if (s == quit) context.getDispatcher().submit(new QuitRequestEvent());
    }
}
