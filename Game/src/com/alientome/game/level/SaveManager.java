package com.alientome.game.level;

import java.io.IOException;

public abstract class SaveManager {

    protected static final int EMPTY_SAVE = -1;
    protected static final int UNINITIALISED_SAVE = 0;
    protected static final int FIRST_LEVEL_ID = 1;

    protected final SaveStatus[] saveStatuses = new SaveStatus[3];

    public SaveManager() {
        for (int i = 0; i < saveStatuses.length; i++) {

            saveStatuses[i] = new SaveStatus(this, i);
        }
    }

    public void actualise() throws IOException {

        for (SaveStatus saveStatus : saveStatuses)
            saveStatus.actualise();
    }

    public SaveStatus getStatus(int saveIndex) {
        return saveStatuses[saveIndex];
    }

    protected abstract int read(int saveIndex) throws IOException;

    protected abstract void save(int saveIndex, int levelID) throws IOException;

    protected abstract void delete(int saveIndex) throws IOException;
}
