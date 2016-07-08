package com.game.entities.ai;

import com.game.entities.Entity;

import java.util.Random;

/**
 * <code>AI</code> is used to simulate intelligent behavior for non-player entities.
 */
public abstract class AI {

    final static Random aIRandom = new Random();
    final Entity entity;
    State state;

    /**
     * Initialize the <code>AI</code>.
     *
     * @param entity the target <code>Entity</code>
     */
    AI(Entity entity) {

        this.entity = entity;
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
    protected abstract void reset();

    /**
     * Should only be called when <code>this.state != State.RUNNING</code>. Used to initialize this <code>AI</code>
     * and, in overrides, children <code>AI</code>.
     */
    public void start() {
        state = State.RUNNING;
    }

    void fail() {
        state = State.FAIL;
    }

    // GETTERS AND SETTERS

    void succeed() {
        state = State.SUCCESS;
    }

    boolean isSuccess() {
        return state == State.SUCCESS;
    }

    boolean isFailure() {
        return state == State.FAIL;
    }

    boolean isRunning() {
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
