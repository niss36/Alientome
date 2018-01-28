package com.alientome.core.util;

import java.io.File;

public interface FileManager {

    void checkFiles();

    File getRootDirectory();

    File getConfig();

    File getKeybindings();

    File getSavesRoot();

    File getSave(int index);

    File getScreenshotsRoot();

    File getScreenshot(String name);

    File getBackupsRoot();

    File getBackup(String prefix);
}
