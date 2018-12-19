package com.alientome.editors.level;

import com.alientome.core.util.WriteOnceObjectProperty;
import com.alientome.game.GameContext;
import javafx.beans.property.Property;

public class LevelEditorContext extends GameContext {

    private final Property<LevelEditor> levelEditor;

    {
        levelEditor = new WriteOnceObjectProperty<>(null, "Level Editor");
    }

    public LevelEditor getLevelEditor() {
        return require(levelEditor);
    }

    public void setLevelEditor(LevelEditor levelEditor) {
        this.levelEditor.setValue(levelEditor);
    }
}
