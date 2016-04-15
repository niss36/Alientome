package com;

import com.game.Block;
import com.game.level.Level;
import com.gui.Frame;
import com.util.Config;

public class Main {

    public static void main(String[] args) {

        Level.getInstance().init("Level1.txt");

        Block.init(64);

        Frame.getInstance().setVisible(true);

        Frame.getInstance().game.init();

        Config.getInstance().load();
    }
}
