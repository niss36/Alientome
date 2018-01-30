package com.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatInterface extends GameComponent {

    private final List<String> messages = new ArrayList<>();
    private final List<String> sentHistory = new ArrayList<>();
    private StringBuilder typing = new StringBuilder();
    private int caretPos = 0;
    private boolean caretShown = false;
    private int historyIndex = 0;

    public ChatInterface(/*Supplier<Level> levelSupplier*/) {

        /*InputManager.getInstance().setListener("chat", "send", makeListener(() -> {
            String trimmed = typing.toString().trim();
            if (trimmed.length() > 0) {

                if (trimmed.startsWith("/")) {
                    Level level = levelSupplier.get();
                    CommandSender sender = level.getControlled();
                    level.executeCommand(sender, trimmed);
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
                char c = event.getKeyChar();

                if (c >= ' ' && c != KeyEvent.CHAR_UNDEFINED)
                    typing.insert(caretPos++, c);
                return true;
            }
            return false;
        });

        GameEventDispatcher.getInstance().register(GameEventType.GAME_EXIT, e -> messages.clear());

        GameEventDispatcher.getInstance().register(GameEventType.MESSAGE_SENT, e -> messages.add((String) e.source));*/

        Timer timer = new Timer(500, e -> caretShown = !caretShown);
        timer.setRepeats(true);
        timer.start();

        setOpaque(false);
        setDimension(new Dimension(400, 400));
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
