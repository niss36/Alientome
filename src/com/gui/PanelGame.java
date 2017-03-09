package com.gui;

import com.game.Game;
import com.events.GameEventDispatcher;
import com.game.GameRenderer;
import com.keybindings.InputManager;
import com.settings.Config;
import com.util.GameFont;
import com.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.events.GameEventType.*;
import static com.util.Util.deepCopy;
import static com.util.Util.makeListener;
import static com.util.profile.ExecutionTimeProfiler.theProfiler;

public class PanelGame extends JPanel implements GameRenderer {

    private final CardLayout cl = new CardLayout();
    private final JPanel glass;
    private final MenuInterface menuInterface;
    private final Font debugFont = GameFont.get(2);
    private final Font defaultFont = GameFont.get(1);
    private final Game game;
    private boolean debug = false;
    private BufferedImage frontBuffer;
    private BufferedImage backBuffer;
    private boolean takeScreenshot;
    private long renders;
    private long averageRenderTime;

    PanelGame() {

        game = new Game(this);

        GameEventDispatcher dispatcher = GameEventDispatcher.getInstance();
        InputManager manager = InputManager.getInstance();

        manager.setListener("running", "debug", makeListener(() -> debug = !debug));
        manager.setListener("global", "profileDump", makeListener(theProfiler::dumpProfileData));
        manager.setListener("global", "screenshot", makeListener(this::tryTakeScreenshot));

        glass = new JPanel();

        glass.setLayout(cl);

        glass.setOpaque(false);

        JPanel blank = new JPanel(null);
        blank.setOpaque(false);

        JPanel chat = new JPanel();
        chat.setLayout(new BoxLayout(chat, BoxLayout.X_AXIS));
        chat.setOpaque(false);

        JPanel chatInner = new JPanel();
        chatInner.setLayout(new BoxLayout(chatInner, BoxLayout.Y_AXIS));
        chatInner.setOpaque(false);

        chatInner.add(Box.createVerticalGlue());
        chatInner.add(new ChatInterface(game::getLevel));
        chatInner.add(Box.createRigidArea(new Dimension(0, 200)));

        chat.add(chatInner);
        chat.add(Box.createHorizontalGlue());

        Dimension d = new Dimension(200, 44);

        menuInterface = new MenuInterface(d, null, "pause");

        manager.setListener("menu.pause", "back", makeListener(menuInterface::popMenu));

        menuInterface.addBaseMenuPoppedListener(() -> dispatcher.submit(null, GAME_RESUME));

        MenuInterface deathInterface = new MenuInterface(d, null, "death");

        manager.setListener("menu.death", "respawn", makeListener(() -> dispatcher.submit(null, GAME_RESET)));
        manager.setListener("menu.death", "exit", makeListener(() -> dispatcher.submit(null, GAME_EXIT)));
        manager.setListener("running", "chat", makeListener(() -> {
            cl.show(glass, "chat");
            InputManager.getInstance().setActiveContext("chat");
        }));
        manager.setListener("chat", "escape", makeListener(() -> {
            cl.show(glass, "blank");
            InputManager.getInstance().setActiveContext("running");
        }));

        dispatcher.register(GAME_EXIT, e -> manager.setActiveContext("menu.main"));
        dispatcher.register(GAME_PAUSE, e -> cl.show(glass, "menu"));
        dispatcher.register(GAME_RESUME, e -> cl.show(glass, "blank"));
        dispatcher.register(GAME_DEATH, e -> cl.show(glass, "death"));

        glass.add(blank, "blank");
        glass.add(chat, "chat");
        glass.add(menuInterface, "menu");
        glass.add(deathInterface, "death");
    }

    @Override
    public void render(double interpolation) {

        theProfiler.startSection("Rendering");

        long renderStart = System.nanoTime();

        theProfiler.startSection("Rendering/Updating Graphics");

        if (backBuffer == null) {
            System.out.println("Creating buffer");
            backBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Graphics graphics = backBuffer.createGraphics();
        graphics.setClip(getBounds());
        graphics.setFont(defaultFont);

        theProfiler.endSection("Rendering/Updating Graphics");

        if (takeScreenshot) {
            theProfiler.startSection("Rendering/Taking Screenshot");
            takeScreenshot = false;
            BufferedImage bufferCopy = deepCopy(frontBuffer);
            saveScreenshot(bufferCopy);
            theProfiler.endSection("Rendering/Taking Screenshot");
        }

        game.getLevel().draw(graphics, debug, interpolation);

        BufferedImage swap = frontBuffer;

        frontBuffer = backBuffer;

        backBuffer = swap;

        game.getDebugInfo().registerFrame();

        repaint();

        long renderTime = System.nanoTime() - renderStart;

        averageRenderTime += (renderTime - averageRenderTime) / ++renders;

        theProfiler.startSection("Rendering/Syncing");
        Toolkit.getDefaultToolkit().sync();
        int maxFPS = Config.getInstance().getInt("maxFPS");
        if (maxFPS != 0) {

            long estimatedWorkTime = maxFPS * averageRenderTime + 33 * game.getAverageUpdateTime();
//            System.out.println((double) averageRenderTime / 1_000_000 + "  " + (double) game.getAverageUpdateTime() / 1_000_000 + "  " + (double) estimatedWorkTime / 1_000_000);
            long excess = 1_000_000_000 - estimatedWorkTime;

            if (excess > 0) {

                long sleepTime = excess / (maxFPS * 1_000_000);
                if (sleepTime > 0) {
                    /*long timeBetweenRendersMS = (timeBetweenRenders / 1_000_000);
                    System.out.println("Sleep : " + sleepTime + "ms; Between renders : " + timeBetweenRendersMS + "ms; Render : " + renderTime + "ms");*/

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        theProfiler.endSection("Rendering/Syncing");

        theProfiler.endSection("Rendering");
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(frontBuffer, 0, 0, this);

        if (debug) {

            g.setColor(new Color(96, 96, 96, 128));
            g.fillRect(0, 0, 200, 200);
            g.setFont(debugFont);
            g.setColor(Color.white);
            game.getDebugInfo().draw(g);
        }

        if (!game.isRunning()) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    void showBaseMenu() {
        menuInterface.showBase();
    }

    JComponent getGlass() {
        return glass;
    }

    private void tryTakeScreenshot() {

        // Soft alternative to synchronisation in order to prevent the buffer from being copied while rendering
        if (game.isPaused()) saveScreenshot(frontBuffer);
        // If the game is actively rendering, the next rendering pass will take the screenshot
        else takeScreenshot = true;
    }

    private void saveScreenshot(BufferedImage image) {

        new Thread(() -> Util.saveScreenshot(image), "Thread-Screenshot").start();
    }
}
