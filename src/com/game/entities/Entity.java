package com.game.entities;

import com.game.GameObject;
import com.game.blocks.Block;
import com.game.level.Level;
import com.game.level.LevelMap;
import com.util.*;
import com.util.visual.AnimationsHandler;

import java.awt.*;
import java.util.Random;

import static com.util.Util.clamp;
import static com.util.Util.decrease;

/**
 * The <code>Entity</code> class is used to represent non-<code>Block</code> objects in the <code>Level</code>.
 */
public abstract class Entity extends GameObject implements EntityConstants {

    private static final Vec2 gravity = new Vec2(0, 1);
    public final Dimension dim;
    public final Level level;
    final Random entityRandom = new Random();
    final AnimationsHandler handler;
    final Vec2 velocity = new Vec2(0, 0);
    private final Vec2 acceleration = new Vec2(0, 0);
    public boolean collidedX;
    public boolean collidedY;
    public GameObject lastCollidedWith;
    boolean onGround;
    double maxVelocity = 5d;
    Block blockIn;
    Direction facing = Direction.RIGHT;
    private Vec2 prevPos;
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

        this.dim = dim;
        this.level = level;

        actualizeBoundingBox();

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

        double x0 = x * Block.width + Block.width / 2 - entity.dim.width / 2;
        double y0 = y * Block.width + Block.width - entity.dim.height;

        entity.pos.set(x0, y0);

        return entity;
    }

    /**
     * Called every game update. Used to update the <code>Entity</code>'s position and logic.
     * Overrides should always call <code>super.onUpdate()</code>.
     */
    public void onUpdate() {

        if (!dead) {

            if (prevPos == null) prevPos = new Vec2(pos);

            // 1: Apply external forces.
            velocity.add(acceleration); //Gathered from method move
            acceleration.set(0, 0);

            decrease(velocity, 0.5d); // Pseudo-friction

            velocity.x = clamp(velocity.x, -maxVelocity, maxVelocity); //Ensure entities don't move too fast due to continuous acceleration

            if (isAffectedByGravity() && !onGround) velocity.add(gravity); // Gravity

            // 2: Resolve collisions
            if (canBeCollidedWith()) {

                collidedY = false;
                collidedX = false;

                actualizeBoundingBox();
                blockIn = blockIn();

                // Test all blocks in a 3*3 square around blockIn for collisions
                for (int i = -1; i < 2; i++) {

                    for (int j = -1; j < 2; j++) {

                        Block b = LevelMap.getInstance().getBlock(blockIn.blockX + i, blockIn.blockY + j);

                        CollisionPoint cp = b.getBoundingBox().processCollision(getNextBoundingBox());

                        if (onCollidedWithBlock(b, cp)) {

                            actualizeBoundingBox();
                            blockIn = blockIn();
                        }
                    }
                }

                if (isAffectedByGravity()) {
                    onGround = false;

                    for (int i = 0; i < 2; i++) {

                        Block b = LevelMap.getInstance().getBlock((int) (pos.x / Block.width) + i, (int) (pos.y + dim.height) / Block.width);

                        onGround = b.isOpaque() && b.getBoundingBox().intersects(boundingBox.offset(0, 1)) || onGround;
                    }
                }

            }

            // 3: Integrate velocity into position
            pos.add(velocity);

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
        return LevelMap.getInstance().getBlockAbsCoordinates(pos.x + dim.width / 2, pos.y + dim.height / 2);
    }

    @Override
    protected AxisAlignedBB boundingBox() {
        return new AxisAlignedBB(pos, dim.width, dim.height);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    protected void draw(Graphics g, int x, int y) {
        handler.draw(g, x, y, facing);
    }

    @Override
    protected void drawDebug(Graphics g, Point origin) {

        g.setColor(Color.red);

        boundingBox.draw(g, origin);
    }

    @Override
    protected boolean drawCondition() {
        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(Graphics g, Point origin) {

        g.setColor(Color.black);

        boundingBox.fill(g, origin);
    }

    boolean isAffectedByGravity() {
        return true;
    }

    /**
     * Set this <code>Entity</code> dead. It will be deleted next call to <code>onUpdate()</code>.
     */
    final void setDead() {
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

        assert canBeCollidedWith();

        if (collisionPoint != null && block.canBeCollidedWith()) {

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

        if (canBeCollidedWith() && other.canBeCollidedWith() && side != null && !(other instanceof EntityProjectile)) {
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
     * @see #onCollidedWithEntity(Entity, Side)
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
                acceleration.x += value;
                facing = direction;
                break;
            case LEFT:
                acceleration.x -= value;
                facing = direction;
                break;
        }
    }

    /**
     * Makes this <code>Entity</code> jump if it is on the ground.
     */
    public void jump() {
        if (onGround) velocity.y = -18;
    }

    /**
     * @param other the <code>Entity</code> to get the distance to
     * @return the euclidean distance from this to the other <code>Entity</code>
     */
    public final double distanceTo(Entity other) {
        return getPos().distance(other.getPos());
    }

    /**
     * @param other the <code>Entity</code> to get the distance to
     * @return the squared euclidean distance from this <code>Entity</code> to the other <code>Entity</code>
     */
    public final double distanceSqTo(Entity other) {
        return getPos().distanceSq(other.getPos());
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
