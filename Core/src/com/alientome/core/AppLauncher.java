package com.alientome.core;

import com.alientome.core.util.Arguments;
import javafx.application.Application;
import javafx.stage.Stage;

public abstract class AppLauncher {

    protected final Arguments arguments;

    public AppLauncher(String[] args) {
        arguments = new Arguments(args);
    }

    public abstract void preInit();

    public abstract void init();

    public abstract void postInit();

    public abstract void start(Application app, Stage stage) throws Exception;
}
