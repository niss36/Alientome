package com.alientome.game.level;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class SaveManager {

    public static final int EMPTY_SAVE_ID = -1;

    protected final IntegerProperty[] savesStatus = new IntegerProperty[3];

    {
        for (int i = 0; i < savesStatus.length; i++) {

            int index = i;

            savesStatus[i] = new SimpleIntegerProperty();
            savesStatus[i].addListener((observable, oldValue, newValue) -> {
                try {
                    if (newValue.intValue() != EMPTY_SAVE_ID && oldValue.intValue() != 0)
                        save(index, newValue.intValue());
                } catch (IOException e) {
                    throw new UncheckedIOException(e); //TODO find a way to make that checked
                }
            });
        }
    }

    public void actualize() throws IOException {

        for (int i = 0; i < savesStatus.length; i++)
            savesStatus[i].set(read(i, false));
    }

    public IntegerProperty getStatus(int saveIndex) {
        return savesStatus[saveIndex];
    }

    protected abstract int read(int saveIndex, boolean createFile) throws IOException;

    protected abstract void save(int saveIndex, int levelID) throws IOException;

    public abstract void delete(int saveIndex) throws IOException;
}
