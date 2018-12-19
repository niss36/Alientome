package com.alientome;

import com.alientome.core.AppLauncher;
import com.alientome.impl.DefaultAppLauncher;
import javafx.application.Application;
import javafx.stage.Stage;

public class Alientome extends Application {

    private static AppLauncher launcher;

    public static void main(String[] args) throws Exception {

        launcher = new DefaultAppLauncher(args);

        launcher.preInit();
        launcher.init();
        launcher.postInit();

        launch(Alientome.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        launcher.start(this, primaryStage);
    }
}
