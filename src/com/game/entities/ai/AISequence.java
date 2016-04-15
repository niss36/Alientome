package com.game.entities.ai;

import java.util.*;

/**
 * <code>AI</code> to execute a <code>List</code> of <code>AI</code>s.
 */
public class AISequence extends AI {

    private final ArrayList<AI> ais = new ArrayList<>();
    private final Queue<AI> aiQueue = new LinkedList<>();
    private AI currentAI;

    /**
     * @param ais the <code>AI</code>s to execute
     */
    public AISequence(AI... ais) {
        super(ais[0].entity);

        this.ais.addAll(Arrays.asList(ais));
    }

    /**
     * @param ais the <code>AI</code>s to execute
     */
    public AISequence(List<AI> ais) {
        super(ais.get(0).entity);

        this.ais.addAll(ais);
    }

    @Override
    public void reset() {
        ais.forEach(AI::reset);
    }

    @Override
    public void start() {
        super.start();

        aiQueue.clear();
        aiQueue.addAll(ais);
        currentAI = aiQueue.poll();
        currentAI.start();
    }

    @Override
    public void act() {

        currentAI.act();

        if (!currentAI.isRunning()) {
            if (currentAI.isFailure())
                fail();
            else if (aiQueue.peek() == null) state = currentAI.getState();
            else if (aiQueue.peek() == null) state = currentAI.getState();
            else {
                currentAI = aiQueue.poll();
                currentAI.start();
            }
        }
    }
}
