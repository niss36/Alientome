package com.game.entities;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.level.Level;
import com.game.level.LevelMap;
import com.util.*;
import com.util.visual.AnimationsHandler;

import java.awt.*;
import java.util.Random;

/**
 * The <code>Entity</code> class is used to represent non-<code>Block</code> objects in the <code>Level</code>.
 */
public abstract class Entity extends GameObject implements EntityConstants {

    private static int blockWidth;
    public final Dimension dim;
    public final Level level;
    final Random entityRandom = new Random();
    final AnimationsHandler handler;
    final Vec2 velocity = new Vec2(0, 0);
    public boolean collidedX;
    public boolean collidedY;
    public GameObject lastCollidedWith;
    boolean onGround;
    Vec2 acceleration = new Vec2(0, 0);
    double maxVelocity = 5d;
    Block blockIn;
    boolean gravity = true;
    Direction facing = Direction.RIGHT;
    private Vec2 prevPos;
    private boolean collide = true;
    private boolean dead = false;

    /**
     * Initialize the <code>Entity</code>.
     *
     * @param x     the x coordinate.
     * @param y     the y coordinate.
     * @param dim   the <code>Dimension</code> of this <code>Entity</code> (width, height).
     * @param level the <code>Level</code> this <code>Entity</code> is in.
     */
    Entity(double x, double y, Dimension dim, Level level) {

        super(x, y);

        this.dim = dim;/*
        this.dim.width *= 2;
        this.dim.height *= 2;*/
        this.level = level;

        actualizeBoundingBox();

        blockWidth = Block.width;

        handler = new AnimationsHandler(getClass());
    }

    /**
     * Creates an <code>Entity</code> using given arguments.
     *
     * @param type  the type of <code>Entity</code> (A value from a static field from <code>EntityConstants</code>).
     * @param x     the x coordinate of the <code>Block</code> this <code>Entity</code> is in.
     * @param y     the y coordinate of the <code>Block</code> this <code>Entity</code> is in.
     * @param level the <code>Level</code> this <code>Entity</code> is in.
     * @return a new <code>Entity</code>
     */
    public static Entity createFromBlockPos(int type, int x, int y, Level level) {

        Entity entity;

        //Initialize coordinates at 0 in order to center the Entity
        switch (type) {
            case PLAYER:
                entity = new EntityPlayer(0, 0, level);
                break;

            case ENEMY:
                entity = new EntityEnemy(0, 0, level, 300);
                break;

            case ENEMY_SHIELD:
                entity = new EntityEnemyShield(0, 0, level, 300);
                break;

            case ENEMY_BOW:
                entity = new EntityEnemyBow(0, 0, level);
                break;

            case ENEMY_WIZARD:
                entity = new EntityEnemyWizard(0, 0, level, 300);
                break;

            default:
                return null;
        }

        double x0 = x * blockWidth + blockWidth / 2 - entity.dim.width / 2;
        double y0 = y * blockWidth + blockWidth - entity.dim.height;

        entity.pos.set(x0, y0);

        return entity;
    }

    /**
     * Private method used to decrease motion.
     *
     * @param d     the number to decrease.
     * @param value the amount to decrease.
     * @return If <code>d==0</code> 0 else <code>d</code> closer to 0 by <code>value</code>.
     */
    private static double decrease(double d, double value) {
        return d < 0 ? d + value : d > 0 ? d - value : 0;
    }

    private static void decrease(Vec2 vec, double value) {

        vec.x = decrease(vec.x, value);
        vec.y = decrease(vec.y, value);
    }

    /**
     * Called every game update. Used to update the <code>Entity</code>'s position and logic.
     * Overrides should always call <code>super.onUpdate()</code>.
     */
    public void onUpdate() {

        if (!dead) {

            if (prevPos == null) prevPos = new Vec2(pos);

            if (collide) {

                collidedY = false;
                collidedX = false;

                actualizeBoundingBox();
                blockIn = blockIn();

                // Test all blocks in a 3*3 square around blockIn for collisions
                for (int i = -1; i < 2; i++) {

                    for (int j = -1; j < 2; j++) {

                        Block b = LevelMap.getInstance().getBlock(blockIn.blockX + i, blockIn.blockY + j, true);

                        CollisionPoint cp = b.getBoundingBox().processCollision(getNextBoundingBox());

                        if (onCollidedWithBlock(b, cp)) {

                            actualizeBoundingBox();
                            blockIn = blockIn();
                        }
                    }
                }

                if (gravity) {
                    onGround = false;

                    for (int i = 0; i < 2; i++) {

                        Block b = LevelMap.getInstance().getBlock((int) (pos.x / blockWidth) + i, (int) (pos.y + dim.height) / blockWidth, true);

                        onGround = b.isOpaque() && b.getBoundingBox().intersects(boundingBox.offset(0, 1)) || onGround;
                    }
                }

                if (!collidedX) pos.x += velocity.x;
                if (!collidedY) pos.y += velocity.y;

            } else {

                pos.add(velocity);
            }

            if (gravity && !onGround) move(Direction.DOWN);

            decrease(velocity, 0.5d);

            blockIn = blockIn();
            actualizeBoundingBox();

            if (!prevPos.equals(pos)) level.move(prevPos, this);

            prevPos = new Vec2(pos);

        } else onDeath();
    }

    /**
     * @return the current <code>Block</code> this <code>Entity</code> is in.
     */
    private Block blockIn() {

        return LevelMap.getInstance().getBlock((int) ((pos.x + dim.width / 2) / blockWidth), (int) ((pos.y + dim.height / 2) / blockWidth), true);
    }

    @Override
    protected AxisAlignedBB boundingBox() {

        return new AxisAlignedBB(pos.x, pos.y, pos.x + dim.width, pos.y + dim.height);
    }

    protected void draw(Graphics g, int x, int y) {

        handler.draw(g, x, y, facing);
    }

    @Override
    protected void drawDebug(Graphics g, Point min) {

        g.setColor(Color.red);

        boundingBox.draw(g, min);
    }

    @Override
    protected boolean drawCondition() {
        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(Graphics g, Point min) {

        g.setColor(Color.black);

        boundingBox.fill(g, min);
    }

    /**
     * Set this <code>Entity</code> dead. It will be deleted next call to <code>onUpdate()</code>.
     */
    void setDead() {
        dead = true;
    }

    /**
     * Called when this <code>Entity</code> is updated after being set dead.
     */
    void onDeath() {
        level.removeEntity(this);
    }

    /**
     * Called when this <code>Entity</code> collides with a <code>Block</code>.
     *
     * @param block          the <code>Block</code> collided with
     * @param collisionPoint info relative to the collision
     * @return whether the collision was processed
     */
    boolean onCollidedWithBlock(Block block, CollisionPoint collisionPoint) {

        if (collisionPoint != null && block.isOpaque()) {

            switch (collisionPoint.getCollisionSide()) {

                case TOP:
                    pos.y = collisionPoint.getY() - dim.height;
                    velocity.y = 0;
                    collidedY = true;
                    break;

                case BOTTOM:
                    pos.y = collisionPoint.getY();
                    velocity.y = 0;
                    collidedY = true;
                    break;

                case LEFT:
                    pos.x = collisionPoint.getX() - dim.width;
                    velocity.x = 0;
                    collidedX = true;
                    break;

                case RIGHT:
                    pos.x = collisionPoint.getX();
                    velocity.x = 0;
                    collidedX = true;
                    break;
            }

            lastCollidedWith = block;

            return true;
        }

        return false;
    }

    /**
     * Called when this <code>Entity</code> collides with another <code>Entity</code>
     *
     * @param other the <code>Entity</code> collided with
     * @param side  the <code>Side</code> of the collision
     * @return whether the collision was processed
     */
    public boolean onCollidedWithEntity(Entity other, Side side) {

        if (collide && other.collide && side != null && !(other instanceof EntityProjectile)) {
            //side is relative to other entity
            switch (side) {
                case TOP: //On the top of the other entity
                    pos.y = other.pos.y - dim.height;
                    velocity.y = other.velocity.y = /*Util.combineVelocities(velocity.y, other.velocity.y)*/0;
                    collidedY = true;
                    break;
                case BOTTOM: //On the bottom of the other entity
                    pos.y = other.pos.y + other.dim.height;
                    if (onGround) velocity.y = 0;
                    else velocity.y = other.velocity.y = /*Util.combineVelocities(velocity.y, other.velocity.y)*/0;
                    other.onGround = onGround;
                    collidedY = true;
                    break;
                case LEFT: //To the left of the other entity
                    pos.x = other.pos.x - dim.width;
                    velocity.x = other.velocity.x = /*Util.combineVelocities(velocity.x, other.velocity.x)*/0;
                    collidedX = true;
                    break;
                case RIGHT: //To the right of the other entity
                    pos.x = other.pos.x + other.dim.width;
                    velocity.x = other.velocity.x = /*Util.combineVelocities(velocity.x, other.velocity.x)*/0;
                    collidedX = true;
                    break;
            }

            lastCollidedWith = other;

            return true;
        }

        return false;
    }

    /**
     * Lets this <code>Entity</code> execute its specific code when an other <code>Entity</code> collides with it.
     *
     * @param other the <code>Entity</code> collided with
     * @param side  the <code>Side</code> of the collision
     * @see this.onCollidedWithEntity
     */
    protected abstract void notifyCollision(Entity other, Side side);

    /**
     * @param direction the <code>Direction</code> to move towards
     */
    public void move(Direction direction) {
        move(direction, 1);
    }

    /**
     * Adds or subtracts the specified value to <code>this.motionX</code> or
     * <code>this.motionY</code> according to the <code>Direction</code>
     *
     * @param direction the <code>Direction</code> to move towards
     * @param value     the value to add to velocity
     */
    public void move(Direction direction, double value) {

        switch (direction) {
            case UP:
                jump();
                break;
            case DOWN:
                if (!onGround) velocity.y += value;
                break;
            case RIGHT:
                velocity.x += value;
                facing = direction;
                break;
            case LEFT:
                velocity.x -= value;
                facing = direction;
                break;
        }

        velocity.x = velocity.x < -maxVelocity ? -maxVelocity : velocity.x > maxVelocity ? maxVelocity : velocity.x;
    }

    /**
     * Makes this <code>Entity</code> jump if it is on the ground.
     */
    public void jump() {
        if (onGround) velocity.y = -18;
    }

    /**
     * Abstract method to create the info necessary to animations loading.
     *
     * @return an <code>AnimationInfo</code> array containing one element per distinct animation
     * for this <code>Entity</code>
     */
//    protected abstract AnimationInfo[] createAnimationInfo();

    /**
     * @param other the <code>Entity</code> to get the distance to
     * @return the euclidean distance from this to the other <code>Entity</code>
     */
    public final double distanceTo(Entity other) {
        return getPosVec().distance(other.getPosVec());
    }

    /**
     * @return this <code>Entity</code>'s bounding box as it will be in the next game update
     */
    public final AxisAlignedBB getNextBoundingBox() {
        return boundingBox.offset(velocity);
    }

    //GETTERS

    public final Vec2 getVelocity() {
        return velocity;
    }

    public final boolean isDead() {
        return dead;
    }
}
