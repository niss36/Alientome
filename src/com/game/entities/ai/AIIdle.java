package com.game.entities.ai;

import com.game.entities.Entity;

public class AIIdle extends AI {

    int time;

    public AIIdle(Entity entity) {
        super(entity);

        time = aIRandom.nextInt(25) + 25;
    }

    @Override
    public void act() {

        if(isRunning()) {
            if(entity.isDead()) fail();
            else if(time == 0) succeed();
            else time--;
        }

    }

    @Override
    public void reset() {
        time = aIRandom.nextInt(25) + 25;
    }
}
