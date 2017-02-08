package com.gui;

import javax.swing.*;
import java.awt.*;

abstract class GameComponent extends JComponent {

    static boolean debugUI;

    GameComponent() {

    }

    void setDimension(Dimension dimension) {

        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
    }
}
