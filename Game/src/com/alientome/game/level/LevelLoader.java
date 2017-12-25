package com.alientome.game.level;

import java.io.File;
import java.io.IOException;

public interface LevelLoader {

    LevelManager loadFrom(File temp, File actual) throws IOException;

    LevelManager loadFrom(File file) throws IOException;

    LevelManager loadFrom(int saveIndex) throws IOException;
}
