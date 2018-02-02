package com.alientome.game.entities;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.collisions.Contact;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Direction;
import com.alientome.core.util.MathUtils;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.GameObject;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.camera.Camera;
import com.alientome.game.camera.DefaultCamera;
import com.alientome.game.collisions.DynamicBoundingBox;
import com.alientome.game.commands.CommandSender;
import com.alientome.game.commands.messages.ConsoleMessage;
import com.alientome.game.control.Control;
import com.alientome.game.control.Controller;
import com.alientome.game.entities.parse.EntityState;
import com.alientome.game.events.MessageEvent;
import com.alientome.game.level.Level;
import com.alientome.game.registry.GameRegistry;
import com.alientome.game.util.EntityTags;
import com.alientome.game.util.Util;
import com.alientome.visual.animations.AnimationsHandler;

import java.awt.*;

import static com.alientome.core.util.Colors.DEBUG;
import static com.alientome.game.blocks.BlockConstants.*;
import static com.alientome.game.profiling.ExecutionTimeProfiler.theProfiler;

/**
 * The <code>Entity</code> class is used to represent non-<code>Block</code> objects in the <code>Level</code>.
 */
public abstract class Entity extends GameObject implements CommandSender {

    private static final Vec2 gravity = new Vec2(0, 1);
    public final Dimension dimension;
    public final Level level;
    public Direction facing = Direction.RIGHT;
    protected final String customName;
    protected final AnimationsHandler handler;
    protected final Vec2 velocity = new Vec2();
    public boolean collidedX;
    public boolean collidedY;
    public GameObject lastCollidedWith;
    protected boolean affectedByGravity = true;
    protected boolean onGround;
    protected double maxVelocity = 5;
    protected Controller controller;
    protected int deathTimer = 0;
    protected boolean dead = false;

    /**
     * Initialize the <code>Entity</code>.
     *
     * @param pos       this <code>Entity</code>'s position
     * @param dimension the <code>Dimension</code> (width, height) of this <code>Entity</code>
     * @param level     the <code>Level</code> this <code>Entity</code> is in
     * @param tags      an instance of <code>EntityTags</code> containing optional creation tags
     */
    protected Entity(Vec2 pos, Dimension dimension, Level level, EntityTags tags) {

        super(pos);

        this.dimension = dimension;
        dimension.width *= 2;
        dimension.height *= 2;
        this.level = level;

        actualizeBoundingBox();

        handler = SpritesLoader.newAnimationsHandlerFor(getClass());

        String name = null;

        if (tags != null) {
            facing = tags.getAs("orientation", facing, Direction::requireHorizontal);
            maxVelocity = tags.getAsDouble("maxSpeed", maxVelocity);
            name = tags.get("name", null);
        }

        customName = name;
    }

    public static Entity create(EntityState state, Level level) {

        if (state == null)
            throw new IllegalArgumentException("Null entity state");

        GameRegistry registry = level.getContext().getRegistry();

        Class<?>[] constructorTypes = {Vec2.class, Level.class, EntityTags.class};

        Vec2 pos = new Vec2();
        Object[] args = {pos, level, state.tags};

        Entity entity = Util.create(registry.getEntitiesRegistry(), state.identifier, constructorTypes, args);

        int[] offsets = state.tags.getAs("offsets", new int[] {0, 0}, s -> {
            String[] split = s.split(";");
            return new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        });

        pos.setX(state.spawnX * Block.WIDTH + Block.WIDTH / 2 - entity.dimension.width / 2 + offsets[0]);
        pos.setY(state.spawnY * Block.WIDTH + Block.WIDTH - entity.dimension.height + offsets[1]);

        return entity;
    }

    public final void preUpdate() {

        theProfiler.startSection("Entity Pre-Update");

        if (dead) {
            if (deathTimer > 0)
                deathTimer--;
            else
                onDeath();
            return;
        }

        if (controller != null)
            controller.update();

        preUpdateInternal();

        // 1: Apply external forces.
        MathUtils.decrease(velocity, 0.5d); // Pseudo-friction

        if (affectedByGravity) velocity.add(gravity); // Gravity

        theProfiler.endSection("Entity Pre-Update");
    }

    protected void preUpdateInternal() {
        // NO-OP on base class
    }

    public final void checkCollisions() {

        theProfiler.startSection("Entity Collisions");

        // 2: Resolve collisions
        if (canBeCollidedWith()) {

            collidedY = false;
            collidedX = false;

            onGround = false;

            AxisAlignedBoundingBox coveredArea = boundingBox.union(getNextBoundingBox());

            // 2.1: Collide blocks
            level.forEachCollidableBlock(coveredArea, this::collideBlock);

            // 2.2: Collide entities
            level.forEachCollidableEntity(coveredArea, this::collideEntity, this);
        }

        theProfiler.endSection("Entity Collisions");
    }

    protected void collideBlock(Block block) {

        Contact contact = getNextBoundingBox().processContact(block.getBoundingBox());

        if (contact != null) {

            //Check internal edges
            int nextBlockX = block.blockX + (int) contact.normal.getX();
            int nextBlockY = block.blockY + (int) contact.normal.getY();

            if (level.getMap().getBlock(nextBlockX, nextBlockY).canBeCollidedWith())
                return;

            onCollidedWithBlock(block, contact);
        }
    }

    protected void collideEntity(Entity entity) {

        Contact contact = getNextBoundingBox().processContact(entity.getNextBoundingBox());

        if (contact != null)
            onCollidedWithEntity(entity, contact);
    }

    public final void postUpdate() {

        theProfiler.startSection("Entity Post-Update");

        // 4: Integrate velocity into position
        pos.add(velocity);

        actualizeBoundingBox();

        level.applyScripts(this);

        postUpdateInternal();

        theProfiler.endSection("Entity Post-Update");
    }

    protected void postUpdateInternal() {
        // NO-OP on base class
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

        g.graphics.setColor(DEBUG);

        boundingBox.draw(g);

        boundingBox.union(getNextBoundingBox()).draw(g);
    }

    @Override
    protected boolean canDraw() {
        return handler.canDraw();
    }

    @Override
    protected void drawSpecial(GameGraphics g, int x, int y) {
        boundingBox.fill(g);
    }

    @Override
    public int getInterpolatedX(double partialTick) {
        return (int) (pos.getX() + velocity.getX() * partialTick);
    }

    @Override
    public int getInterpolatedY(double partialTick) {
        return (int) (pos.getY() + velocity.getY() * partialTick);
    }

    public boolean beforeCollideWith(Entity other) {
        return true;
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
    public void addConsoleMessage(ConsoleMessage message) {
        level.getContext().getDispatcher().submit(new MessageEvent(message));
    }

    /**
     * Set this <code>Entity</code> dead. It will be deleted next call to <code>onUpdate()</code>.
     */
    public final void setDead() {
        if (!dead && deathTimer == 0) onSetDead();
        dead = true;
        velocity.set(0, 0);
    }

    protected void onSetDead() {
    }

    /**
     * Called when this <code>Entity</code> is updated after being set dead.
     */
    private void onDeath() {

        if (deathTimer == 0) {
            level.removeEntity(this);
            deathTimer = -1;
        }
    }

    /**
     * Called when this <code>Entity</code> collides with a <code>Block</code>.
     *
     * @param block   the <code>Block</code> collided with
     * @param contact info relative to the collision
     * @return whether the collision was processed
     */
    public boolean onCollidedWithBlock(Block block, Contact contact) {

        switch (block.beforeCollide(this, contact)) {

            case NO_COLLISION:
                return false;

            case COLLISION:
                Vec2 mtv = new Vec2(contact.normal, contact.depth);

                velocity.add(mtv);

                if (contact.normal.getY() < 0)
                    onGround = true;

            case PROCESSED_COLLISION:
                if (contact.normal.getX() != 0)
                    collidedX = true;
                else // y is assumed to be non-zero
                    collidedY = true;

                lastCollidedWith = block;

            default:
                return true;
        }
    }

    /**
     * Called when this <code>Entity</code> collides with another <code>Entity</code>
     *
     * @param other   the <code>Entity</code> collided with
     * @param contact the <code>Contact</code> for the intersection that was detected, or null if none
     * @return whether any collision happened and was resolved
     */
    public boolean onCollidedWithEntity(Entity other, Contact contact) {

        if (beforeCollideWith(other) && other.beforeCollideWith(this)) {

            double weightedDepth = MathUtils.roundClosest(contact.depth / 2, 0.5);

            Vec2 mtv = new Vec2(contact.normal, weightedDepth);

            velocity.add(mtv);
            other.velocity.sub(mtv);

            if (contact.normal.getX() != 0)
                collidedX = true;
            else // y is assumed to be non-zero
                collidedY = true;

            lastCollidedWith = other;

            if (contact.normal.getY() > 0) other.onGround = onGround;
            else if (contact.normal.getY() < 0) onGround = other.onGround;

            notifyCollision(other, contact);
            other.notifyCollision(this, contact.reverse());
            return true;
        }

        return false;
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

        double maxVelocity = this.maxVelocity + 0.5;

        boolean wasExcess = velocity.getX() > maxVelocity || velocity.getX() < -maxVelocity;

        if (!(wasExcess && direction.horizontal))
            velocity.addScaled(direction.normal, value);

        if (direction.horizontal) {
            facing = direction;
            if (!wasExcess)
                velocity.setX(clamp(velocity.getX(), -maxVelocity, maxVelocity));
        }
    }

    /**
     * Makes this <code>Entity</code> jump if it is on the ground.
     */
    public void jump() {
        if (onGround) {
            if (velocity.getY() < 0)
                velocity.setY(0);
            velocity.addY(-21);
        }
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

    public Camera newCamera() {
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

    public boolean isDead() {
        return dead;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public Vec2 getVelocity() {
        return velocity;
    }

    public int getWidth() {
        return dimension.width;
    }

    public int getHeight() {
        return dimension.height;
    }

    public Vec2 getCenterPos() {
        return new Vec2(getWidth() / 2d, getHeight() / 2d).add(pos);
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean hasCustomName() {
        return customName != null;
    }

    public String getCustomName() {
        return customName;
    }

    public abstract String getNameKey();
}
