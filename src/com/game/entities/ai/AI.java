package com.game.entities.ai;

import com.game.entities.Entity;

import java.util.Random;

public abstract class AI {

    public enum State {

        RUNNING,
        FAIL,
        SUCCESS
    }

    protected Random aIRandom = new Random();

    Entity entity;

    State state;

    public AI(Entity entity) {

        this.entity = entity;
    }

    public abstract void act();

    public abstract void reset();

    public void start() {
        state = State.RUNNING;
    }

    protected void fail() {
        state = State.FAIL;
    }

    protected void succeed() {
        state = State.SUCCESS;
    }

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
}
