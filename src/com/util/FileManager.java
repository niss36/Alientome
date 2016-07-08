package com.util;

import org.jetbrains.annotations.Contract;

import java.io.File;

import static com.util.Util.log;

public final class FileManager {
    private static final FileManager ourInstance = new FileManager();

    private FileManager() {
    }

    public static FileManager getInstance() {
        return ourInstance;
    }

    public void checkFiles() {

        File dir = getDirectory();

        if (!dir.exists() && !dir.mkdir()) log("Main directory could not be created.", 3);

        File config = getConfig();

        if (!config.exists()) Config.getInstance().createConfigFile(config);

        File saveDir = getSaveDirectory();

        if (!saveDir.exists() && !saveDir.mkdir()) log("Saves directory could not be created", 3);
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

    private static final class FileNames {

        private static final String fileSeparator = System.getProperty("file.separator");
        public static final String directory = System.getProperty("user.home") + fileSeparator + "Alientome" + fileSeparator;
        public static final String saveDirectory = directory + "saves" + fileSeparator;
        public static final String config = directory + "Config.properties";
    }
}
