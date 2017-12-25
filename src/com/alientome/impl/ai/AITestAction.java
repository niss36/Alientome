package com.alientome.impl.ai;

import com.alientome.game.ai.AI;

import java.util.Arrays;
import java.util.List;

public class AITestAction extends AI {

    private final List<AITest> tests;
    private final List<AI> actions;

    private final AI defaultAction;

    private AI current;
    private int currentIndex;

    /**
     * @param tests         the array of <code>AITest</code>s to be executed
     * @param actions       the array of <code>AI</code>s to correspond to each <code>AITest</code>
     * @param defaultAction the default <code>AI</code>
     * @throws IllegalArgumentException if the two arrays do not have the same length.
     */
    public AITestAction(AITest[] tests, AI[] actions, AI defaultAction) {
        super(defaultAction);

        if (tests.length != actions.length)
            throw new IllegalArgumentException("Tests and actions arrays do not have the same length");

        this.tests = Arrays.asList(tests);
        this.actions = Arrays.asList(actions);

        this.defaultAction = defaultAction;
    }

    @Override
    public void act() {

        if (!current.isRunning()) current.start();
        current.act();

        for (int i = 0; i < tests.size(); i++) {

            AITest test = tests.get(i);
            if (test.result()) {
                if (currentIndex != i) {
                    current = actions.get(i);
                    current.start();
                    currentIndex = i;
                }
                return;
            }
        }

        if (current != defaultAction) current = defaultAction;

        currentIndex = -1;
    }

    @Override
    public void reset() {
        current = defaultAction;
        current.start();
    }

    @Override
    public void start() {
        super.start();
        reset();
    }
}
