package com.alientome.editors.level.gui.fx;

import com.alientome.editors.level.background.Layer;
import javafx.scene.control.ListCell;

public class LayerListCell extends ListCell<Layer> {

    @Override
    protected void updateItem(Layer item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null)
            setText(null);
        else
            setText(item.name);
    }
}
