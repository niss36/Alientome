package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.util.FileManager;
import com.alientome.game.level.SaveManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultSaveManager extends SaveManager {

    protected final Context context;

    public DefaultSaveManager(Context context) {
        this.context = context;
    }

    @Override
    public int read(int saveIndex, boolean createFile) throws IOException {

        Path saveFile = context.getFileManager().getSave(saveIndex);

        if (Files.notExists(saveFile))
            if (createFile)
                getStatus(saveIndex).set(1);
            else
                return EMPTY_SAVE_ID;

        return read(saveFile);
    }

    @Override
    public void save(int saveIndex, int levelID) throws IOException {

        FileManager manager = context.getFileManager();

        try (DataOutputStream stream = new DataOutputStream(Files.newOutputStream(manager.getSave(saveIndex)))) {
            stream.writeInt(levelID);
        }
    }

    @Override
    public void delete(int saveIndex) throws IOException {

        Files.delete(context.getFileManager().getSave(saveIndex));
        getStatus(saveIndex).set(EMPTY_SAVE_ID);
    }

    protected int read(Path saveFile) throws IOException {

        try (DataInputStream stream = new DataInputStream(Files.newInputStream(saveFile))) {
            return stream.readInt();
        }
    }
}
