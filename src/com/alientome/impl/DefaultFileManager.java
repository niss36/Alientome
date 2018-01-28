package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.util.FileManager;
import com.alientome.core.util.Logger;

import java.io.File;
import java.util.function.Consumer;

public class DefaultFileManager implements FileManager {

    protected static final Logger log = Logger.get();
    protected final Context context;
    protected final File rootDirectory;

    public DefaultFileManager(Context context, File rootDirectory) {

        this.context = context;
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void checkFiles() {

        checkDir(getRootDirectory(), "Main directory");

        checkDir(getSavesRoot(), "Saves directory");

        checkDir(getScreenshotsRoot(), "Screenshots directory");

        checkFile(getConfig(), context.getConfig()::createConfigFile);

        checkFile(getKeybindings(), context.getInputManager()::createKeybindingsFile);
    }

    @Override
    public File getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public File getConfig() {
        return new File(rootDirectory, "config.txt");
    }

    @Override
    public File getKeybindings() {
        return new File(rootDirectory, "keybindings.txt");
    }

    @Override
    public File getSavesRoot() {
        return new File(rootDirectory, "saves");
    }

    @Override
    public File getSave(int index) {
        return new File(rootDirectory, "saves/" + index + ".txt");
    }

    @Override
    public File getScreenshotsRoot() {
        return new File(rootDirectory, "screenshots");
    }

    @Override
    public File getScreenshot(String name) {
        return new File(rootDirectory, "screenshots/" + name + ".png");
    }

    protected void checkDir(File directory, String loggedName) {

        if (!directory.exists())
            if (directory.mkdir())
                log.i(loggedName + " was created");
            else
                log.e(loggedName + " could not be created");
    }

    protected void checkFile(File file, Consumer<File> creator) {

        if (!file.exists()) creator.accept(file);
    }
}
