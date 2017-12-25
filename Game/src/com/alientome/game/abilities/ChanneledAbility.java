package com.alientome.game.abilities;

import com.alientome.game.entities.Entity;

public abstract class ChanneledAbility extends ActiveAbility {

    private final int maxChannelTime;
    private final int actTime;
    private int state = -1;

    protected ChanneledAbility(Entity owner, int cooldown,
                               int maxChannelTime, int actTime) {
        super(owner, cooldown);
        this.maxChannelTime = maxChannelTime;
        this.actTime = actTime;
    }

    public void startChannel() {
        state = 0;
    }

    public abstract void stopChannel();

    protected void endChannel() {
        state = -1;
    }

    protected boolean isStopped() {
        return state == -1;
    }

    protected abstract void onMaxChannel();

    protected abstract void onChannelProgress(int currentState);

    protected abstract void act();

    @Override
    public void update() {
        super.update();

        if (state >= 0) {

            if (state > maxChannelTime) {
                onMaxChannel();
            } else {
                onChannelProgress(state);

                if (state == actTime)
                    act();

                state++;
            }
        }
    }
}
