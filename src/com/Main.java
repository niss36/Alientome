package com;

import com.game.Block;
import com.game.Level;
import com.gui.Frame;
import com.util.Config;

public class Main {

    public static void main(String[] args) {

        Block.init(64);

        Level.getInstance().init(1);

        Frame.getInstance().setVisible(true);

        Frame.getInstance().panelGame.init();

        Config.getInstance().load();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> Config.getInstance().save()));
    }
}
