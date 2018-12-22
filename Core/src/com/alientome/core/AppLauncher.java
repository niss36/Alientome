package com.alientome.core;

import com.alientome.core.util.Arguments;
import javafx.application.Application;
import javafx.stage.Stage;

public abstract class AppLauncher {

    protected final Arguments arguments;

    public AppLauncher(String[] args) {
        arguments = new Arguments(args);
    }

    public abstract void preInit() throws Exception;

    public abstract void init() throws Exception;

    public abstract void postInit() throws Exception;

    public abstract void start(Application app, Stage stage) throws Exception;

    public abstract void shutdown() throws Exception;
}
