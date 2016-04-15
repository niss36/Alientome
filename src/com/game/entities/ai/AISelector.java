package com.game.entities.ai;

import java.util.*;

public class AISelector extends AI {

    private final ArrayList<AI> ais = new ArrayList<>();
    private final Queue<AI> aiQueue = new LinkedList<>();
    private AI currentAI;

    /**
     * @param ais the <code>AI</code>s to execute
     */
    public AISelector(AI... ais) {
        super(ais[0].entity);

        this.ais.addAll(Arrays.asList(ais));
    }

    /**
     * @param ais the <code>AI</code>s to execute
     */
    public AISelector(List<AI> ais) {
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

        if (currentAI.isRunning()) return;

        if (currentAI.isSuccess()) {
            succeed();
            return;
        }

        if (aiQueue.peek() == null) state = currentAI.getState();

        else {
            currentAI = aiQueue.poll();
            currentAI.start();
        }
    }
}
