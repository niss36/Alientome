package com.alientome.impl.entities;

import com.alientome.core.util.Vec2;
import com.alientome.game.ai.AI;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityEnemy;
import com.alientome.game.level.Level;
import com.alientome.game.util.EntityTags;
import com.alientome.impl.ai.*;

import java.awt.*;

public class EntityEnemyBow extends EntityEnemy implements Marksman {

    private int shootCooldown = 0;
    private int shootState = -1;

    public EntityEnemyBow(Vec2 pos, Level level, EntityTags tags) {

        super(pos, new Dimension(19, 27), level, tags, 10);

        maxVelocity = 4;
    }

    @Override
    protected void preUpdateInternal() {

        if (shootCooldown > 0) shootCooldown--;

        if (shootState >= 0) {
            if (shootState >= 17) stopCharging();
            else {
                handler.setAnimationUsed(1);
                shootState++;
            }
        }

        super.preUpdateInternal();
    }

    @Override
    public void startCharging() {

        if (shootCooldown == 0 && shootState < 0) shootState = 0;
    }

    @Override
    public void stopCharging() {

        if (shootState >= 0) {

            shoot();
            handler.setAnimationEndListener(() -> handler.setAnimationUsed(0));
            shootState = -1;
        }
    }

    @Override
    public void shoot() {

        if (shootCooldown == 0) {

            float chargeRatio = shootState / 17f;

            double verticalMotion = chargeRatio * (-level.getPlayer().distanceTo(this) + level.getRandom().nextInt(50) - 25) / 25;

            level.spawnEntity(new EntityArrow(this, chargeRatio * 3, verticalMotion));
            shootCooldown = 33;
        }
    }

    @Override
    protected AI createAI(EntityTags tags) {

        Entity player = level.getPlayer();

        int fleeRange = tags.getAsInt("fleeRange", 150);
        int shootRange = tags.getAsInt("shootRange", 400);

        AITest[] tests = {new AISeeEntity(this, player, fleeRange), new AISeeEntity(this, player, shootRange)};
        AI[] actions = {new AIFlee(this, player, fleeRange), new AIShoot(this, player)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    @Override
    public String getNameKey() {
        return "entities.enemyBow.name";
    }
}
