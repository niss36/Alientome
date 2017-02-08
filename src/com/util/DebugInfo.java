package com.util;

import com.game.entities.Entity;
import com.game.level.Level;

import java.awt.*;

public class DebugInfo {

    private final Level level;

    private long prevTimeFrames;
    private int frames;
    private int fps; //Frames per second

    private long prevTimeUpdates;
    private int updates;
    private int ups; //Game Updates per second

    private int cellUpdates;

    public DebugInfo(Level level) {
        this.level = level;
    }

    public void registerFrame() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - prevTimeFrames >= 1000) {

            prevTimeFrames = currentTime;
            fps = frames;
            frames = 0;
        } else
            frames++;
    }

    public void registerUpdate() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - prevTimeUpdates >= 1000) {

            prevTimeUpdates = currentTime;
            ups = updates;
            updates = 0;
        } else
            updates++;
    }

    public void registerCellUpdates(int cellUpdates) {
        this.cellUpdates = cellUpdates;
    }

    public void draw(Graphics g) {

        Entity player = level.getControlled();
        Vec2 playerPos = player.getPos();

        int baseLineY = g.getFontMetrics().getAscent() + 5;
        int lineHeight = g.getFontMetrics().getHeight();

        g.drawString(fps + "FPS", 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString(ups + "UPS", 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString(cellUpdates + " cell updates", 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString("X / Y : " + playerPos.x + " / " + playerPos.y, 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString("G : " + (player.isOnGround() ? 1 : 0), 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString("T " + level.getTimeTicks(), 5, baseLineY);
    }
}
