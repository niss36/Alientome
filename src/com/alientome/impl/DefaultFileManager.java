package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.util.FileManager;
import com.alientome.core.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFileManager implements FileManager {

    protected static final Logger log = Logger.get();
    protected final Context context;
    protected final Path rootDirectory;

    public DefaultFileManager(Context context, Path rootDirectory) {

        this.context = context;
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void checkFiles() throws IOException {

        checkDir(getRootDirectory(), "Main directory");

        checkDir(getSavesRoot(), "Saves directory");

        checkDir(getScreenshotsRoot(), "Screenshots directory");

        checkDir(getBackupsRoot(), "Backups directory");

        checkFile(getConfig(), context.getConfig()::createDefaultFile, "Config default file");

        checkFile(getKeybindings(), context.getInputManager()::createDefaultFile, "Keybindings default file");
    }

    @Override
    public Path getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public Path getConfig() {
        return rootDirectory.resolve("config.txt");
    }

    @Override
    public Path getKeybindings() {
        return rootDirectory.resolve("keybindings.txt");
    }

    @Override
    public Path getSavesRoot() {
        return rootDirectory.resolve("saves");
    }

    @Override
    public Path getSave(int index) {
        return getSavesRoot().resolve(index + ".txt");
    }

    @Override
    public Path getScreenshotsRoot() {
        return rootDirectory.resolve("screenshots");
    }

    @Override
    public Path getScreenshot(String name) {
        return getScreenshotsRoot().resolve(name + ".png");
    }

    @Override
    public Path getBackupsRoot() {
        return rootDirectory.resolve("backups");
    }

    @Override
    public Path getBackup(String prefix) {
        return getBackupsRoot().resolve(prefix);
    }

    protected void checkDir(Path directory, String loggedName) throws IOException {

        if (Files.notExists(directory)) {
            try {
                Files.createDirectory(directory);
                log.i(loggedName + " was created");
            } catch (IOException e) {
                log.e("Couldn't create " + loggedName);
                throw e;
            }
        }
    }

    protected void checkFile(Path path, FileCreator creator, String loggedName) throws IOException {
        if (Files.notExists(path)) {
            try {
                creator.create(path);
                log.i(loggedName + " was created");
            } catch (IOException e) {
                log.e("Couldn't create " + loggedName);
                throw e;
            }
        }
    }

    protected interface FileCreator {

        void create(Path destination) throws IOException;
    }
}
