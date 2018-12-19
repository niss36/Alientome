package com.alientome.impl.level;

import com.alientome.game.GameContext;
import com.alientome.impl.level.source.CompressedCompoundSource;
import com.alientome.impl.level.source.LevelSource;
import com.alientome.impl.level.source.UncompressedCompoundSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CompoundLevelManager extends AbstractLevelManager {

    private Path lastFile;

    public CompoundLevelManager(GameContext context, Path file) throws IOException {
        this(context, file, file);
    }

    public CompoundLevelManager(GameContext context, Path tempFile, Path location) throws IOException {

        super(context);

        try {
            loadLevel(tempFile);
            lastFile = location;
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
            loadLevel(resolved);

            reset();

            lastFile = resolved;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void loadLevel(Path file) throws IOException {

        LevelSource source;

        if (Files.isDirectory(file))
            source = new UncompressedCompoundSource(context, parser, file);
        else
            source = new CompressedCompoundSource(context, parser, file);

        level = new DefaultLevel(this, source);
    }
}
