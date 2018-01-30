package com.alientome.impl.ai;

import com.alientome.game.ai.AI;

/**
 * <code>AI</code> to repeat an other <code>AI</code>. Fails if the repeated <code>AI</code> fails
 * and succeeds when the target <code>AI</code> was repeated the specified amount of times.
 * (If <code>this.time < 0</code>, it will never succeed as it will repeat the <code>AI</code> until it fails)
 */
public class AIRepeat extends AI {

    private final AI ai;
    private final int originalTimes;
    private int times;

    /**
     * @param ai    the <code>AI</code> to repeat
     * @param times the number of repetitions.
     *              If <code>times < 0</code>, repeat indefinitely
     */
    private AIRepeat(AI ai, int times) {
        super(ai);

        this.ai = ai;
        this.times = times;
        this.originalTimes = times;
    }

    /**
     * Repeat an <code>AI</code> indefinitely
     *
     * @param ai the <code>AI</code> to repeat
     */
    public AIRepeat(AI ai) {
        this(ai, -1);
    }

    @Override
    public void act() {
        if (ai.isFailure()) fail();
        else if (ai.isSuccess()) {
            if (times == 0) succeed();
            else if (times > 0 || times <= -1) {
                times--;
                ai.reset();
                ai.start();
            }
        } else ai.act();
    }

    @Override
    public void reset() {
        times = originalTimes;
    }

    @Override
    public void start() {
        super.start();
        ai.start();
    }
}
