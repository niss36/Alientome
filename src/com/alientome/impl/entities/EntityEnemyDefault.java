package com.alientome.impl.entities;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.collisions.Contact;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.abilities.AttackAbility;
import com.alientome.game.abilities.ChanneledAbility;
import com.alientome.game.abilities.SimpleAttackAbility;
import com.alientome.game.abilities.components.*;
import com.alientome.game.ai.AI;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityEnemy;
import com.alientome.game.entities.EntityPlayer;
import com.alientome.game.level.Level;
import com.alientome.game.util.EntityTags;
import com.alientome.game.util.Util;
import com.alientome.impl.ai.*;

import java.awt.*;

public class EntityEnemyDefault extends EntityEnemy {

    protected final ChanneledAbility attackAbility = createAttackAbility();
    protected int followRange;

    public EntityEnemyDefault(Vec2 pos, Level level, EntityTags tags) {

        super(pos, new Dimension(20, 27), level, tags, 10);

        maxVelocity = 3;
    }

    @Override
    protected AI createAI(EntityTags tags) {

        followRange = tags.getAsInt("followRange", 300);

        Entity player = level.getPlayer();

        AITest[] tests = {new AIEntityAbove(this, player, 10), new AIEntityAbove(this, player, 15), new AISeeEntity(this, player, followRange)};
        AI[] actions = {new AIFlee(this, player, 150), new AIIdle(this, -1), new AIFollow(this, player, false)};

        return new AIRepeat(
                new AITestAction(
                        tests,
                        actions,
                        new AIWander(this)
                )
        );
    }

    @Override
    protected boolean onCollidedWithPlayer(EntityPlayer player, Contact contact) {

        boolean ret = !super.onCollidedWithPlayer(player, contact);

        if (ret)
            attackAbility.startChannel();

        return ret;
    }

    @Override
    protected void preUpdateInternal() {

        attackAbility.update();
    }

    protected AttackAbility<EntityPlayer> createAttackAbility() {

        Attack<EntityPlayer> attack = new SimpleAttack<>(3f);
        Target<EntityPlayer> target = action -> {

            AxisAlignedBoundingBox nextBoundingBox = getNextBoundingBox();

            AxisAlignedBoundingBox area = Util.getNextFrontBoundingBox(this, 10, 5);

            AxisAlignedBoundingBox playerNextBoundingBox = level.getPlayer().getNextBoundingBox();
            if (playerNextBoundingBox.intersects(area))
                action.accept(level.getPlayer());
            else {
                AxisAlignedBoundingBox areaUnder = new StaticBoundingBox(nextBoundingBox.getMinX() - 5, nextBoundingBox.getMaxY(), nextBoundingBox.getMaxX() + 5, nextBoundingBox.getMaxY() + 5);
                if (playerNextBoundingBox.intersects(areaUnder))
                    action.accept(level.getPlayer());
            }
        };
        AttackVisuals visuals = new SimpleAttackVisuals(handler, 1, 0);
        return new SimpleAttackAbility<>(this, 15, 7, 5, target, attack, visuals);
    }

    @Override
    public String getNameKey() {
        return "entities.enemyDefault.name";
    }
}
