package com.game.level;

import com.util.Util;
import com.util.visual.SpritesLoader;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;

public class DefaultLevelSource extends LevelSource {

    private final int levelID;
    private final int saveIndex;
    private LevelMap map;

    public DefaultLevelSource(int saveIndex) {

        levelID = LevelSaveManager.getInstance().init(saveIndex);

        this.saveIndex = saveIndex;
    }

    @Override
    Element getDataRoot(Level level) throws Exception {

        return Util.parseXML("Level/" + levelID + "/data");
    }

    @Override
    public BufferedImage getBackground() {
        return SpritesLoader.getSprite("Level/" + levelID + "/background");
    }

    @Override
    public LevelMap getMap() {
        if (map == null)
            map = new LevelMap(SpritesLoader.getSprite("Level/" + levelID + "/tilemap"));
        return map;
    }

    @Override
    public void save() {
        LevelSaveManager.getInstance().save(levelID, saveIndex);
    }
}
