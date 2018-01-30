package com.alientome.editors.level.gui.fx;

import com.alientome.editors.level.state.EntityState;
import javafx.scene.control.ListCell;

public class EntityStateListCell extends ListCell<EntityState> {

    @Override
    protected void updateItem(EntityState item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.name);
            setGraphic(item.sprite.view);
        }
    }
}
