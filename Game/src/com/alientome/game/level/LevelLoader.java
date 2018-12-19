package com.alientome.game.level;

import java.io.IOException;
import java.nio.file.Path;

public interface LevelLoader {

    LevelManager loadFrom(Path temp, Path actual) throws IOException;

    LevelManager loadFrom(Path file) throws IOException;

    LevelManager loadFrom(int saveIndex) throws IOException;
}
