package com.game.entities;

import com.game.Level;
import com.gui.Frame;
import com.util.Config;
import com.util.visual.AnimationInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
            if(chargeState >= 4) setAnimationInUse(2);
            else {
                setAnimationInUse(1);
                chargeState += 0.1f;
            }
            maxVelocity = Math.max(5 - (int) chargeState, 2);
        } else {
            setAnimationInUse(0);
            maxVelocity = 5;
        }

        /*ArrayList<Integer> l = Frame.getInstance().panelGame.game.pressedKeys;
        if(!l.contains(Config.getInstance().getKey("Key.MoveLeft")) && !l.contains(Config.getInstance().getKey("Key.MoveRight")))
            if(motionX != 0) System.out.println((int) (x + Math.signum(motionX) * motionX * motionX + motionX / 2));*/
    }

    @Override
    public void onDeath() {

//        String[] options = {"Respawn", "Quit"};

        Frame.getInstance().panelGame.game.playerDeath();/*

        Frame.getInstance().panelGame.game.setPause(true);

        int i = JOptionPane.showOptionDialog(null, "You died.", "Oops", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        Frame.getInstance().panelGame.game.setPause(false);

        if (i == JOptionPane.YES_OPTION) level.reset();
        else System.exit(0);*/
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
