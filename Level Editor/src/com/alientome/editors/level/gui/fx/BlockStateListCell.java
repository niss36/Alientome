package com.alientome.editors.level.gui.fx;

import com.alientome.editors.level.state.BlockState;
import javafx.scene.control.ListCell;

public class BlockStateListCell extends ListCell<BlockState> {

    @Override
    protected void updateItem(BlockState item, boolean empty) {
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
