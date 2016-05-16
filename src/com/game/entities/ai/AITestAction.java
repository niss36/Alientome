package com.game.entities.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AITestAction extends AI {

    private final List<AITest> aiTests = new ArrayList<>();
    private final List<AI> ais = new ArrayList<>();

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
        super(defaultAction.entity);

        if (tests.length != actions.length)
            throw new IllegalArgumentException("Tests and actions arrays do not have the same length.");

        aiTests.addAll(Arrays.asList(tests));
        ais.addAll(Arrays.asList(actions));

        this.defaultAction = defaultAction;
    }

    @Override
    public void act() {

        if (!current.isRunning()) current.start();
        current.act();

        for (int i = 0; i < aiTests.size(); i++) {

            AITest test = aiTests.get(i);
            if (test.result()) {
                if (currentIndex != i) {
                    current = ais.get(i);
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
    protected void reset() {
        current = defaultAction;
        current.start();
    }

    @Override
    public void start() {
        super.start();
        reset();
    }
}
