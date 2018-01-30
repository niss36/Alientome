package com.alientome.core.util;

public class VersionConflictData {

    public final String gameVersion;
    public final String userVersion;

    public VersionConflictData(String gameVersion, String userVersion) {
        this.gameVersion = gameVersion;
        this.userVersion = userVersion;
    }
}
