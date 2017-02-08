package com.game.level;

import com.util.Util;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class FileLevelSource extends LevelSource {

    private final File levelDir;

    public FileLevelSource(File levelDir) {

        this.levelDir = levelDir;
    }

    @Override
    Element getDataRoot(Level level) throws Exception {
        return Util.parseXML(new File(levelDir, "data.xml"));
    }

    @Override
    BufferedImage getBackground() {
        try {
            return ImageIO.read(new File(levelDir, "background.png"));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    LevelMap getMap() {
        try {
            return new LevelMap(ImageIO.read(new File(levelDir, "tilemap.png")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    void save() {
    }
}
