package com.game.entities.ai;

import com.game.entities.Entity;
import com.game.entities.EntityEnemyBow;
import com.util.Direction;

public class AIFire extends AI {

    private final Entity target;

    /**
     * Initialize the <code>AI</code>.
     *
     * @param entity the target <code>Entity</code>
     */
    public AIFire(EntityEnemyBow entity, Entity target) {
        super(entity);

        this.target = target;
    }

    @Override
    public void act() {

        if (target.getPos().x < entity.getPos().x) entity.move(Direction.LEFT, 0);
        else if (target.getPos().x > entity.getPos().x) entity.move(Direction.RIGHT, 0);

        ((EntityEnemyBow) entity).fire();
    }

    @Override
    protected void reset() {
    }
}
