package com.alientome.gui.fx.controllers;

import com.alientome.core.Context;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.keybindings.InputManager;
import com.alientome.core.util.GameFont;
import com.alientome.core.util.Util;
import com.alientome.game.Game;
import com.alientome.game.GameContext;
import com.alientome.game.GameRenderer;
import com.alientome.game.commands.messages.ConsoleMessage;
import com.alientome.game.events.*;
import com.alientome.game.level.Level;
import com.alientome.gui.fx.DialogsUtil;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.alientome.core.events.GameEventType.*;
import static com.alientome.core.util.Colors.*;
import static com.alientome.core.util.Util.deepCopy;
import static com.alientome.core.util.Util.makeListener;
import static com.alientome.game.profiling.ExecutionTimeProfiler.theProfiler;

public class FXMLGameController extends FXMLController implements GameRenderer {

    private static final Font DEFAULT_FONT = GameFont.get(1);
    private static final Font DEBUG_FONT = GameFont.get(2);

    private Game game;

    private Transition transition;

    private boolean debug;
    private boolean takeScreenshot;

    private WritableImage image;

    private BufferedImage frontBuffer;
    private BufferedImage backBuffer;

    private Rectangle bounds;

    private long renders;
    private long averageRenderTime;

    private AnimationTimer timer;

    @Override
    public void init(Scene scene) {

        game = new Game(this, (GameContext) context);

        GameEventDispatcher dispatcher = context.getDispatcher();

        dispatcher.register(GAME_EXIT, e -> {
            manager.switchToScene("MAIN");
            context.getInputManager().setActiveContext(null);
            pauseMenu.setVisible(false);
            deathMenu.setVisible(false);
            pane.setOpacity(0);
            timer.stop();
            frontBuffer = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            backBuffer = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            image = null;
            Platform.runLater(() -> canvas.getGraphicsContext2D().clearRect(0, 0, bounds.width, bounds.height));
        });
        dispatcher.register(GAME_PAUSE, e -> {
            pauseMenu.setVisible(true);
            if (transition != null) {
                transition.stop();
                transition = null;
            }
            pane.setOpacity(0.5);
            timer.stop();
        });
        dispatcher.register(GAME_RESUME, e -> {
            pauseMenu.setVisible(false);
            deathMenu.setVisible(false);
            timer.start();
            transition = new Transition() {

                {
                    setCycleDuration(Duration.millis(330));
                }

                @Override
                protected void interpolate(double frac) {
                    pane.setOpacity(0.5 * (1 - frac));
                }
            };
            transition.playFromStart();
        });
        dispatcher.register(GAME_DEATH, e -> {
            transition = new Transition() {

                {
                    setCycleDuration(Duration.millis(330));
                }

                @Override
                protected void interpolate(double frac) {
                    pane.setOpacity(0.5 * frac);
                }
            };
            transition.setOnFinished(event -> deathMenu.setVisible(true));
            transition.playFromStart();
        });
        dispatcher.register(GAME_ERROR, e -> Platform.runLater(() -> {
            DialogsUtil.showErrorDialog(context, ((GameErrorEvent) e).error);
            context.getDispatcher().submit(new GameExitEvent());
        }));
        dispatcher.register(MESSAGE_SENT, e -> Platform.runLater(() -> consoleView.getItems().add(((MessageEvent) e).message)));

        InputManager inputManager = context.getInputManager();

        inputManager.setListener("running", "debug", makeListener(() -> debug = !debug));
        inputManager.setListener("running", "console", makeListener(this::openConsole));
        inputManager.setListener("global", "profileDump", makeListener(theProfiler::dumpProfileData));
        inputManager.setListener("global", "screenshot", makeListener(this::tryTakeScreenshot));
        inputManager.setListener("console", "close", makeListener(this::closeConsole));

        I18N i18N = context.getI18N();

        i18N.applyBindTo((Labeled) pauseMenu.lookup(".title"));

        for (Node node : pauseMenu.lookupAll(".button"))
            i18N.applyBindTo((Labeled) node);

        i18N.applyBindTo((Labeled) deathMenu.lookup(".title"));

        for (Node node : deathMenu.lookupAll(".button"))
            i18N.applyBindTo((Labeled) node);

        scene.addEventHandler(KeyEvent.ANY, e -> {
            if (context.getInputManager().consumeEvent(e))
                e.consume();
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                GraphicsContext gc = canvas.getGraphicsContext2D();

                image = SwingFXUtils.toFXImage(frontBuffer, image);

                gc.drawImage(image, 0, 0);
            }
        };

        bounds = new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight());

        frontBuffer = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        backBuffer = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);

        pane.visibleProperty().bind(Bindings.notEqual(0, pane.opacityProperty()));

        consoleView.setCellFactory(param -> new MessageListCell(context));

        i18N.localeProperty().addListener(observable -> consoleView.refresh());

        consoleInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && console.isVisible()) consoleInput.requestFocus();
        });
    }

    private static class MessageListCell extends ListCell<ConsoleMessage> {

        private final Context context;

        MessageListCell(Context context) {
            this.context = context;
        }

        @Override
        protected void updateItem(ConsoleMessage item, boolean empty) {
            super.updateItem(item, empty);

            if (empty)
                setText(null);
            else
                setText(item.getMessage(context.getI18N()));
        }
    }

    @Override
    public void render(double interpolation) {

        theProfiler.startSection("Rendering");

        long renderStart = System.nanoTime();

        theProfiler.startSection("Rendering/Updating Graphics");

        Graphics graphics = backBuffer.createGraphics();
        graphics.setClip(bounds);
        graphics.setFont(DEFAULT_FONT);
        graphics.setColor(BACKGROUND);

        theProfiler.endSection("Rendering/Updating Graphics");

        if (takeScreenshot) {
            theProfiler.startSection("Rendering/Taking Screenshot");
            takeScreenshot = false;
            BufferedImage bufferCopy = deepCopy(frontBuffer);
            saveScreenshot(bufferCopy);
            theProfiler.endSection("Rendering/Taking Screenshot");
        }

        game.getLevel().draw(graphics, debug, interpolation);

        if (debug) {

            graphics.setColor(DEBUG_INFO_BG);
            graphics.fillRect(0, 0, 200, 200);
            graphics.setFont(DEBUG_FONT);
            graphics.setColor(DEBUG_INFO_TEXT);
            game.getDebugInfo().draw(graphics);
        }

        swapBuffers();

        game.getDebugInfo().registerFrame();

        long renderTime = System.nanoTime() - renderStart;

        averageRenderTime += (renderTime - averageRenderTime) / ++renders;

        theProfiler.startSection("Rendering/FPS Limit");
        int maxFPS = context.getConfig().getAsInt("maxFPS");
        if (maxFPS != 0) {

            long estimatedWorkTime = maxFPS * averageRenderTime + 33 * game.getAverageUpdateTime();
            long excess = 1_000_000_000 - estimatedWorkTime;

            if (excess > 0) {
                long sleepTime = excess / (maxFPS * 1_000_000);

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        theProfiler.endSection("Rendering/FPS Limit");

        theProfiler.endSection("Rendering");
    }

    private void tryTakeScreenshot() {

        // Soft alternative to synchronisation in order to prevent the buffer from being copied while rendering
        if (game.isPaused()) saveScreenshot(frontBuffer);
            // If the game is actively rendering, the next rendering pass will take the screenshot
        else takeScreenshot = true;
    }

    private void saveScreenshot(BufferedImage image) {

        new Thread(() -> Util.saveScreenshot(context.getFileManager(), image), "Thread-Screenshot").start();
    }

    private void swapBuffers() {
        BufferedImage temp = frontBuffer;
        frontBuffer = backBuffer;
        backBuffer = temp;
    }

    private void openConsole() {
        console.setVisible(true);

        context.getInputManager().setActiveContext("console");

        Platform.runLater(consoleInput::requestFocus);
    }

    private void closeConsole() {
        console.setVisible(false);
        consoleInput.setText("");

        context.getInputManager().setActiveContext("running");
    }

    @FXML
    private Canvas canvas;

    @FXML
    private GridPane console;

    @FXML
    private Pane pane;

    @FXML
    private GridPane pauseMenu;

    @FXML
    private GridPane deathMenu;

    @FXML
    private Button resume, reset, options, exit, deathReset, deathExit;

    @FXML
    private TextField consoleInput;

    @FXML
    private ListView<ConsoleMessage> consoleView;

    @FXML
    private void onButtonAction(ActionEvent e) {

        Object s = e.getSource();

        GameEventDispatcher dispatcher = context.getDispatcher();

             if (s == resume) dispatcher.submit(new GameResumeEvent());
        else if (s == reset) dispatcher.submit(new GameResetEvent());
        else if (s == options) manager.pushScene("OPTIONS");
        else if (s == exit || s == deathExit) dispatcher.submit(new GameExitEvent());
        else if (s == deathReset) {
            try {
                dispatcher.submitAndWait(new GamePauseEvent());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dispatcher.submit(new GameResetEvent());
        }
    }

    @FXML
    private void onConsoleSubmit(ActionEvent e) {

        String trimmed = consoleInput.getText().trim();

        if (!trimmed.isEmpty()) {

            Level level = game.getLevel();

            level.executeCommand(level.getControlled(), trimmed);
        }

        closeConsole();
    }
}
