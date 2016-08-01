package com.gui;

import com.game.Game;
import com.game.level.Level;
import com.game.level.LevelSaveManager;
import com.util.listeners.SaveListener;

import javax.swing.*;
import java.awt.*;

import static com.util.Util.log;

class MenuSavePanel extends JPanel implements SaveListener {

    private final int saveIndex;
    private final MenuButton buttonSave;
    private final MenuButton buttonDelete;
    private int prevLevelID = -1;

    MenuSavePanel(Dimension buttonSaveDimension, Dimension buttonDeleteDimension, Font font, int saveIndex) {

        this.saveIndex = saveIndex;

        buttonSave = new MenuButton("", buttonSaveDimension, font);
        buttonSave.addActionListener(e -> {
            buttonSave.setEnabled(false);
            new Thread(() -> {
                Level.getInstance().init(saveIndex);
                Frame.getInstance().showCard(Frame.GAME);
                Game.getInstance().init(Frame.getInstance().panelGame);
                Game.getInstance().start();
                buttonSave.setEnabled(true);
            }, "Thread-LevelLoad").start();
        });

        buttonDelete = new MenuButton("X", buttonDeleteDimension, font);
        buttonDelete.addActionListener(e -> {
            int action = new ConfirmDialog(Frame.getInstance(), "Delete this save ?").showDialog();
            if (action == ConfirmDialog.ACCEPT)
                if (!LevelSaveManager.getInstance().deleteSaveFile(saveIndex))
                    log("An error occurred while deleting this save", 2);
        });

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        LevelSaveManager.getInstance().addSaveListener(this);

        actualize();
    }

    private void actualize() {

        int levelID = LevelSaveManager.getInstance().getIDFromIndex(saveIndex);

        if (levelID != prevLevelID) {

            removeAll();

            if (levelID != 0) {

                buttonSave.setText("Level " + levelID);

                add(buttonSave);
                add(Box.createRigidArea(new Dimension(40, 0)));
                add(buttonDelete);
            } else {

                buttonSave.setText("New Game");

                add(buttonSave);
            }

            revalidate();

            prevLevelID = levelID;
        }
    }

    @Override
    public void saveChanged(int index) {
        if (index == saveIndex) actualize();
    }
}
