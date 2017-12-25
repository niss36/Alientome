package com.alientome.gui.fx.controllers;

import com.alientome.core.SharedInstances;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.util.Logger;
import com.alientome.editors.level.LevelEditor;
import com.alientome.game.events.GameStartEvent;
import com.alientome.game.level.LevelLoader;
import com.alientome.game.level.SaveManager;
import com.alientome.gui.fx.DialogsUtil;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

import static com.alientome.core.SharedNames.*;

public class FXMLPlayController extends FXMLController {

    private static final Logger log = Logger.get();

    private final Button[] saves = new Button[3];
    private final Button[] deletes = new Button[3];

    @Override
    public void init(Scene scene) {

        I18N i18N = SharedInstances.get(I18N);
        SaveManager manager = SharedInstances.get(SAVE_MANAGER);

        i18N.applyBindTo((Label) scene.getRoot().lookup(".title"));
        i18N.applyBindTo(back);
        i18N.applyBindTo(editor);

        {
            int i = 0;
            for (Node node : scene.getRoot().lookupAll(".save-button")) {
                saves[i++] = (Button) node;
            }
        }

        {
            int i = 0;
            for (Node node : scene.getRoot().lookupAll(".save-delete-button")) {
                deletes[i++] = (Button) node;
            }
        }

        for (int i = 0; i < 3; i++) {

            IntegerProperty saveStatus = manager.getStatus(i);

            saves[i].textProperty().bind(i18N.createStringBinding(() -> {
                if (saveStatus.get() > 0)
                    return i18N.get("menu.saves.level", saveStatus.get());
                else
                    return i18N.get("menu.saves.new");
            }, saveStatus));

            deletes[i].managedProperty().bind(deletes[i].visibleProperty());
            deletes[i].visibleProperty().bind(saveStatus.greaterThan(0));
        }

        scene.windowProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                manager.actualize();
        });
    }

    @FXML
    private Button back, editor;

    @FXML
    private void onButtonAction(ActionEvent e) {

        Object s = e.getSource();

        for (int i = 0; i < 3; i++) {
            if (s == saves[i]) {
                playSave(i);
                return;
            } else if (s == deletes[i]) {
                deleteSave(i);
                return;
            }
        }

        if (s == back)
            manager.popScene();
        else if (s == editor)
            openEditor();
    }

    private void playSave(int index) {

        SaveManager manager = SharedInstances.get(SAVE_MANAGER);

        IntegerProperty saveStatus = manager.getStatus(index);

        if (saveStatus.get() == -1)
            saveStatus.set(1);
        else {

            try {
                LevelLoader loader = SharedInstances.get(LOADER);
                GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);

                dispatcher.submit(new GameStartEvent(loader.loadFrom(index)));

                this.manager.switchToScene("GAME");
            } catch (IOException e) {
                log.e("Exception while loading level " + saveStatus.get() + " :");
                e.printStackTrace();
                DialogsUtil.showErrorDialog(e);
            }
        }
    }

    private void deleteSave(int index) {

        SaveManager manager = SharedInstances.get(SAVE_MANAGER);

        boolean delete = DialogsUtil.showConfirmDialog(null, null, "menu.saves.deletePrompt");

        if (delete && !manager.delete(index))
            log.w("Save " + index + " could not be deleted");
    }

    private void openEditor() {

        try {
            LevelEditor.start();
        } catch (Exception e) {
            log.e("Exception while opening editor :");
            e.printStackTrace();
            DialogsUtil.showErrorDialog(e);
        }
    }
}
