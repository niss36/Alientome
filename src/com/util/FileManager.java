package com.util;

import com.keybindings.InputManager;
import com.settings.Config;
import org.jetbrains.annotations.Contract;

import java.io.File;

public class FileManager {
    private static final FileManager ourInstance = new FileManager();

    private static final Logger log = Logger.get();

    private FileManager() {
    }

    public static FileManager getInstance() {
        return ourInstance;
    }

    public void checkFiles() {

        File dir = getDirectory();

        if (!dir.exists())
            if (dir.mkdir()) log.i("Main directory was created");
            else log.f("Main directory could not be created");

        File config = getConfig();

        if (!config.exists()) Config.getInstance().createConfigFile(config);

        File keybindings = getKeybindings();

        if (!keybindings.exists()) InputManager.getInstance().createKeybindingsFile(keybindings);

        File saveDir = getSaveDirectory();

        if (!saveDir.exists())
            if (saveDir.mkdir()) log.i("Saves directory was created");
            else log.f("Saves directory could not be created");

        File screenshotDir = getScreenshotDirectory();

        if (!screenshotDir.exists())
            if (screenshotDir.mkdir()) log.i("Screenshots directory was created");
            else log.f("Screenshots directory could not be created");
    }

    @Contract(" -> !null")
    private File getDirectory() {
        return new File(FileNames.directory);
    }

    @Contract(" -> !null")
    public File getConfig() {
        return new File(FileNames.config);
    }

    @Contract("_ -> !null")
    public File getSave(int index) {
        return new File(FileNames.saveDirectory + index + ".txt");
    }

    @Contract(" -> !null")
    private File getSaveDirectory() {
        return new File(FileNames.saveDirectory);
    }

    @Contract(" -> !null")
    private File getScreenshotDirectory() {
        return new File(FileNames.screenshotDirectory);
    }

    @Contract("_ -> !null")
    public File getScreenshot(String name) {
        return new File(FileNames.screenshotDirectory + name + ".png");
    }

    @Contract(" -> !null")
    public File getKeybindings() {
        return new File(FileNames.keybindings);
    }

    private static class FileNames {

        private static final String directory = System.getProperty("user.home") + "/Alientome/";
        private static final String saveDirectory = directory + "saves/";
        private static final String config = directory + "config.txt";
        private static final String keybindings = directory + "keybindings.txt";
        private static final String screenshotDirectory = directory + "screenshots/";
    }
}
