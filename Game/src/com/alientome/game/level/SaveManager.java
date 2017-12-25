package com.alientome.game.level;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class SaveManager {

    public static final int EMPTY_SAVE_ID = -1;

    protected final IntegerProperty[] savesStatus = new IntegerProperty[3];

    {
        for (int i = 0; i < savesStatus.length; i++) {

            int index = i;

            savesStatus[i] = new SimpleIntegerProperty();
            savesStatus[i].addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() != EMPTY_SAVE_ID && oldValue.intValue() != 0)
                    save(index, newValue.intValue());
            });
        }
    }

    public void actualize() {

        for (int i = 0; i < savesStatus.length; i++)
            savesStatus[i].set(read(i, false));
    }

    public IntegerProperty getStatus(int saveIndex) {
        return savesStatus[saveIndex];
    }

    protected abstract int read(int saveIndex, boolean createFile);

    protected abstract void save(int saveIndex, int levelID);

    public abstract boolean delete(int saveIndex);
}
