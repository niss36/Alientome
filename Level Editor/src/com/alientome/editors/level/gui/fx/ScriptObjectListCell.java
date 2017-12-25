package com.alientome.editors.level.gui.fx;

import com.alientome.editors.level.state.ScriptObject;
import javafx.scene.control.ListCell;

public class ScriptObjectListCell extends ListCell<ScriptObject> {

    @Override
    protected void updateItem(ScriptObject item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null)
            setText(null);
        else
            setText(item.getBounds() + " on " + item.affected);
    }
}
