package com.alientome.gui.fx.controllers;

import com.alientome.core.Context;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.settings.Config;
import com.alientome.core.util.Logger;
import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;
import com.alientome.gui.fx.DialogsUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class FXMLOptionsController extends FXMLController {

    private static final Logger log = Logger.get();
    private final Map<String, ControlProvider> controlProviders = new HashMap<>();

    {
        controlProviders.put("Toggle", (settingXML, id, i18N, config) -> {
            ToggleButton button = new ToggleButton();
            button.setPrefWidth(200);
            button.setPrefHeight(42);
            GridPane.setHalignment(button, HPos.RIGHT);

            Property<Boolean> property = config.getProperty(id);
            button.selectedProperty().bindBidirectional(property);
            button.textProperty().bind(Bindings.createStringBinding(() -> property.getValue() ? "ON" : "OFF", property));
            return button;
        });

        controlProviders.put("VolumeControl", (settingXML, id, i18N, config) -> {
            Label label = new Label();
            label.setPrefWidth(40);

            Slider slider = new Slider();
            slider.setPrefHeight(42);
            HBox.setHgrow(slider, Priority.ALWAYS);

            HBox box = new HBox(10, label, slider);
            box.getStyleClass().add("labeled-slider");
            box.setPrefWidth(200);
            box.setPrefHeight(42);
            box.setMaxWidth(USE_PREF_SIZE);
            box.setMaxHeight(USE_PREF_SIZE);
            GridPane.setHalignment(box, HPos.RIGHT);

            IntegerProperty property = config.getIntegerProperty(id);
            label.textProperty().bind(Bindings.createStringBinding(() -> property.get() + "%", property));
            slider.valueProperty().bindBidirectional(property);

            return box;
        });

        controlProviders.put("ComboBox:String", (settingXML, id, i18N, config) -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setPrefWidth(200);
            comboBox.setPrefHeight(42);
            GridPane.setHalignment(comboBox, HPos.RIGHT);
            for (WrappedXML entryXML : settingXML.nodesWrapped("entry"))
                comboBox.getItems().add(entryXML.getAttr("value"));

            comboBox.valueProperty().bindBidirectional(config.getProperty(id));
            return comboBox;
        });

        controlProviders.put("FPSCombo", (settingXML, id, i18N, config) -> {
            ComboBox<Integer> comboBox = new ComboBox<>();
            comboBox.setPrefWidth(200);
            comboBox.setPrefHeight(42);
            GridPane.setHalignment(comboBox, HPos.RIGHT);
            for (WrappedXML entryXML : settingXML.nodesWrapped("entry"))
                comboBox.getItems().add(entryXML.getAttrInt("value"));
            comboBox.setButtonCell(new FPSListCell(context));
            comboBox.setCellFactory(param -> new FPSListCell(context));
            comboBox.valueProperty().bindBidirectional(config.getProperty(id));

            i18N.localeProperty().addListener(observable -> {
                comboBox.setButtonCell(new FPSListCell(context));
                comboBox.setCellFactory(null); //Force actualization
                comboBox.setCellFactory(param -> new FPSListCell(context));
            });

            return comboBox;
        });
    }

    @Override
    public void init(Scene scene) throws IOException {

        Config config = context.getConfig();
        I18N i18N = context.getI18N();

        scene.getRoot().lookupAll("*").forEach(node -> {
            if (node instanceof Labeled)
                context.getI18N().applyBindTo((Labeled) node);
        });

        WrappedXML configDisplayXML = Util.parseXMLNew("configDisplay.xml");

        grid.getChildren().clear();

        int rowIndex = 0;
        for (WrappedXML categoryXML : configDisplayXML.nodesWrapped("categories/category")) {

            Label categoryLabel = new Label("options.category." + categoryXML.getAttr("id"));
            i18N.applyBindTo(categoryLabel);
            GridPane.setColumnSpan(categoryLabel, 2);
            GridPane.setHalignment(categoryLabel, HPos.CENTER);
            categoryLabel.getStyleClass().add("option-category");

            grid.addRow(rowIndex++, categoryLabel);
            grid.getRowConstraints().add(new RowConstraints(USE_COMPUTED_SIZE, 30, USE_COMPUTED_SIZE));

            for (WrappedXML settingXML : categoryXML.nodesWrapped("setting")) {

                String id = settingXML.getAttr("id");
                String type = settingXML.getAttr("type");

                Label settingLabel = new Label("options." + id);
                i18N.applyBindTo(settingLabel);
                settingLabel.getStyleClass().add("option-label");

                ControlProvider provider = controlProviders.get(type);
                if (provider == null) {
                    log.w("Unable to find a control provider for type '" + type + "'. Ignoring...");
                    grid.addRow(rowIndex++, settingLabel);
                } else {
                    Node settingControl = provider.create(settingXML, id, i18N, config);
                    grid.addRow(rowIndex++, settingLabel, settingControl);
                }
                grid.getRowConstraints().add(new RowConstraints(USE_COMPUTED_SIZE, 60, USE_COMPUTED_SIZE));
            }
        }
    }

    private static class FPSListCell extends ListCell<Integer> {

        private final Context context;

        FPSListCell(Context context) {
            this.context = context;
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            I18N i18N = context.getI18N();

            if (empty || item == null)
                setText(null);
            else
                setText(item == 0 ? i18N.get("options.maxFPS.unlimited") : i18N.get("options.maxFPS.format", (int) item));
        }
    }

    private interface ControlProvider {

        Node create(WrappedXML settingXML, String id, I18N i18N, Config config);
    }

    @FXML
    private GridPane grid;

    @FXML
    private Button done, resetOptions, controls;

    @FXML
    private void onButtonAction(ActionEvent e) {

        Object s = e.getSource();

             if (s == done) done();
        else if (s == resetOptions) {
            if (DialogsUtil.showConfirmDialog(context, null, null, "menu.options.reset.prompt")) {
                try {
                    context.getConfig().reset();
                } catch (IOException ioe) {
                    log.e("Couldn't reset config");
                    ioe.printStackTrace();
                    DialogsUtil.showErrorDialog(context, ioe);
                }
            }
        }
        else if (s == controls) manager.pushScene("CONTROLS");
    }

    private void done() {
        manager.popScene();
        if (context.getConfig().needsSave())
            try {
                context.getConfig().save();
            } catch (IOException e) {
                log.e("Could not save config:");
                e.printStackTrace();
                DialogsUtil.showErrorDialog(context, e);
            }
    }
}
