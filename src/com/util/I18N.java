package com.util;

import com.settings.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    private static final Logger log = Logger.get();
    private static final List<Runnable> langChangedListeners = new ArrayList<>();
    private static Locale currentLocale;
    private static ResourceBundle messagesBundle;

    public static void init() {

        String language = Config.getInstance().getString("language");
        init(language);

        Config.getInstance().addSettingListener("language", newValue -> {
            init((String) newValue);
            notifyListeners();
        });
    }

    private static void init(String language) {

        currentLocale = new Locale(language);

        messagesBundle = ResourceBundle.getBundle("Lang/lang", currentLocale);
    }

    public static String getString(String unlocalizedName) {
        if (!messagesBundle.containsKey(unlocalizedName)) {
            log.w("Non-existent unlocalized key used : " + unlocalizedName);
            return unlocalizedName;
        }
        return messagesBundle.getString(unlocalizedName);
    }

    public static String getStringFormatted(String unlocalizedName, Object... args) {
        if (!messagesBundle.containsKey(unlocalizedName)) {
            log.w("Non-existent unlocalized key used : " + unlocalizedName);
            return unlocalizedName;
        }
        return String.format(currentLocale, messagesBundle.getString(unlocalizedName), args);
    }

    public static void addLangChangedListener(Runnable listener) {
        langChangedListeners.add(listener);
    }

    private static void notifyListeners() {
        langChangedListeners.forEach(Runnable::run);
    }
}
