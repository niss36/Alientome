package com.alientome.core;

import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.keybindings.InputManager;
import com.alientome.core.settings.Config;
import com.alientome.core.sound.SoundManager;
import com.alientome.core.util.FileManager;
import com.alientome.core.util.WriteOnceObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Property<Config> config;
    private final Property<GameEventDispatcher> dispatcher;
    private final Property<FileManager> fileManager;
    private final Property<I18N> i18N;
    private final Property<InputManager> inputManager;
    private final Property<SoundManager> soundManager;

    {
        config = new WriteOnceObjectProperty<>(this, "Config");
        dispatcher = new WriteOnceObjectProperty<>(this, "Dispatcher");
        fileManager = new WriteOnceObjectProperty<>(this, "File Manager");
        i18N = new WriteOnceObjectProperty<>(this, "I18N");
        inputManager = new WriteOnceObjectProperty<>(this, "Input Manager");
        soundManager = new WriteOnceObjectProperty<>(this, "Sound Manager");
    }

    private final Map<String, Property<?>> custom = new HashMap<>();

    // CONFIG

    public Config getConfig() {
        return require(config);
    }

    public void setConfig(Config config) {
        this.config.setValue(config);
    }

    // DISPATCHER

    public GameEventDispatcher getDispatcher() {
        return require(dispatcher);
    }

    public void setDispatcher(GameEventDispatcher dispatcher) {
        this.dispatcher.setValue(dispatcher);
    }

    // FILE MANAGER

    public FileManager getFileManager() {
        return require(fileManager);
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager.setValue(fileManager);
    }

    // I18N

    public I18N getI18N() {
        return require(i18N);
    }

    public void setI18N(I18N i18N) {
        this.i18N.setValue(i18N);
    }

    // INPUT MANAGER

    public InputManager getInputManager() {
        return require(inputManager);
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager.setValue(inputManager);
    }

    // SOUND MANAGER

    public SoundManager getSoundManager() {
        return require(soundManager);
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager.setValue(soundManager);
    }

    // CUSTOM

    public <T> T get(String id) {
        return require(property(id));
    }

    public void set(String id, Object obj) {
        property(id).setValue(obj);
    }

    @SuppressWarnings("unchecked")
    public <T> Property<T> property(String id) {
        return (Property<T>) custom.computeIfAbsent(id, s -> new SimpleObjectProperty<T>(Context.this, id));
    }

    protected <T> T require(Property<T> p) {
        T v = p.getValue();
        if (v == null)
            throw new IllegalStateException("Missing component : " + p.getName());
        return v;
    }
}
