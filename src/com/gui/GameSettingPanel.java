package com.gui;

import com.settings.Config;
import com.settings.Setting;
import com.settings.ValueFilter;
import com.util.GameFont;
import com.util.I18N;
import com.util.listeners.ValueListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Vector;

class GameSettingPanel extends JComponent {

    GameSettingPanel(Dimension itemDimension, Font font, Setting setting) {

        Dimension labelDimension = new Dimension(itemDimension.width + itemDimension.width / 3, itemDimension.height);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        String unlocalizedText = "setting." + setting.id;
        GameLabel label = new GameLabel(labelDimension, unlocalizedText, font, GameLabel.LEFT);

        JComponent component = getComponent(itemDimension, font, setting);

        add(Box.createRigidArea(new Dimension(itemDimension.width, 0)));
        add(label);
        add(Box.createHorizontalGlue());
        add(component);
        add(Box.createRigidArea(new Dimension(itemDimension.width, 0)));
    }

    private static JComponent getComponent(Dimension itemDimension, Font font, Setting setting) {

        if (setting.filter instanceof ValueFilter.List) {

            ValueFilter.List list = (ValueFilter.List) setting.filter;

            Object[] items = list.values.toArray();

            Vector<Object> display = new Vector<>(items.length);

            for (int i = 0; i < items.length; i++)
                display.add(setting.dictionary.getMappingFor(items[i]));

            JComboBox<Object> comboBox = new JComboBox<>(display);

            comboBox.setPreferredSize(itemDimension);
            comboBox.setMaximumSize(itemDimension);
            comboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED)
                    Config.getInstance().set(setting.id, items[comboBox.getSelectedIndex()]);
            });
            comboBox.setSelectedItem(Config.getInstance().get(setting.id));
            comboBox.setFont(font != null ? font : GameFont.get(3));
            comboBox.setLightWeightPopupEnabled(false);
            ValueListener valueListener = newValue -> {
                for (int i = 0; i < items.length; i++) {

                    if (items[i] == newValue) {
                        comboBox.setSelectedItem(display.get(i));
                        return;
                    }
                }
            };
            Config.getInstance().addSettingListener(setting.id, valueListener);
            valueListener.valueChanged(Config.getInstance().get(setting.id));

            I18N.addLangChangedListener(() -> {
                int selectedIndex = comboBox.getSelectedIndex();

                for (int i = 0; i < items.length; i++)
                    display.set(i, setting.dictionary.getMappingFor(items[i]));

                comboBox.setSelectedItem(display.get(selectedIndex));
            });

            return comboBox;

        } else if (setting.filter instanceof ValueFilter.Range) {

            ValueFilter.Range range = (ValueFilter.Range) setting.filter;

            if (setting.valueType.equals(Integer.class)) {

                int min = (int) range.min;
                int max = (int) range.max;

                GameSlider slider = new GameSlider(itemDimension, font, min, max, Config.getInstance().getInt(setting.id), setting.dictionary::getMappingFor);
                slider.addValueListener(newValue -> Config.getInstance().setInt(setting.id, newValue));
                Config.getInstance().addSettingListener(setting.id, newValue -> slider.setValue((int) newValue));
                I18N.addLangChangedListener(slider::setTextToVal);

                return slider;
            }
        } else if (setting.filter instanceof ValueFilter.Type) {

            if (setting.valueType.equals(Boolean.class)) {

                int dimensionLength = itemDimension.height;

                GameCheckbox checkbox = new GameCheckbox(new Dimension(dimensionLength, dimensionLength));
                checkbox.addCheckboxListener(newValue -> Config.getInstance().setBoolean(setting.id, newValue));
                checkbox.setChecked(Config.getInstance().getBoolean(setting.id));
                Config.getInstance().addSettingListener(setting.id, newValue -> checkbox.setChecked((boolean) newValue));

                return checkbox;
            }
        }

        throw new IllegalArgumentException(setting.toString());
    }
}