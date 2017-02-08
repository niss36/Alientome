package com.gui;

import com.events.GameEventDispatcher;
import com.game.level.DefaultLevelSource;
import com.game.level.Level;
import com.game.level.LevelSaveManager;
import com.util.Logger;

import javax.swing.*;
import java.awt.*;

import static com.events.GameEventType.GAME_START;
import static com.util.I18N.*;

class GameSavePanel extends JComponent {

    private static final Logger log = Logger.get();

    private final int saveIndex;
    private final GameButton saveButton;
    private final GameButton deleteButton;
    private int prevLevelID = 0;

    GameSavePanel(Dimension saveButtonDimension, Font font, int saveIndex) {

        this.saveIndex = saveIndex;

        saveButton = new GameButton(saveButtonDimension, "", font);
        saveButton.addActionListener(e -> {
            saveButton.setEnabled(false);
            new Thread(() -> {
                Level level = new Level(new DefaultLevelSource(saveIndex));
                GameEventDispatcher.getInstance().submit(level, GAME_START);
                saveButton.setEnabled(true);
            }, "Thread-LevelLoad").start();
        });

        //noinspection SuspiciousNameCombination
        Dimension buttonDeleteDimension = new Dimension(saveButtonDimension.height, saveButtonDimension.height);

        deleteButton = new GameButton(buttonDeleteDimension, "", font);
        deleteButton.addActionListener(e -> {
            int action = new ConfirmDialog("menu.saves.delete.title").showDialog();
            if (action == ConfirmDialog.ACCEPT)
                if (!LevelSaveManager.getInstance().deleteSaveFile(saveIndex))
                    log.e("An error occurred while deleting this save");
        });
        deleteButton.setText("X");

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        LevelSaveManager.getInstance().addSaveListener(index -> {
            if (index == saveIndex) actualize();
        });

        addLangChangedListener(() -> {

            int levelID = LevelSaveManager.getInstance().getIDFromIndex(saveIndex);

            if (levelID != -1)
                saveButton.setText(getStringFormatted("menu.saves.name", levelID));
            else
                saveButton.setText(getString("menu.saves.new"));
        });

        actualize();
    }

    private void actualize() {

        int levelID = LevelSaveManager.getInstance().getIDFromIndex(saveIndex);

        if (levelID != prevLevelID) {

            removeAll();

            if (levelID != -1) {

                saveButton.setText(getStringFormatted("menu.saves.name", levelID));

                add(saveButton);
                add(Box.createRigidArea(new Dimension(40, 0)));
                add(deleteButton);
            } else {

                saveButton.setText(getString("menu.saves.new"));

                add(saveButton);
            }

            revalidate();

            prevLevelID = levelID;
        }
    }
}
