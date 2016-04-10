package com.game.entities.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class AISequence extends AI {

    private AI currentAI;
    ArrayList<AI> ais = new ArrayList<>();
    Queue<AI> aiQueue = new LinkedList<>();

    public AISequence(AI... ais) {
        super(ais[0].entity);

        this.ais.addAll(Arrays.asList(ais));
    }

    @Override
    public void reset() {
        for (AI ai : ais) ai.reset();
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

        if(!currentAI.isRunning()) {
            if(aiQueue.peek() == null) state = currentAI.getState();
            else if(aiQueue.peek() == null) state = currentAI.getState();
            else {
                currentAI = aiQueue.poll();
                currentAI.start();
            }
        }
    }
}
