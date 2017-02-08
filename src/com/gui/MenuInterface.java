package com.gui;

import com.events.GameEventDispatcher;
import com.keybindings.InputManager;
import com.keybindings.KeyBinding;
import com.settings.Config;
import com.settings.Setting;
import com.util.GameFont;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.events.GameEventType.*;
import static com.util.Util.parseXML;

public class MenuInterface extends GameComponent {

    private static final Element menusDocumentRoot;
    private static final Map<String, Element> menus = new HashMap<>();

    static {
        try {
            menusDocumentRoot = parseXML("menus");

            NodeList menusList = menusDocumentRoot.getElementsByTagName("menu");
            for (int i = 0; i < menusList.getLength(); i++) {

                Element menuNode = (Element) menusList.item(i);
                menus.put(menuNode.getAttribute("id"), menuNode);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private final Dimension itemDimension;
    private final String baseMenu;
    private final Stack<String> menuStack = new Stack<>();
    private final CardLayout cl = new CardLayout();
    private final List<Runnable> baseMenuPoppedListeners = new ArrayList<>();

    public MenuInterface(Dimension itemDimension, Font font, String baseMenu) {

        this.itemDimension = itemDimension;
        this.baseMenu = baseMenu;
        menuStack.push(baseMenu);

        setFont(font);
        setLayout(cl);
        setOpaque(false);

        List<String> reachableMenus = getReachableMenus(baseMenu, new ArrayList<>());

        for (String menuID : reachableMenus) {

            JPanel menu = createMenuPanel(menuID);
            add(menu, menuID);
        }
    }

    private static List<String> getReachableMenus(String baseMenu, List<String> reachableMenus) {

        if (baseMenu == null) throw new IllegalArgumentException("Base menu is null");

        reachableMenus.add(baseMenu);

        Element menu = menus.get(baseMenu);
        if (menu == null)
            throw new IllegalArgumentException("Base menu (" + baseMenu + ") does not correspond to any existing menus");

        NodeList buttonsList = menu.getElementsByTagName("button");
        for (int i = 0; i < buttonsList.getLength(); i++) {

            Element button = (Element) buttonsList.item(i);

            String action = button.getAttribute("action");
            if (action.equals("showMenu")) {

                String targetMenuID = button.getAttribute("data");
                if (!reachableMenus.contains(targetMenuID))
                    getReachableMenus(targetMenuID, reachableMenus);
            }
        }

        return reachableMenus;
    }

    private JPanel createMenuPanel(String menuID) {

        GameEventDispatcher dispatcher = GameEventDispatcher.getInstance();

        Element menuNode = menus.get(menuID);

        String id = "menu." + menuID + ".";

        NodeList componentsList = menuNode.getChildNodes();

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(false);

        menu.add(Box.createVerticalStrut(40));
        GameLabel label = new GameLabel(itemDimension, id + "title", getFont(), GameLabel.CENTER);
        menu.add(label);

        for (int j = 0; j < componentsList.getLength(); j++) {

            if (componentsList.item(j).getNodeType() == Node.ELEMENT_NODE) {

                Element componentNode = (Element) componentsList.item(j);

                menu.add(Box.createVerticalStrut(20));

                String componentType = componentNode.getTagName();

                switch (componentType) {
                    case "panel":
                        String panelType = componentNode.getAttribute("type");

                        switch (panelType) {
                            case "saves":

                                for (int k = 0; k < 3; k++) {

                                    GameSavePanel savePanel = new GameSavePanel(itemDimension, getFont(), k + 1);
                                    menu.add(savePanel);
                                    if (k != 2) menu.add(Box.createVerticalStrut(20));
                                }
                                break;

                            case "options":

                                List<Setting> editableSettings = Config.getInstance().getEditableSettings();

                                for (Setting setting : editableSettings) {

                                    GameSettingPanel settingPanel = new GameSettingPanel(itemDimension, getFont(), setting);
                                    menu.add(settingPanel);
                                    menu.add(Box.createVerticalStrut(20));
                                }

                                GameButton resetOptions = new GameButton(itemDimension, "menu.options.reset", getFont());
                                resetOptions.addActionListener(e -> {
                                    if (new ConfirmDialog("menu.options.reset.title").showDialog() == GameDialog.ACCEPT)
                                        dispatcher.submit(null, CONFIG_RESET);
                                });
                                menu.add(resetOptions);
                                break;

                            case "keybindings":

                                List<KeyBinding> mappableBindings = InputManager.getInstance().getMappableBindings();

                                for (KeyBinding mappableBinding : mappableBindings) {

                                    GameControlPanel controlPanel = new GameControlPanel(itemDimension, getFont(), mappableBinding);
                                    menu.add(controlPanel);
                                    menu.add(Box.createVerticalStrut(20));
                                }
                                GameButton resetControls = new GameButton(itemDimension, "menu.controls.reset", getFont());
                                resetControls.addActionListener(e -> {
                                    if (new ConfirmDialog("menu.controls.reset.title").showDialog() == GameDialog.ACCEPT)
                                        dispatcher.submit(null, KEYBINDINGS_RESET);
                                });
                                menu.add(resetControls);
                                break;
                        }
                        break;

                    case "button":
                        String action = componentNode.getAttribute("action");
                        String data = componentNode.getAttribute("data");

                        String buttonUnlocalizedText = (!"".equals(data) ? id + data : "gui." + action);

                        GameButton button = new GameButton(itemDimension, buttonUnlocalizedText, getFont());
                        ActionListener listener;
                        switch (action) {
                            case "showMenu":
                                listener = e -> pushMenu(data);
                                break;

                            case "quit":
                                listener = e -> System.exit(0);
                                break;

                            case "back":
                            case "done":
                                listener = e -> popMenu();
                                break;

                            case "game":
                                switch (data) {
                                    case "resume":
                                        listener = e -> dispatcher.submit(null, GAME_RESUME);
                                        break;

                                    case "reset":
                                        listener = e -> dispatcher.submit(null, GAME_RESET);
                                        break;

                                    case "exit":
                                        listener = e -> dispatcher.submit(null, GAME_EXIT);
                                        break;

                                    default:
                                        listener = null;
                                }
                                break;

                            default:
                                listener = null;
                        }
                        button.addActionListener(listener);

                        menu.add(button);
                        break;

                    default:
                        throw new UnsupportedOperationException("Component type " + componentType + " is not supported");
                }
            }
        }

        return menu;
    }

    public void pushMenu(String menuID) {

        menuStack.push(menuID);
        cl.show(this, menuStack.peek());
    }

    public void popMenu() {

        if (menuStack.size() == 1) {
            baseMenuPoppedListeners.forEach(Runnable::run);
            return;
        }
        menuStack.pop();
        cl.show(this, menuStack.peek());
    }

    public void showBase() {

        menuStack.clear();
        pushMenu(baseMenu);
    }

    public void addBaseMenuPoppedListener(Runnable listener) {
        baseMenuPoppedListeners.add(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (debugUI) {

            g.setFont(GameFont.get(2));

            g.drawString("Menu [base=" + baseMenu + ", current=" + menuStack.peek() + "]", 5, 15);
        }
    }
}
