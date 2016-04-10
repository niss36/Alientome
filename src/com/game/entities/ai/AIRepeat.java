package com.game.entities.ai;

public class AIRepeat extends AI {

    private AI ai;
    private int times;
    private int originalTimes;

    public AIRepeat(AI ai, int times) {
        super(ai.entity);

        this.ai = ai;
        this.times = times;
        this.originalTimes = times;
    }

    public AIRepeat(AI ai) {
        this(ai, -1);
    }

    @Override
    public void start() {
        super.start();
        ai.start();
    }

    @Override
    public void reset() {
        times = originalTimes;
    }

    @Override
    public void act() {
        if(ai.isFailure()) fail();
        else if(ai.isSuccess()) {
            if(times == 0) succeed();
            else if(times > 0 || times <= -1) {
                times--;
                ai.reset();
                ai.start();
            }
        } else ai.act();
    }
}
