package com.alientome.gui.fx.controllers;

import com.alientome.core.SharedInstances;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.keybindings.InputManager;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;
import com.alientome.game.events.KeybindingsResetEvent;
import com.alientome.gui.fx.DialogsUtil;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import static com.alientome.core.SharedNames.*;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.UNDEFINED;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class FXMLControlsController extends FXMLController {

    private final Map<String, Property<KeyCode>> properties = new HashMap<>();

    private Button used = null;

    @Override
    public void init(Scene scene) {

        Property<I18N> i18N = SharedInstances.getProperty(I18N);
        InputManager manager = SharedInstances.get(INPUT_MANAGER);

        scene.getRoot().lookupAll("*").forEach(node -> {
            if (node instanceof Labeled)
                i18N.getValue().applyBindTo((Labeled) node);
        });

        WrappedXML keybindingsDisplayXML;

        try {
            keybindingsDisplayXML = Util.parseXMLNew("keybindingsDisplay.xml");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        int rowIndex = 0;
        for (WrappedXML categoryXML : keybindingsDisplayXML.nodesWrapped("categories/category")) {

            Label categoryLabel = new Label("controls.category." + categoryXML.getAttr("id"));
            i18N.getValue().applyBindTo(categoryLabel);
            GridPane.setColumnSpan(categoryLabel, 2);
            GridPane.setHalignment(categoryLabel, HPos.CENTER);
            categoryLabel.getStyleClass().add("control-category");

            grid.addRow(rowIndex++, categoryLabel);
            grid.getRowConstraints().add(new RowConstraints(USE_COMPUTED_SIZE, 30, USE_COMPUTED_SIZE));

            for (WrappedXML bindingXML : categoryXML.nodesWrapped("binding")) {

                String uid = bindingXML.getAttr("uid");
                String[] split = uid.split(":");
                String context = split[0];
                String id = split[1];

                Label bindingLabel = new Label("controls." + id);
                i18N.getValue().applyBindTo(bindingLabel);
                bindingLabel.getStyleClass().add("control-label");

                Property<KeyCode> property = manager.bindingProperty(context, id);
                properties.put(uid, property);

                Button bindingButton = new Button(toString(property.getValue()));
                bindingButton.setPrefWidth(120);
                bindingButton.setPrefHeight(48);
                GridPane.setHalignment(bindingButton, HPos.RIGHT);
                bindingButton.getStyleClass().add("control-button");
                bindingButton.setId(uid);
                bindingButton.setOnAction(this::onKeyPickerAction);

                property.addListener((observable, oldValue, newValue) -> bindingButton.setText(toString(newValue)));

                grid.addRow(rowIndex++, bindingLabel, bindingButton);
                grid.getRowConstraints().add(new RowConstraints(USE_COMPUTED_SIZE, 60, USE_COMPUTED_SIZE));
            }
        }

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (used != null) {
                if (e.getCode() == ESCAPE) {
                    setKeyTo(UNDEFINED);
                    e.consume();
                } else  {
                    setKeyTo(e.getCode());
                    e.consume();
                }
            }
        });
    }

    private void setKeyTo(KeyCode code) {
        Property<KeyCode> property = properties.get(used.getId());
        if (property.getValue() == code)
            used.setText(toString(code));
        else if (isBound(used.getId(), code))
            return;
        property.setValue(code);
        used = null;
    }

    private boolean isBound(String uid, KeyCode code) {
        InputManager manager = SharedInstances.get(INPUT_MANAGER);
        return manager.isBound(uid.substring(0, uid.indexOf(':')), code);
    }

    private String toString(KeyCode code) {
        if (code == UNDEFINED)
            return "NONE";
        else {
            String str = code.name();
            if (str.startsWith("DIGIT"))
                str = str.substring(5);
            return str;
        }
    }

    @FXML
    private GridPane grid;

    @FXML
    private Button done, resetControls;

    @FXML
    private void onButtonAction(ActionEvent e) {

        Node s = (Node) e.getSource();

        if (used != null) {
            used.setText(toString(properties.get(used.getId()).getValue()));
            used = null;
        }

        GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);

             if (s == done) manager.popScene();
        else if (s == resetControls) {
            if (DialogsUtil.showConfirmDialog(null, null, "menu.controls.reset.prompt"))
                dispatcher.submit(new KeybindingsResetEvent());
        }
    }

    private void onKeyPickerAction(ActionEvent e) {

        Button s = (Button) e.getSource();

        if (used != null)
            used.setText(toString(properties.get(used.getId()).getValue()));

        s.setText("<...>");
        used = s;
    }
}
