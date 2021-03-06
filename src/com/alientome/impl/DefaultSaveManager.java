package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.util.FileManager;
import com.alientome.game.level.SaveManager;

import java.io.*;

public class DefaultSaveManager extends SaveManager {

    protected final Context context;

    public DefaultSaveManager(Context context) {
        this.context = context;
    }

    @Override
    public int read(int saveIndex, boolean createFile) {

        File saveFile = context.getFileManager().getSave(saveIndex);

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

        FileManager manager = context.getFileManager();

        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(manager.getSave(saveIndex)))) {

            stream.writeInt(levelID);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean delete(int saveIndex) {

        if (context.getFileManager().getSave(saveIndex).delete()) {

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
