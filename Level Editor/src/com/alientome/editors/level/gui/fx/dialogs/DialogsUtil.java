package com.alientome.editors.level.gui.fx.dialogs;

import com.alientome.editors.level.Level;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class DialogsUtil {

    public static final int SAVE = 0, DISCARD = 1, CANCEL = 2;

    public static int showUnsavedChangesDialog() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Unsaved Changes");
        alert.setHeaderText(null);
        alert.setContentText("Unsaved changes detected. Save them now ?");

        ButtonType save = new ButtonType("Save");
        ButtonType discard = new ButtonType("Discard");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(save, discard, cancel);

        Optional<ButtonType> opt = alert.showAndWait();
        if (opt.isPresent()) {
            ButtonType val = opt.get();
            if (val == save)
                return SAVE;
            if (val == discard)
                return DISCARD;
        }
        return CANCEL;
    }

    public static int[] showNewLevelDialog() {

        Dialog<int[]> dialog = new Dialog<>();

        dialog.setTitle("New Level");

        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField width = new TextField("30");
        TextField height = new TextField("15");

        grid.add(new Label("Width :"), 0, 0);
        grid.add(width, 1, 0);
        grid.add(new Label("Height :"), 0, 1);
        grid.add(height, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK)
                return new int[] {Integer.valueOf(width.getText()), Integer.valueOf(height.getText())};
            return null;
        });

        Optional<int[]> result = dialog.showAndWait();

        return result.orElse(null);
    }

    public static int[] showResizeDialog(Level level) {

        Dialog<int[]> dialog = new Dialog<>();

        dialog.setTitle("Resize Level");

        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField width = new TextField(String.valueOf(level.getWidth()));
        TextField height = new TextField(String.valueOf(level.getHeight()));

        ComboBox<String> xCombo = new ComboBox<>();
        xCombo.getItems().addAll("Left", "Right");
        xCombo.getSelectionModel().select(1);
        ComboBox<String> yCombo = new ComboBox<>();
        yCombo.getItems().addAll("Top", "Bottom");
        yCombo.getSelectionModel().select(1);

        grid.add(new Label("Width :"), 0, 0);
        grid.add(width, 1, 0);
        grid.add(new Label("Height :"), 0, 1);
        grid.add(height, 1, 1);
        grid.add(new Label("X Extend Direction :"), 0, 2);
        grid.add(xCombo, 1, 2);
        grid.add(new Label("Y Extend Direction :"), 0, 3);
        grid.add(yCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK)
                return new int[] {Integer.valueOf(width.getText()), Integer.valueOf(height.getText()),
                        xCombo.getSelectionModel().getSelectedIndex(), yCombo.getSelectionModel().getSelectedIndex()};
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    public static File showNewLayerDialog(Stage stage, File directory) {

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Open Background Image");
        chooser.setInitialDirectory(directory);
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("PNG Images", "*.png"));

        return chooser.showOpenDialog(stage);
    }
}
