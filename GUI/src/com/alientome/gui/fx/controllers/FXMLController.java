package com.alientome.gui.fx.controllers;

import com.alientome.gui.fx.StageManager;
import javafx.scene.Scene;

public abstract class FXMLController {

    protected StageManager manager;

    public void setManager(StageManager manager) {
        this.manager = manager;
    }

    public abstract void init(Scene scene);
}
