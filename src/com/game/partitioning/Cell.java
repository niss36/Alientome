package com.game.partitioning;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.buffs.Buff;
import com.game.entities.Entity;
import com.game.entities.EntityPlayer;
import com.util.AxisAlignedBB;
import com.util.Couple;

import java.util.ArrayList;

/**
 * Part of a <code>Tree</code>. Represents a single space partition in the <code>Level</code>.
 */
class Cell {

    private final int x;
    private final int y;

    ArrayList<GameObject> objects;
    private AxisAlignedBB boundingBox;

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
            boundingBox = new AxisAlignedBB(x * Block.width, y * Block.width, (x + 1) * Block.width, (y + 1) * Block.width);

        return boundingBox.intersects(object.getBoundingBox());
    }

    /**
     * Updates this <code>Cell</code>. At the moment, only used for collision or overlap detection
     * between contained objects.
     */
    void update() {
        assert hasObjects();

        ArrayList<Couple<Entity>> checked = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {
            GameObject object0 = objects.get(i);

            if (object0 instanceof Buff) continue;

            Entity entity0 = (Entity) object0;

            // Fix 'ghost' dead entity
            if (entity0.isDead()) {
                objects.remove(entity0);
                // Since an object was removed, next element is at index i (Instead of i + 1)
                i--;
                continue;
            }

            @SuppressWarnings("unchecked")
            ArrayList<GameObject> arrayList = (ArrayList<GameObject>) objects.clone();

            arrayList.remove(object0);

            for (GameObject object1 : arrayList) {

                if (object1 instanceof Entity) processEntityEntityCollision(entity0, (Entity) object1, checked);

                else if (object1 instanceof Buff) processEntityBuffCollision(entity0, (Buff) object1);
            }
        }

        Tree.updated++;
    }

    /**
     * Resolves the collisions between the given entities, and ensure that they will not be tested again.
     *
     * @param entity0 an <code>Entity</code>
     * @param entity1 an other <code>Entity</code>
     * @param checked a list of <code>Couple</code>s of entities which already have been tested.
     */
    private void processEntityEntityCollision(Entity entity0, Entity entity1, ArrayList<Couple<Entity>> checked) {

        //Player must be the first element of collisions to avoid bugs
        if (entity1 instanceof EntityPlayer) processEntityEntityCollision(entity1, entity0, checked);

        else {

            Couple<Entity> couple = new Couple<>(entity0, entity1);

            if (checked.contains(couple)) return;

            checked.add(couple);

            if (entity1.getNextBoundingBox()
                    .intersects(entity0.getNextBoundingBox()))

                entity1.onCollidedWithEntity(
                        entity0,
                        entity0.getNextBoundingBox()
                                .intersectionSideWith(entity1.getNextBoundingBox()));
        }
    }

    /**
     * Resolves the overlap between the given entity and the given buff.
     *
     * @param entity the <code>Entity</code> to check
     * @param buff   the <code>Buff</code> to check
     */
    private void processEntityBuffCollision(Entity entity, Buff buff) {

        if (entity.getNextBoundingBox().intersects(buff.getBoundingBox())) buff.entityEntered(entity);
    }
}
