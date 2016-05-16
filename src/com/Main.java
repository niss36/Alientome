package com;

import com.game.Level;
import com.gui.Frame;
import com.util.Config;

public class Main {

    public static void main(String[] args) {

        Level.getInstance().init(1);

        Config.getInstance().load();

        Frame.getInstance().panelGame.init();

        Frame.getInstance().setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> Config.getInstance().save(), "main"));
    }
}
