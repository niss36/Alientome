package com.alientome.core.util;

import java.io.IOException;
import java.nio.file.Path;

public interface FileManager {

    void checkFiles() throws IOException;

    Path getRootDirectory();

    Path getConfig();

    Path getKeybindings();

    Path getSavesRoot();

    Path getSave(int index);

    Path getScreenshotsRoot();

    Path getScreenshot(String name);

    Path getBackupsRoot();

    Path getBackup(String prefix);
}
