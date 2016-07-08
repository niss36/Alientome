package com.gui;

import com.util.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelMainMenu extends JPanel {

    //CardLayout constants
    private final String[] choices = {"Menu", "MenuSaves"};
    private final int MENU = 0, MENU_SAVES = 1;
    private final CardLayout cl = new CardLayout();
    private final JPanel glass;
    private final String versionStr;

    public PanelMainMenu() {
        super();

        versionStr = "Version " + Config.getInstance().getString("Version");

        glass = new JPanel();

        glass.setLayout(cl);

        glass.setOpaque(false);


        Dimension d = new Dimension(300, 80);
        Font f = new Font("Serif", Font.BOLD, 50);

        String[] menuNames = {
                "Play",
                "Options",
                "Quit"};

        ActionListener[] menuListeners = {
                e -> showCard(MENU_SAVES),
                e -> {
                },
                e -> System.exit(0)};

        JPanel menu = MenuUtility.createMenu(d, "Main Menu", f, menuNames, menuListeners);

        MenuSavePanel[] savePanels = new MenuSavePanel[3];

        Dimension d1 = new Dimension(80, 80);

        for (int i = 0; i < savePanels.length; i++) {

            savePanels[i] = new MenuSavePanel(d, d1, f, i + 1);
        }

        MenuButton savesBack = new MenuButton("Back", d, f);
        savesBack.addActionListener(e -> showCard(MENU));

        JPanel menuSaves = MenuUtility.createMenu(d, "Saves", f, savePanels, savesBack);

        glass.add(menu, choices[MENU]);
        glass.add(menuSaves, choices[MENU_SAVES]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString(versionStr, 5, getHeight() - 5);
    }

    public void showCard(int index) {

        cl.show(glass, choices[index]);
    }

    JPanel getGlass() {

        return glass;
    }
}
