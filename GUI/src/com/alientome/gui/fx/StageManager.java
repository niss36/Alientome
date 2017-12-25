package com.alientome.gui.fx;

import com.alientome.gui.fx.controllers.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StageManager {

    protected final Stage stage;
    private final Map<String, Scene> scenes;
    private final Stack<Scene> scenesStack;

    public StageManager(Stage stage) {
        this.stage = stage;
        scenes = new HashMap<>();
        scenesStack = new Stack<>();
    }

    public FXMLController loadAndGetController(URL location, String id) throws IOException {

        FXMLLoader loader = new FXMLLoader(location);

        Parent root = loader.load();
        Scene scene = new Scene(root);

        scenes.put(id, scene);

        FXMLController controller = loader.getController();

        controller.setManager(this);
        controller.init(scene);

        return controller;
    }

    public void switchToScene(String id) {

        Scene scene = scenes.get(id);

        safeSetScene(scene);

        scenesStack.clear();
        scenesStack.push(scene);
    }

    public void pushScene(String id) {

        Scene scene = scenes.get(id);

        safeSetScene(scene);

        scenesStack.push(scene);
    }

    public void popScene() {

        scenesStack.pop();

        safeSetScene(scenesStack.peek());
    }

    public void exit() {

        if (Platform.isFxApplicationThread())
            exitInternal();
        else
            Platform.runLater(this::exitInternal);
    }

    protected void exitInternal() {
        stage.close();
    }

    public String getCurrentScene() {
        return scenesStack.empty() ? null : idFor(scenesStack.peek());
    }

    public Stage getStage() {
        return stage;
    }

    private String idFor(Scene scene) {
        for (Map.Entry<String, Scene> entry : scenes.entrySet())
            if (entry.getValue() == scene)
                return entry.getKey();
        return null;
    }

    private void safeSetScene(Scene scene) {

        if (Platform.isFxApplicationThread())
            stage.setScene(scene);
        else
            Platform.runLater(() -> stage.setScene(scene));
    }
}
