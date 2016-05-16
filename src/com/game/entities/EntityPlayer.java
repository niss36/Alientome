package com.game.entities;

import com.game.Level;
import com.gui.Frame;
import com.util.visual.AnimationInfo;

import java.awt.*;

public class EntityPlayer extends EntityLiving {

    private int ghostBallCoolDown = 0;

    private float chargeState = -1;

    @SuppressWarnings("SameParameterValue")
    public EntityPlayer(int x, int y, Level level) {

        super(x, y, new Dimension(20, 31), level, 20);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        if (ghostBallCoolDown > 0) ghostBallCoolDown--;

        if (chargeState >= 0) {
            if (chargeState >= 4) setAnimationInUse(2);
            else {
                setAnimationInUse(1);
                chargeState += 0.1f;
            }
            maxVelocity = Math.max(5 - (int) chargeState, 2);
        } else {
            setAnimationInUse(0);
            maxVelocity = 5;
        }
    }

    @Override
    public void onDeath() {

        Frame.getInstance().panelGame.game.playerDeath();
    }

    @Override
    protected AnimationInfo[] createAnimationInfo() {
        AnimationInfo[] info = new AnimationInfo[3];
        info[0] = new AnimationInfo("Alientome", 2, 10);
        info[1] = new AnimationInfo("Alientome/Charge", 5, 10);
        info[2] = new AnimationInfo("Alientome/Charged", 2, 10);

        return info;
    }

    private void throwGhostBall(boolean big) {

        if (ghostBallCoolDown == 0) {

            level.spawnEntity(new EntityGhostBall(this, big));

            ghostBallCoolDown = 33;
        }
    }

    public void startCharging() {

        if (ghostBallCoolDown == 0 && chargeState < 0) chargeState = 0;
    }

    public void stopCharging() {
        if (chargeState >= 0) {
            throwGhostBall(chargeState >= 4);
            chargeState = -1;
        }
    }
}
