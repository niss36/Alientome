package com.alientome.editors.animations.util;

import java.io.*;
import java.util.Properties;
import java.util.function.Function;

public class Settings {

    private final File source;
    private final Properties properties = new Properties();

    public Settings(File source) throws IOException {

        this.source = source;

        if (!source.exists()) {

            File parent = source.getParentFile();

            if (!parent.exists() && !parent.mkdirs())
                throw new IOException("Couldn't create directory " + parent);
        } else {

            Reader reader = new FileReader(source);

            properties.load(reader);

            reader.close();
        }
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public File getFile(String key) {

        String path = properties.getProperty(key, "");

        return path.isEmpty() ? null : new File(path);

    }

    public File computeIfAbsent(String key, Function<String, File> computeFunction) {

        File file = getFile(key);

        if (file == null) {
            file = computeFunction.apply(key);
            setString(key, file.getAbsolutePath());
        }

        return file;
    }

    public boolean getBoolean(String key, boolean defaultValue) {

        String boolString = getString(key, "");

        return boolString.isEmpty() ? defaultValue : Boolean.parseBoolean(boolString);
    }

    public void setString(String key, String value) {
        properties.setProperty(key, value);
    }

    public void save() {

        try (Writer writer = new FileWriter(source)) {

            properties.store(writer, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
