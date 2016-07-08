package com.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Utility class to easily create menus
 */
@SuppressWarnings("WeakerAccess")
class MenuUtility {

    /**
     * @see #createMenu(Dimension, String, Font, String[], ActionListener[])
     */
    @SuppressWarnings("SameParameterValue")
    static JPanel createMenu(Dimension itemDimension, String title, String[] buttonNames, ActionListener[] listeners) {
        return createMenu(itemDimension, title, null, buttonNames, listeners);
    }

    /**
     * Creates a menu with one column of <code>MenuButton</code>s.
     *
     * @param itemDimension the <code>Dimension</code> of this menu's <code>MenuItem</code>s.
     * @param title         the text of the title <code>MenuLabel</code>.
     * @param font          the <code>MenuItem</code>s' <code>Font</code>
     * @param buttonNames   the text of the buttons.
     * @param listeners     the buttons' actions.
     * @return a <code>JPanel</code> containing all of the menu's components.
     */
    @SuppressWarnings("SameParameterValue")
    static JPanel createMenu(Dimension itemDimension, String title, Font font, String[] buttonNames, ActionListener[] listeners) {

        JPanel menu = createMenuBase(title, itemDimension, font);

        for (int i = 0; i < buttonNames.length; i++) {

            menu.add(Box.createVerticalStrut(20));
            MenuButton button = new MenuButton(buttonNames[i], itemDimension, font);
            button.addActionListener(listeners[i]);
            menu.add(button);
        }

        return menu;
    }

    @SuppressWarnings("SameParameterValue")
    static JPanel createMenu(Dimension itemDimension, String title, Font font, JComponent[] rows, MenuButton... additionalButtons) {

        JPanel menu = createMenuBase(title, itemDimension, font);

        for (JComponent row : rows) {

            menu.add(Box.createVerticalStrut(20));
            menu.add(row);
        }

        for (MenuButton button : additionalButtons) {

            menu.add(Box.createVerticalStrut(20));
            menu.add(button);
        }

        return menu;
    }

    /**
     * @see #createLabelledMenu(Dimension, String, Font, String[], MenuButton[], MenuItem...)
     */
    static JPanel createLabelledMenu(Dimension itemDimension, String title, String[] labelNames, MenuButton[] buttons, MenuItem... additionalButtons) {
        return createLabelledMenu(itemDimension, title, null, labelNames, buttons, additionalButtons);
    }

    /**
     * Create a menu with one column of <code>MenuLabel</code>s aligned with one column of <code>MenuButton</code>s.
     * Additional buttons can be added and will be placed as a single column underneath the preceding double column.
     *
     * @param itemDimension     the <code>Dimension</code> of this menu's <code>MenuItem</code>s.
     * @param title             the text of the title <code>MenuLabel</code>.
     * @param font              the <code>MenuItem</code>s' <code>Font</code>
     * @param labelNames        the text of the <code>MenuLabel</code>s.
     * @param buttons           the <code>MenuButton</code>s.
     * @param additionalButtons optional. Additional <code>MenuButton</code>s placed at the end of the menu.
     * @return a <code>JPanel</code> containing all of the menu's components.
     */
    static JPanel createLabelledMenu(Dimension itemDimension, String title, Font font, String[] labelNames, MenuButton[] buttons, MenuItem... additionalButtons) {

        MenuLabel[] labels = new MenuLabel[labelNames.length];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new MenuLabel(labelNames[i], itemDimension, MenuLabel.RIGHT, font);
        }

        return createDoubleMenu(itemDimension, title, font, labels, 0, buttons, additionalButtons);
    }

    /**
     * @see #createDoubleMenu(Dimension, String, Font, MenuItem[], int, MenuItem[], MenuItem...)
     */
    @SuppressWarnings("SameParameterValue")
    public static JPanel createDoubleMenu(Dimension itemDimension, String title, MenuItem[] leftItems, int gap, MenuItem[] rightItems, MenuItem... additionalItems) {
        return createDoubleMenu(itemDimension, title, null, leftItems, gap, rightItems, additionalItems);
    }

    /**
     * Create a menu with one column of <code>MenuItem</code>s aligned with another column of <code>MenuItem</code>s.
     * Additional items can be added and will be placed as a single column underneath the preceding double column.
     *
     * @param itemDimension   the <code>Dimension</code> of this menu's <code>MenuItem</code>s.
     * @param title           the text of the title <code>MenuLabel</code>.
     * @param leftItems       the left column of <code>MenuItem</code>s.
     * @param gap             the spacing between the two columns in pixels.
     * @param rightItems      the right column of <code>MenuItem</code>s.
     * @param additionalItems optional. Additional <code>MenuItem</code>s placed at the end of the menu.
     * @return a <code>JPanel</code> containing all of the menu's components.
     */
    @SuppressWarnings("SameParameterValue")
    static JPanel createDoubleMenu(Dimension itemDimension, String title, Font font, MenuItem[] leftItems, int gap, MenuItem[] rightItems, MenuItem... additionalItems) {

        JPanel menu = createMenuBase(title, itemDimension, font);

        for (int i = 0; i < leftItems.length; i++) {

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);

            row.add(leftItems[i]);
            row.add(Box.createRigidArea(new Dimension(gap, 0)));
            row.add(rightItems[i]);

            menu.add(Box.createVerticalStrut(20));
            menu.add(row);
        }

        for (MenuItem item : additionalItems) {

            menu.add(Box.createVerticalStrut(20));
            menu.add(item);
        }

        return menu;
    }

    /**
     * @see #createChoiceMenu(Dimension, String, Font, int, MenuItem...)
     */
    @SuppressWarnings("SameParameterValue")
    static JPanel createChoiceMenu(Dimension itemDimension, String title, int gap, MenuItem... choices) {
        return createChoiceMenu(itemDimension, title, null, gap, choices);
    }

    /**
     * Creates a menu with one row of <code>MenuItem</code>s.
     *
     * @param itemDimension the <code>Dimension</code> of this menu's <code>MenuItem</code>s.
     * @param title         the text of the title <code>MenuLabel</code>.
     * @param gap           the spacing between two <code>MenuItem</code>s in pixels.
     * @param choices       the elements constituting this menu.
     * @return a <code>JPanel</code> containing all of the menu's components.
     */
    @SuppressWarnings("SameParameterValue")
    static JPanel createChoiceMenu(Dimension itemDimension, String title, Font font, int gap, MenuItem... choices) {

        JPanel menu = createMenuBase(title, itemDimension, font);

        if (choices.length > 0) {

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);

            row.add(choices[0]);

            for (int i = 1; i < choices.length; i++) {

                row.add(Box.createRigidArea(new Dimension(gap, 0)));
                row.add(choices[i]);
            }

            menu.add(Box.createVerticalStrut(20));
            menu.add(row);
        }

        return menu;
    }

    /**
     * Private method to create the base of the titled menu.
     *
     * @param title         the text of the title <code>MenuLabel</code>.
     * @param itemDimension the <code>Dimension</code> of this menu's <code>MenuItem</code>s.
     * @param font          the <code>Font</code> of the <code>MenuLabel</code>
     * @return a basic menu.
     */
    private static JPanel createMenuBase(String title, Dimension itemDimension, Font font) {

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(false);

        menu.add(Box.createVerticalStrut(40));

        MenuLabel label = new MenuLabel(title, itemDimension, MenuLabel.CENTER, font);

        menu.add(label);

        return menu;
    }
}
