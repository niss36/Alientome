package com.gui;

import com.keybindings.InputManager;
import com.keybindings.KeyBinding;

import javax.swing.*;
import java.awt.*;

class GameControlPanel extends JComponent {

    GameControlPanel(Dimension itemDimension, Font font, KeyBinding binding) {

        InputManager inputManager = InputManager.getInstance();

        Dimension labelDimension = new Dimension(itemDimension.width + itemDimension.width / 3, itemDimension.height);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        String unlocalizedText = "key." + binding.id;
        GameLabel label = new GameLabel(labelDimension, unlocalizedText, font, GameLabel.LEFT);

        GameKeyPicker keyPicker = new GameKeyPicker(
                binding.context.getKeybinding(binding.id),
                itemDimension, font,
                integer -> !inputManager.isKeyBound(integer, binding.context));
        keyPicker.addValueListener(newValue -> binding.context.setKeybinding(binding.id, newValue));

        binding.addValueListener(keyPicker::setCurrentKey);

        add(Box.createRigidArea(new Dimension(itemDimension.width, 0)));
        add(label);
        add(Box.createHorizontalGlue());
        add(keyPicker);
        add(Box.createRigidArea(new Dimension(itemDimension.width, 0)));
    }
}
