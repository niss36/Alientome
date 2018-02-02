package com.alientome.game;

import com.alientome.core.Context;
import com.alientome.core.util.Vec2;
import com.alientome.game.entities.Entity;
import com.alientome.game.level.Level;

import java.awt.*;

public class DebugInfo {

    private final Context context;
    private final Level level;

    private long prevTimeFrames;
    private int frames;
    private int fps; //Frames per second

    private long prevTimeUpdates;
    private int updates;
    private int ups; //Game Updates per second

    public DebugInfo(Context context, Level level) {
        this.context = context;
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

    public void draw(Graphics g) {

        Entity player = level.getControlled();
        Vec2 playerPos = player.getPos();

        int baseLineY = g.getFontMetrics().getAscent() + 5;
        int lineHeight = g.getFontMetrics().getHeight();

        int maxFPS = context.getConfig().getAsInt("maxFPS");
        String fpsLimit;
        if (maxFPS != 0)
            fpsLimit = String.valueOf(maxFPS);
        else fpsLimit = "U";

        g.drawString(fps + "FPS / " + fpsLimit, 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString(ups + "UPS", 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString("X / Y : " + playerPos.x + " / " + playerPos.y, 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString("G : " + (player.isOnGround() ? 1 : 0), 5, baseLineY);
        baseLineY += lineHeight;
        g.drawString("T " + level.getTimeTicks(), 5, baseLineY);
    }
}
