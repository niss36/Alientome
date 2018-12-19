package com.alientome.gui.fx.controllers;

import com.alientome.core.Context;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.util.Logger;
import com.alientome.editors.level.LevelEditor;
import com.alientome.game.GameContext;
import com.alientome.game.events.GameStartEvent;
import com.alientome.gui.fx.DialogsUtil;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class FXMLPlayController extends FXMLController {

    private static final Logger log = Logger.get();

    private final Button[] saves = new Button[3];
    private final Button[] deletes = new Button[3];

    private GameContext context;

    @Override
    public void setContext(Context context) {
        super.setContext(context);

        this.context = (GameContext) context;
    }

    @Override
    public void init(Scene scene) throws IOException {

        I18N i18N = context.getI18N();

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

            IntegerProperty saveStatus = context.getSaveManager().getStatus(i);

            saves[i].textProperty().bind(i18N.createStringBinding(() -> {
                if (saveStatus.get() > 0)
                    return context.getI18N().get("menu.saves.level", saveStatus.get());
                else
                    return context.getI18N().get("menu.saves.new");
            }, saveStatus));

            deletes[i].managedProperty().bind(deletes[i].visibleProperty());
            deletes[i].visibleProperty().bind(saveStatus.greaterThan(0));
        }

        scene.windowProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    context.getSaveManager().actualize();
                } catch (IOException e) {
                    log.e("Couldn't actualise saves:");
                    e.printStackTrace();
                    DialogsUtil.showErrorDialog(context, e);
                }
            }
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

        IntegerProperty saveStatus = context.getSaveManager().getStatus(index);

        if (saveStatus.get() == -1)
            saveStatus.set(1);
        else {

            try {
                context.getDispatcher().submit(new GameStartEvent(context.getLoader().loadFrom(index)));

                this.manager.switchToScene("GAME");
            } catch (IOException e) {
                log.e("Exception while loading level " + saveStatus.get() + " :");
                e.printStackTrace();
                DialogsUtil.showErrorDialog(context, e);
            }
        }
    }

    private void deleteSave(int index) {

        boolean delete = DialogsUtil.showConfirmDialog(context, null, null, "menu.saves.deletePrompt");

        if (delete) {
            try {
                context.getSaveManager().delete(index);
            } catch (IOException e) {
                log.e("Save " + index + " could not be deleted");
                e.printStackTrace();
                DialogsUtil.showErrorDialog(context, e);
            }
        }
    }

    private void openEditor() {

        try {
            LevelEditor.start(context);
        } catch (Exception e) {
            log.e("Exception while opening editor :");
            e.printStackTrace();
            DialogsUtil.showErrorDialog(context, e);
        }
    }
}
