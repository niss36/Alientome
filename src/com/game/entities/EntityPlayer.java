package com.game.entities;

import com.game.level.Level;
import com.gui.Frame;
import com.util.Config;

import java.awt.*;

public class EntityPlayer extends EntityLiving {

    private int ghostBallCoolDown = 0;

    private int chargeState = -1;

    @SuppressWarnings("SameParameterValue")
    EntityPlayer(int x, int y, Level level) {

        super(x, y, new Dimension(20, 31), level, 20);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

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
    protected void drawDebug(Graphics g, Point origin) {
        super.drawDebug(g, origin);

        if (Config.getInstance().getBoolean("Debug.ShowBlockIn") && blockIn != null) {

            Color c = g.getColor();
            g.setColor(new Color(255, 0, 0, 100));
            blockIn.getBoundingBox().fill(g, origin);
            g.setColor(c);
        }
    }

    @Override
    public void onDeath() {

        Frame.getInstance().panelGame.game.playerDeath();
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
}
