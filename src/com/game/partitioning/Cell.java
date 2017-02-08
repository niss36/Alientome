package com.game.partitioning;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.buffs.Buff;
import com.game.entities.Entity;
import com.util.collisions.AxisAlignedBoundingBox;
import com.util.collisions.Contact;
import com.util.collisions.StaticBoundingBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of a <code>Tree</code>. Represents a single space partition in the <code>Level</code>.
 */
class Cell {

    private final int x;
    private final int y;

    List<GameObject> objects;
    private AxisAlignedBoundingBox boundingBox;

    /**
     * Constructs the <code>Cell</code>.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    Cell(int x, int y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Adds the given object to this <code>Cell</code>.
     *
     * @param object the <code>GameObject</code> to add.
     */
    public void add(GameObject object) {

        if (objects == null) objects = new ArrayList<>();

        objects.add(object);
    }

    /**
     * Removes the given object from this <code>Cell</code>.
     *
     * @param object the <code>GameObject</code> to remove.
     */
    void remove(GameObject object) {

        if (objects != null) {
            objects.remove(object);
            if (objects.size() == 0) objects = null;
        }
    }

    /**
     * Empties this <code>Cell</code>.
     */
    void empty() {

        objects = null;
    }

    /**
     * @return whether this <code>Cell</code> contains objects
     */
    boolean hasObjects() {

        return objects != null && !objects.isEmpty();
    }

    /**
     * Tests whether the given object is at least partially contained in this <code>Cell</code>.
     *
     * @param object the <code>GameObject</code> to test
     * @return whether the object overlaps this <code>Cell</code>
     */
    boolean canContain(GameObject object) {

        if (boundingBox == null)
            boundingBox = new StaticBoundingBox(x * Block.WIDTH, y * Block.WIDTH, (x + 1) * Block.WIDTH, (y + 1) * Block.WIDTH);

        return boundingBox.intersects(object.getBoundingBox());
    }

    /**
     * Updates this <code>Cell</code>. At the moment, only used for collision or overlap detection
     * between contained objects.
     */
    void update() {
        assert hasObjects();

        for (int i = 0; i < objects.size(); i++) {

            GameObject objectI = objects.get(i);

            for (int j = i + 1; j < objects.size(); j++) {

                GameObject objectJ = objects.get(j);

                processCollision(objectI, objectJ);
            }
        }
    }

    private void processCollision(GameObject object0, GameObject object1) {

        if (object0 instanceof Entity)
            if (object1 instanceof Entity)
                processCollision((Entity) object0, (Entity) object1);
            else //Object1 is a Buff
                processCollision((Entity) object0, (Buff) object1);
        else //Object0 is a Buff
            if (object1 instanceof Entity)
                processCollision((Entity) object1, (Buff) object0);
        //No-op on Buff/Buff
    }

    private void processCollision(Entity entity0, Entity entity1) {

        if (entity0.canBeCollidedWith() && entity1.canBeCollidedWith()) {

            Contact contact = entity0.getNextBoundingBox().processCollisionWith(entity1.getNextBoundingBox(), entity0.getVelocity(), entity1.getVelocity());

            if (contact != null) entity1.onCollidedWithEntity(entity0, contact);
        }
    }

    private void processCollision(Entity entity, Buff buff) {

        if (entity.getNextBoundingBox().intersects(buff.getBoundingBox()))
            buff.onEntityEntered(entity);
    }
}
