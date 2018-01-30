package com.alientome.impl.level;

import com.alientome.core.util.FileUtils;
import com.alientome.game.GameContext;
import com.alientome.game.level.Level;
import com.alientome.impl.level.source.CompressedCompoundSource;
import javafx.beans.property.IntegerProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SaveLevelManager extends AbstractLevelManager {

    private final IntegerProperty saveStatus;

    public SaveLevelManager(GameContext context, int saveIndex) throws IOException {

        super(context);

        try {
            saveStatus = context.getSaveManager().getStatus(saveIndex);

            level = load(saveStatus.get());

            if (level == null)
                throw new IllegalArgumentException("Unknown Level ID : " + saveStatus.get());
        } catch (IOException | RuntimeException e) {
            dispose();
            throw e;
        }
    }

    @Override
    public void nextLevel() {

        int id = saveStatus.get() + 1;

        try {
            Level level = load(id);

            if (level == null)
                log.w("Unknown Level ID : " + id);
            else {
                this.level = level;

                reset();

                saveStatus.set(id);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void loadLevel(String path) {
    }

    private Level load(int levelID) throws IOException {

        URL resource = ClassLoader.getSystemResource("Level/" + levelID + ".lvl");

        if (resource == null)
            return null;

        Path temp = FileUtils.toTempFile("Level", levelID + ".lvl");

        try (InputStream stream = resource.openStream()) {
            Files.copy(stream, temp, StandardCopyOption.REPLACE_EXISTING);
        }

        return new DefaultLevel(this, new CompressedCompoundSource(context, parser, temp.toFile()));
    }
}
