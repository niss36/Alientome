package com.alientome.impl.ai;

import com.alientome.core.util.Direction;
import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;
import com.alientome.impl.entities.Marksman;

public class AIShoot extends AI {

    private final Marksman marksman;
    private final Entity target;

    /**
     * Initialize the <code>AI</code>.
     *
     * @param marksman the target <code>Marksman</code>
     */
    public AIShoot(Marksman marksman, Entity target) {
        super(marksman.getEntity());

        this.marksman = marksman;
        this.target = target;
    }

    @Override
    public void act() {

        if (target.getPos().x < entity.getPos().x) entity.move(Direction.LEFT, 0);
        else if (target.getPos().x > entity.getPos().x) entity.move(Direction.RIGHT, 0);

        marksman.startCharging();
    }

    @Override
    public void reset() {
    }
}
