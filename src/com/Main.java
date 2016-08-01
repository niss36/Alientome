package com;

import com.gui.Frame;
import com.util.Config;
import com.util.FileManager;
import com.util.visual.SpritesLoader;

public class Main {

    public static void main(String[] args) {

        FileManager.getInstance().checkFiles();

        Config.getInstance().load();

        SpritesLoader.load();

        Frame.getInstance().setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> Config.getInstance().save(), "main"));
    }
}
