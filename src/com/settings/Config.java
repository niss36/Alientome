package com.settings;

import com.events.GameEventDispatcher;
import com.events.GameEventType;
import com.util.FileManager;
import com.util.Logger;
import com.util.Util;
import com.util.listeners.ValueListener;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    private static final Config ourInstance = new Config();
    private static final Logger log = Logger.get();
    private final List<Setting> editableSettings = new ArrayList<>();
    private final Map<String, Setting> settings = new LinkedHashMap<>();
    private final File userConfig;
    private final Map<String, Object> values = new HashMap<>();

    private Config() {
        userConfig = FileManager.getInstance().getConfig();

        GameEventDispatcher.getInstance().register(GameEventType.CONFIG_RESET, e -> reset());
    }

    public static Config getInstance() {
        return ourInstance;
    }

    public void load() {

        log.i("Loading config");

        try {
            init();
        } catch (Exception e) {
            log.f("Error while loading settings :");
            e.printStackTrace();
        }

        read(defaultConfig());

        read(userConfig);

        log.i("Loaded config");
    }

    public void save() {

        log.i("Saving config");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userConfig))) {

            for (Map.Entry<String, Object> entry : values.entrySet()) {

                String line = entry.getKey() + "=" + entry.getValue();

                writer.write(line);
                writer.newLine();
            }
            log.i("Saved config");
        } catch (IOException e) {
            log.e("Could not save config :");
            e.printStackTrace();
        }
    }

    private void read(InputStream stream) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            List<String> lines = reader.lines().collect(Collectors.toList());
            read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(File file) {

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            read(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(List<String> lines) {

        for (String line : lines) {

            String[] keyValPair = line.split("=");
            String key = keyValPair[0];
            String val = keyValPair[1];

            Setting setting = settings.get(key);
            if (setting != null) {
                Object value = ValueFilter.get(setting.valueType, val);
                set(key, value);
            }
        }
    }

    private void init() throws Exception {

        Element root = Util.parseXML("config");

        NodeList settingsList = root.getElementsByTagName("setting");
        for (int i = 0; i < settingsList.getLength(); i++) {

            Element settingNode = (Element) settingsList.item(i);
            Setting setting = Setting.parseXML(settingNode, editableSettings);
            settings.put(setting.id, setting);
        }
    }

    private InputStream defaultConfig() {
        return ClassLoader.getSystemResourceAsStream("defaultConfig.txt");
    }

    public void createConfigFile(File targetFile) {

        try (InputStream in = defaultConfig()) {
            Files.copy(in, targetFile.toPath());
            log.i("Config file was created");
        } catch (IOException e) {
            e.printStackTrace();
            log.w("Config file could not be created");
        }
    }

    private void reset() {
        read(defaultConfig());
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) get(key);
    }

    public int getInt(String key) {
        return (int) get(key);
    }

    public void set(String key, Object value) {
        values.put(key, value);
        settings.get(key).notifyListeners(value);
    }

    public void setString(String key, String value) {
        set(key, value);
    }

    public void setBoolean(String key, boolean value) {
        set(key, value);
    }

    public void setInt(String key, int value) {
        set(key, value);
    }

    public List<Setting> getEditableSettings() {
        return editableSettings;
    }

    public void addSettingListener(String settingID, ValueListener listener) {
        settings.get(settingID).addValueListener(listener);
    }
}
