package com.alientome.impl;

import com.alientome.core.SharedInstances;
import com.alientome.core.util.FileManager;
import com.alientome.game.level.SaveManager;

import java.io.*;

import static com.alientome.core.SharedNames.FILE_MANAGER;

public class DefaultSaveManager extends SaveManager {

    public DefaultSaveManager() {}

    @Override
    public int read(int saveIndex, boolean createFile) {

        FileManager manager = SharedInstances.get(FILE_MANAGER);
        File saveFile = manager.getSave(saveIndex);

        if (!saveFile.exists())
            if (createFile)
                getStatus(saveIndex).set(1);
            else
                return EMPTY_SAVE_ID;

        try {
            return read(saveFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void save(int saveIndex, int levelID) {

        FileManager manager = SharedInstances.get(FILE_MANAGER);

        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(manager.getSave(saveIndex)))) {

            stream.writeInt(levelID);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean delete(int saveIndex) {

        FileManager manager = SharedInstances.get(FILE_MANAGER);

        if (manager.getSave(saveIndex).delete()) {

            getStatus(saveIndex).set(-1);

            return true;
        }

        return false;
    }

    protected int read(File saveFile) throws IOException {

        try (DataInputStream stream = new DataInputStream(new FileInputStream(saveFile))) {
            return stream.readInt();
        }
    }
}
