package com.alientome.impl.level;

import com.alientome.impl.level.source.CompressedCompoundSource;
import com.alientome.impl.level.source.LevelSource;
import com.alientome.impl.level.source.UncompressedCompoundSource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class CompoundLevelManager extends AbstractLevelManager {

    private Path lastFile;

    public CompoundLevelManager(File file) throws IOException {
        this(file, file);
    }

    public CompoundLevelManager(File tempFile, File location) throws IOException {

        try {
            loadLevel(tempFile);
            lastFile = location.toPath();
        } catch (IOException | RuntimeException e) {
            dispose();
            throw e;
        }
    }

    @Override
    public void nextLevel() {
        System.out.println("Next level");
    }

    @Override
    public void loadLevel(String path) {

        try {
            Path resolved = lastFile.resolve(path);
            loadLevel(resolved.toFile());

            reset();

            lastFile = resolved;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void loadLevel(File file) throws IOException {

        LevelSource source;

        if (file.isDirectory())
            source = new UncompressedCompoundSource(parser, file);
        else
            source = new CompressedCompoundSource(parser, file);

        level = new DefaultLevel(this, source);
    }
}
