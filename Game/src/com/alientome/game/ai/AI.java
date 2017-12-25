package com.alientome.game.ai;

import com.alientome.game.entities.Entity;

/**
 * <code>AI</code> is used to simulate intelligent behavior for non-player entities.
 */
public abstract class AI {

    protected final Entity entity;
    protected State state;

    /**
     * Initialize the <code>AI</code>.
     *
     * @param entity the target <code>Entity</code>
     */
    protected AI(Entity entity) {
        this.entity = entity;
    }

    protected AI(AI ai) {
        this(ai.entity);
    }

    /**
     * Should be called every game update. Overrides of this method
     * contains all the logic to the <code>AI</code>.
     */
    public abstract void act();

    /**
     * Used to put the AI back into the initial phase,
     * including additional <code>AI</code>-specific variables.
     */
    public abstract void reset();

    /**
     * Should only be called when <code>this.state != State.RUNNING</code>. Used to initialize this <code>AI</code>
     * and, in overrides, children <code>AI</code>.
     */
    public void start() {
        state = State.RUNNING;
    }

    protected void fail() {
        state = State.FAIL;
    }

    protected void succeed() {
        state = State.SUCCESS;
    }

    // GETTERS

    public boolean isSuccess() {
        return state == State.SUCCESS;
    }

    public boolean isFailure() {
        return state == State.FAIL;
    }

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public State getState() {
        return state;
    }

    /**
     * Enum used to track the execution state of this AI.
     * An instance whose <code>State</code> is <code>State.RUNNING</code>
     * should continue executing on next calls to <code>AI.act()</code>.
     */
    public enum State {

        RUNNING,
        FAIL,
        SUCCESS
    }
}
