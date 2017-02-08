package com.game.entities;

import com.events.GameEventDispatcher;
import com.events.GameEventType;
import com.game.camera.DefaultCamera;
import com.game.GameObject;
import com.game.blocks.Block;
import com.game.command.CommandSender;
import com.game.control.Control;
import com.game.control.Controller;
import com.game.level.Level;
import com.util.Direction;
import com.util.Vec2;
import com.util.collisions.AxisAlignedBoundingBox;
import com.util.collisions.Contact;
import com.util.collisions.DynamicBoundingBox;
import com.util.visual.AnimationsHandler;
import com.util.visual.GameGraphics;

import java.awt.*;
import java.util.Random;

import static com.util.Util.*;
import static com.util.profile.ExecutionTimeProfiler.theProfiler;

/**
 * The <code>Entity</code> class is used to represent non-<code>Block</code> objects in the <code>Level</code>.
 */
public abstract class Entity extends GameObject implements EntityConstants, CommandSender {

    private static final Vec2 gravity = new Vec2(0, 1);
    public final Dimension dimension;
    public final Level level;
    final Random entityRandom = new Random();
    final AnimationsHandler handler;
    final Vec2 velocity = new Vec2();
    final Vec2 acceleration = new Vec2();
    public boolean collidedX;
    public boolean collidedY;
    public GameObject lastCollidedWith;
    boolean affectedByGravity = true;
    boolean onGround;
    double maxVelocity = 7d;
    Block blockIn;
    Direction facing = Direction.RIGHT;
    Controller controller;
    private Vec2 prevPos;
    private boolean dead = false;
    private boolean wasDead = false;

    /**
     * Initialize the <code>Entity</code>.
     *
     * @param pos this <code>Entity</code>'s position
     * @param dimension the <code>Dimension</code> of this <code>Entity</code> (width, height).
     * @param level     the <code>Level</code> this <code>Entity</code> is in.
     */
    Entity(Vec2 pos, Dimension dimension, Level level) {

        super(pos);

        this.dimension = dimension;
        dimension.width *= 2;
        dimension.height *= 2;
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
        Vec2 pos = new Vec2();

        //Initialize coordinates at 0 in order to center the Entity
        switch (type) {
            case PLAYER:
                entity = new EntityPlayer(pos, level);
                break;

            case ENEMY:
                entity = new EntityEnemy(pos, level, 300);
                break;

            case ENEMY_SHIELD:
                entity = new EntityEnemyShield(pos, level, 300);
                break;

            case ENEMY_BOW:
                entity = new EntityEnemyBow(pos, level);
                break;

            case ENEMY_WIZARD:
                entity = new EntityEnemyWizard(pos, level, 300);
                break;

            case DUMMY:
                entity = new EntityDummy(pos, level);
                break;

            default:
                throw new IllegalArgumentException("Unknown type : " + type);
        }

        pos.x = x * Block.WIDTH + Block.WIDTH / 2 - entity.dimension.width / 2;
        pos.y = y * Block.WIDTH + Block.WIDTH - entity.dimension.height;

        return entity;
    }

    public final void preUpdate() {

        theProfiler.startSection("Entity Update");

        if (dead) {
            onDeath();
            return;
        }

        if (controller != null)
            controller.update();

        preUpdateInternal();

        if (prevPos == null) prevPos = new Vec2(pos);

        // 1: Apply external forces.
        velocity.add(acceleration); //Gathered from method move
        acceleration.set(0, 0);

        decrease(velocity, 0.5d); // Pseudo-friction

        velocity.x = clamp(velocity.x, -maxVelocity, maxVelocity); //Ensure entities don't move too fast due to continuous acceleration

        if (affectedByGravity) velocity.add(gravity); // Gravity
    }

    void preUpdateInternal() {
        // NO-OP on base class
    }

    public final void doBlockCollisions() {

        // 2: Resolve collisions
        if (canBeCollidedWith()) {

            collidedY = false;
            collidedX = false;

            actualizeBlockIn();

            theProfiler.startSection("Entity Update/Block Collisions");
            // Test all blocks in a 3*3 square around blockIn for collisions
            for (int i = -1; i < 2; i++) {

                for (int j = -1; j < 2; j++) {

                    Block block = level.map.getBlock(blockIn.blockX + i, blockIn.blockY + j);

                    if (!block.canBeCollidedWith()) continue;
                    Contact contact = block.getBoundingBox().processCollisionWith(getNextBoundingBox(), /*velocity*/null);

                    if (onCollidedWithBlock(block, contact)) actualizeBlockIn();
                }
            }
            theProfiler.endSection("Entity Update/Block Collisions");

            // 3: Set the onGround flag
            if (affectedByGravity) {
                theProfiler.startSection("Entity Update/Ground Detection");
                onGround = false;

                for (int i = 0; i < 2; i++) {

                    Block block = level.map.getBlock((int) (pos.x / Block.WIDTH) + i, (int) (pos.y + dimension.height) / Block.WIDTH);

                    // Bounding box 1 pixel under the (predicted) next position's bounding box
                    AxisAlignedBoundingBox offsetBoundingBox = boundingBox.offset(velocity.x, velocity.y + 1);

                    onGround = block.isOpaque() && block.getBoundingBox().intersects(offsetBoundingBox) || onGround;
                }
                theProfiler.endSection("Entity Update/Ground Detection");
            }
        }
    }

    public final void postUpdate() {

        // 4: Integrate velocity into position
        pos.add(velocity);

        actualizeBlockIn();

        if (!pos.equals(prevPos)) level.move(prevPos, this);

        prevPos = new Vec2(pos);

        postUpdateInternal();

        if (dead) onDeath();

        theProfiler.endSection("Entity Update");
    }

    void postUpdateInternal() {
        // NO-OP on base class
    }

    private void actualizeBlockIn() {
        blockIn = level.map.getBlockAbsCoordinates(pos.x + dimension.width / 2, pos.y + dimension.height / 2);
    }

    @Override
    protected AxisAlignedBoundingBox boundingBox() {
        return new DynamicBoundingBox(pos, dimension.width, dimension.height);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !dead;
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
        handler.draw(g, x, y, facing);
    }

    @Override
    protected void drawDebug(GameGraphics g) {

        g.graphics.setColor(Color.red);

        boundingBox.draw(g);
    }

    @Override
    protected boolean drawCondition() {
        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(GameGraphics g) {
        boundingBox.fill(g);
    }

    @Override
    public int getInterpolatedX(double partialTick) {
        return (int) (pos.x + velocity.x * partialTick);
    }

    @Override
    public int getInterpolatedY(double partialTick) {
        return (int) (pos.y + velocity.y * partialTick);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public void addChatMessage(String message) {
        GameEventDispatcher.getInstance().submit(message, GameEventType.MESSAGE_SENT);
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
        if (!wasDead) {
            level.removeEntity(this);
            velocity.set(0, 0);
            if (controller != null) controller.notifyControlledDeath();
            wasDead = true;
        }
    }

    /**
     * Called when this <code>Entity</code> collides with a <code>Block</code>.
     *
     * @param block   the <code>Block</code> collided with
     * @param contact info relative to the collision
     * @return whether the collision was processed
     */
    boolean onCollidedWithBlock(Block block, Contact contact) {

        assert canBeCollidedWith() && block.canBeCollidedWith();

        if (contact != null) {

            resolveCollision(block, contact);

            return true;
        }

        return false;
    }

    /**
     * Called when this <code>Entity</code> collides with another <code>Entity</code>
     *
     * @param other   the <code>Entity</code> collided with
     * @param contact the <code>Contact</code> for the intersection that was detected, or null if none
     * @return whether any collision happened and was resolved
     */
    public boolean onCollidedWithEntity(Entity other, Contact contact) {

        assert canBeCollidedWith() && other.canBeCollidedWith() && contact != null;

        if (!(other instanceof EntityProjectile)) {

            resolveCollision(other, contact);

            if (contact.normal.y > 0) other.onGround = onGround;
            else if (contact.normal.y < 0) onGround = other.onGround;

            notifyCollision(other, contact);
            other.notifyCollision(this, contact.negate());

            return true;
        }

        return false;
    }

    private void resolveCollision(GameObject colliding, Contact contact) {

        assert canBeCollidedWith() && colliding.canBeCollidedWith() && contact != null && contact.depth > 0;

        boolean isCollidingEntity = colliding instanceof Entity;

        int thisRatioWeight = 1; // Entities weight 1 in the velocity repartition
        int otherRatioWeight = isCollidingEntity ? 1 : 0; // Blocks weight 0
        int totalRatioWeight = thisRatioWeight + otherRatioWeight;

        double weightedDepth = roundClosest(contact.depth / totalRatioWeight, 0.5);

        Vec2 penetrationVec = contact.normal.multiplyImmutable(weightedDepth);

        velocity.add(penetrationVec);
        if (isCollidingEntity) ((Entity) colliding).velocity.add(penetrationVec.negate());

        if (contact.normal.x != 0)
            collidedX = true;
        else // y is assumed to be non-zero
            collidedY = true;

        lastCollidedWith = colliding;
    }

    /**
     * Lets this <code>Entity</code> execute its specific code when an other <code>Entity</code> collides with it.
     *
     * @param other   the <code>Entity</code> collided with
     * @param contact the <code>Contact</code> holding the info of the collision
     * @see #onCollidedWithEntity(Entity, Contact)
     */
    protected abstract void notifyCollision(Entity other, Contact contact);

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
                acceleration.y -= value;
                break;
            case DOWN:
                acceleration.y += value;
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
        if (onGround) acceleration.y = -21;
    }

    /**
     * @param other the <code>Entity</code> to get the distance to
     * @return the euclidean distance from this to the other <code>Entity</code>
     */
    public final double distanceTo(Entity other) {
        return pos.distance(other.pos);
    }

    /**
     * @param other the <code>Entity</code> to get the distance to
     * @return the squared euclidean distance from this <code>Entity</code> to the other <code>Entity</code>
     */
    public final double distanceSqTo(Entity other) {
        return pos.distanceSq(other.pos);
    }

    /**
     * @return this <code>Entity</code>'s bounding box as it will be in the next game update
     */
    public final AxisAlignedBoundingBox getNextBoundingBox() {
        return boundingBox.offset(velocity);
    }

    public DefaultCamera newCamera() {
        return new DefaultCamera(pos, velocity, dimension.width / 2, dimension.height / 2);
    }

    public Controller newController() {
        controller = new Controller(this);
        controller.addControl(Control.createMoveControl(this, "moveLeft", Direction.LEFT));
        controller.addControl(Control.createMoveControl(this, "moveRight", Direction.RIGHT));
        controller.addControl(Control.createJumpControl(this, "jump"));
        return controller;
    }

    public void onControlLost() {
        controller = null;
    }

    //GETTERS & SETTERS

    public final boolean isDead() {
        return dead;
    }

    public final boolean isOnGround() {
        return onGround;
    }

    public final Vec2 getVelocity() {
        return velocity;
    }

    public final void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    public final void setController(Controller controller) {
        this.controller = controller;
    }
}
