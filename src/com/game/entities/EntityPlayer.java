package com.game.entities;

import com.game.control.Control;
import com.game.control.Controller;
import com.game.level.Level;
import com.settings.Config;
import com.util.Vec2;
import com.util.visual.GameGraphics;

import java.awt.*;

public class EntityPlayer extends EntityLiving {

    private int ghostBallCoolDown = 0;

    private int chargeState = -1;

    @SuppressWarnings("SameParameterValue")
    EntityPlayer(Vec2 pos, Level level) {

        super(pos, new Dimension(20, 31), level, 20);
    }

    @Override
    void preUpdateInternal() {

        if (ghostBallCoolDown > 0) ghostBallCoolDown--;

        if (chargeState >= 0) {
            if (chargeState >= 40) handler.setAnimationUsed(2);
            else {

                handler.setAnimationUsed(1);
                chargeState++;
            }
            maxVelocity = Math.max(5 - chargeState / 10, 2);
        } else {

            handler.setAnimationUsed(0);
            maxVelocity = 5;
        }
    }

    @Override
    protected void drawDebug(GameGraphics g) {
        super.drawDebug(g);

        if (Config.getInstance().getBoolean("showBlockIn") && blockIn != null) {

            Color c = g.graphics.getColor();
            g.graphics.setColor(new Color(255, 0, 0, 100));
            blockIn.getBoundingBox().fill(g);
            g.graphics.setColor(c);
        }
    }

    private void throwGhostBall() {

        if (ghostBallCoolDown == 0) {

            level.spawnEntity(new EntityGhostBall(this, chargeState >= 40));

            ghostBallCoolDown = 33;
        }
    }

    public void startCharging() {

        if (ghostBallCoolDown == 0 && chargeState < 0) chargeState = 0;
    }

    public void stopCharging() {
        if (chargeState >= 0) {
            throwGhostBall();
            chargeState = -1;
        }
    }

    @Override
    public Controller newController() {
        Controller controller = super.newController();
        controller.addControl(Control.createChargeControl(this, "special1",
                entity -> ((EntityPlayer) entity).startCharging(),
                entity -> ((EntityPlayer) entity).stopCharging()));

        return controller;
    }
}
