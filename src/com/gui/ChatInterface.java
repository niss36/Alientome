package com.gui;

import com.events.GameEventDispatcher;
import com.events.GameEventType;
import com.game.command.CommandSender;
import com.game.level.Level;
import com.keybindings.InputManager;
import com.util.GameFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.util.Util.makeListener;

public class ChatInterface extends GameComponent {

    private final List<String> messages = new ArrayList<>();
    private final List<String> sentHistory = new ArrayList<>();
    private StringBuilder typing = new StringBuilder();
    private int caretPos = 0;
    private boolean caretShown = false;
    private int historyIndex = 0;

    public ChatInterface(Supplier<Level> levelSupplier) {

        InputManager.getInstance().setListener("chat", "send", makeListener(() -> {
            String trimmed = typing.toString().trim();
            if (trimmed.length() > 0) {

                if (trimmed.startsWith("/")) {
                    Level level = levelSupplier.get();
                    CommandSender e = level.getControlled();
                    level.executeCommand(e, trimmed);
                } else
                    GameEventDispatcher.getInstance().submit(trimmed, GameEventType.MESSAGE_SENT);
                typing = new StringBuilder();
                caretPos = 0;

                sentHistory.add(trimmed);
                historyIndex = 0;
            }
        }));

        InputManager.getInstance().setListener("chat", "deleteBefore", makeListener(() -> {
            if (typing.length() > 0 && caretPos > 0)
                typing.delete(--caretPos, caretPos + 1);
        }));

        InputManager.getInstance().setListener("chat", "deleteAfter", makeListener(() -> {
            if (typing.length() > 0)
                typing.delete(caretPos, caretPos + 1);
        }));

        InputManager.getInstance().setListener("chat", "moveCaretLeft", makeListener(() -> {
            if (caretPos > 0)
                caretPos--;
        }));

        InputManager.getInstance().setListener("chat", "moveCaretRight", makeListener(() -> {
            if (typing.length() > caretPos)
                caretPos++;
        }));

        InputManager.getInstance().setListener("chat", "historyBefore", makeListener(() -> {
            if (historyIndex < sentHistory.size()) {
                typing = new StringBuilder(sentHistory.get(sentHistory.size() - ++historyIndex));
                caretPos = 0;
            }
        }));

        InputManager.getInstance().setListener("chat", "historyAfter", makeListener(() -> {

        }));

        InputManager.getInstance().addUnknownEventHandler("chat", event -> {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                /*int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    if (typing.length() > 0)
                        typing.delete(--caretPos, caretPos + 1);

                } else if (keyCode == KeyEvent.VK_DELETE) {
                    if (typing.length() > caretPos)
                        typing.delete(caretPos, caretPos + 1);

                } else if (keyCode == KeyEvent.VK_LEFT) {
                    if (typing.length() > 0)
                        caretPos--;

                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    if (typing.length() > caretPos)
                        caretPos++;

                } else if (keyCode == KeyEvent.VK_UP){
                    if (historyIndex < sentHistory.size()) {

                        typing = new StringBuilder(sentHistory.get(sentHistory.size() - ++historyIndex));
                        caretPos = 0;
                    }

                } else if (keyCode == KeyEvent.VK_DOWN) {
                    if (historyIndex >= 1) {


                    }

                } else {*/
                    char c = event.getKeyChar();

                    if (c >= ' ' && c != KeyEvent.CHAR_UNDEFINED)
                        typing.insert(caretPos++, c);
//                }

                return true;
            }
            return false;
        });

        GameEventDispatcher.getInstance().register(GameEventType.GAME_EXIT, e -> messages.clear());

        GameEventDispatcher.getInstance().register(GameEventType.MESSAGE_SENT, e -> messages.add((String) e.source));

        Timer timer = new Timer(500, e -> caretShown = !caretShown);
        timer.setRepeats(true);
        timer.start();

        setOpaque(false);
        setDimension(new Dimension(400, 400));
        setFont(GameFont.get(3));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(128, 128, 128, 192));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);

        FontMetrics metrics = g.getFontMetrics();

        int y = getHeight() - metrics.getDescent();
        int h = metrics.getHeight();

        String temp = typing.toString();

        g.drawString(temp, 5, y);
        if (caretShown) {

            int x = 5 + metrics.stringWidth(temp.substring(0, caretPos));
            g.drawString("|", x, y);
        }
        y -= h;

        for (int i = messages.size() - 1; i >= 0; i--) {

            g.drawString(messages.get(i), 5, y);
            y -= h;
        }
    }
}
