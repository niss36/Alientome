package com.alientome.gui.fx.controllers;

import com.alientome.core.Context;
import com.alientome.gui.fx.StageManager;
import javafx.scene.Scene;

public abstract class FXMLController {

    protected StageManager manager;
    protected Context context;

    public void setManager(StageManager manager) {
        this.manager = manager;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract void init(Scene scene);
}
