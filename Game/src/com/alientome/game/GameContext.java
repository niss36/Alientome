package com.alientome.game;

import com.alientome.core.Context;
import com.alientome.core.util.WriteOnceObjectProperty;
import com.alientome.game.level.LevelLoader;
import com.alientome.game.level.SaveManager;
import com.alientome.game.registry.GameRegistry;
import javafx.beans.property.Property;

public class GameContext extends Context {

    private final Property<LevelLoader> loader;
    private final Property<GameRegistry> registry;
    private final Property<SaveManager> saveManager;

    {
        loader = new WriteOnceObjectProperty<>(this, "Level Loader");
        registry = new WriteOnceObjectProperty<>(this, "Registry");
        saveManager = new WriteOnceObjectProperty<>(this, "Save Manager");
    }

    // REGISTRY

    public GameRegistry getRegistry() {
        return require(registry);
    }

    public void setRegistry(GameRegistry registry) {
        this.registry.setValue(registry);
    }

    // LOADER

    public LevelLoader getLoader() {
        return require(loader);
    }

    public void setLoader(LevelLoader loader) {
        this.loader.setValue(loader);
    }

    // SAVE MANAGER

    public SaveManager getSaveManager() {
        return require(saveManager);
    }

    public void setSaveManager(SaveManager saveManager) {
        this.saveManager.setValue(saveManager);
    }
}
