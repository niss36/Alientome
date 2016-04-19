package com.util;

public class FileNames {

    private static final String userHome = System.getProperty("user.home");
    private static final String fileSeparator = System.getProperty("file.separator");

    public static final String directory = userHome + fileSeparator + "Alientome";
    public static final String config = directory + fileSeparator + "Config.properties";
}
