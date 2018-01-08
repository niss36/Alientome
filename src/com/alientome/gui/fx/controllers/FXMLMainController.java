package com.alientome.gui.fx.controllers;

import com.alientome.core.SharedInstances;
import com.alientome.core.SharedNames;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.events.QuitRequestEvent;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.settings.Config;
import com.alientome.core.sound.SoundManager;
import com.alientome.editors.level.LevelEditor;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;

import static com.alientome.core.SharedNames.*;
import static com.alientome.core.events.GameEventType.*;

public class FXMLMainController extends FXMLController {

    @Override
    public void init(Scene scene) {

        Property<I18N> i18N = SharedInstances.getProperty(I18N) ;
        Property<Config> config = SharedInstances.getProperty(CONFIG) ;

        scene.getRoot().lookupAll("*").forEach(node -> {
            if (node instanceof Labeled) {
                Labeled labeled = (Labeled) node;
                if (labeled.getId().equals("version-label"))
                    i18N.getValue().applyBindTo(labeled, config.getValue().getProperty("version"));
                else
                    i18N.getValue().applyBindTo(labeled);
            }
        });

        GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);
        Property<SoundManager> soundManager = SharedInstances.getProperty(SOUND_MANAGER) ;

        dispatcher.register(GAME_START, e -> soundManager.getValue().stopPlaying("music.main"));
        dispatcher.register(GAME_EXIT, e -> soundManager.getValue().playLooping("music.main"));
        dispatcher.register(QUIT, e -> {
            manager.exit();
            LevelEditor.exit();
        });

        soundManager.getValue().playLooping("music.main");
    }

    @FXML
    private Button play, options, quit;

    @FXML
    private void onButtonAction(ActionEvent e) {

        Object s = e.getSource();

             if (s == play) manager.pushScene("PLAY");
        else if (s == options) manager.pushScene("OPTIONS");
        else if (s == quit) {
            GameEventDispatcher dispatcher = SharedInstances.get(SharedNames.DISPATCHER);
            dispatcher.submit(new QuitRequestEvent());
        }
    }
}
