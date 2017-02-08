package com;

import com.gui.GameFrame;
import com.keybindings.InputManager;
import com.settings.Config;
import com.util.FileManager;
import com.util.I18N;
import com.util.profile.ExecutionTimeProfiler;
import com.util.visual.SpritesLoader;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Thread.currentThread().setName("Thread-Main");

        boolean debugUI = args.length > 0 && args[0].equals("-debugUI");

        FileManager.getInstance().checkFiles();

        Config.getInstance().load();

        InputManager.getInstance().load();
        InputManager.getInstance().setActiveContext("menu.main");

        I18N.init();

        JFrame frame = new GameFrame(debugUI);

        frame.setVisible(true);

        SpritesLoader.load();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Config.getInstance().save();
            InputManager.getInstance().save();
            ExecutionTimeProfiler.theProfiler.dumpProfileData();
        }, "Thread-Shutdown"));
    }
}
