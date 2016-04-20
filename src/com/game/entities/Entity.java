package com.game.entities;

import com.game.Block;
import com.game.Level;
import com.sun.javafx.geom.Vec2d;
import com.util.AxisAlignedBB;
import com.util.Direction;
import com.util.Side;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * The <code>Entity</code> class is used to represent non-<code>Block</code> objects in the <code>Level</code>
 */
public abstract class Entity {

    public final boolean collide;
    public final Dimension dim;
    public final Level level;
    final Random entityRandom = new Random();
    private final int blockWidth;
    public boolean collidedX;
    public boolean collidedY;
    public Object lastCollidedWith;
    boolean onGround;
    double x;
    double y;
    double motionX;
    double motionY;
    double maxVelocity;
    Block blockIn;
    boolean gravity;
    Direction facing = Direction.RIGHT;
    private boolean dead = false;
    private int imageUsed;
    private int animationCount;
    private AxisAlignedBB boundingBox;

    /**
     * Initialize the <code>Entity</code>
     *
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param dim   the <code>Dimension</code> of this <code>Entity</code> (width, height)
     * @param level the <code>Level</code> this <code>Entity</code> is in
     */
    Entity(double x, double y, Dimension dim, Level level) {

        this.x = x;
        this.y = y;
        this.dim = dim;
        this.level = level;

        motionX = 0;
        motionY = 0;

        maxVelocity = 5;

        blockWidth = Block.width;

        collide = true;

        gravity = true;

        boundingBox = new AxisAlignedBB(x, y, x + dim.width, y + dim.height);
    }

    static Entity create(int type, int x, int y, Level level) {
        switch (type) {
            case 1:
                return new EntityEnemy(x, y, level, 300);

            case 2:
                return new EntityEnemyShield(x, y, level, 300);

            default:
                return null;
        }
    }

    /**
     * Private method used to decrease motion.
     *
     * @param d     the number to decrease
     * @param value the amount to decrease
     * @return If <code>d==0</code> 0 else <code>d</code> closer to 0 by <code>value</code>
     */
    private static double decrease(double d, double value) {
        return d < 0 ? d + value : d > 0 ? d - value : 0;
    }

    /**
     * Called every game update. Used to update the <code>Entity</code>'s position and logic.
     * Overrides should always call <code>super.onUpdate()</code>.
     */
    public void onUpdate() {

        if (!dead) {
            if (collide) {

                collidedY = false;
                collidedX = false;

                boundingBox = new AxisAlignedBB(x, y, x + dim.width, y + dim.height);
                blockIn = new Block((int) ((x + dim.width / 2) / blockWidth), (int) ((y + dim.height / 2) / blockWidth));

                if (motionY != 0) {

                    int t = (int) (motionY / Math.abs(motionY));

                    for (int i = -1; i < 2; i++) {

                        Block b = new Block(blockIn.getX() + i, blockIn.getY() + t);

                        if (b.getBoundingBox().intersectsWith(boundingBox.offset(0, motionY))) {

                            onCollidedWithBlock(b, Side.toSide(0, t));

                            boundingBox = new AxisAlignedBB(x, y, x + dim.width, y + dim.height);
                            blockIn = new Block((int) ((x + dim.width / 2) / blockWidth), (int) ((y + dim.height / 2) / blockWidth));
                        }
                    }
                }

                if (motionX != 0) {

                    int t = (int) (motionX / Math.abs(motionX));

                    for (int i = -1; i < 2; i++) {

                        Block b = new Block(blockIn.getX() + t, blockIn.getY() + i);

                        if (b.getBoundingBox().intersectsWith(boundingBox.offset(motionX, 0))) {
                            onCollidedWithBlock(b, Side.toSide(t, 0));
                            boundingBox = new AxisAlignedBB(x, y, x + dim.width, y + dim.height);
                        }
                    }
                }

                if (gravity) {
                    onGround = false;

                    for (int i = 0; i < 2; i++)
                        onGround = new Block((int) ((x + i * dim.width - i) / blockWidth), (int) ((y + 1 + dim.height) / blockWidth)).isOpaque() || onGround;
                }

                level.processEntityCollisions(this);

                if (!collidedX) x += motionX;
                if (!collidedY) y += motionY;

            } else {
                x += motionX;
                y += motionY;
            }

            if (!onGround && gravity) move(Direction.DOWN);

            motionX = decrease(motionX, 0.5d);
            motionY = decrease(motionY, 0.5d);

            blockIn = new Block((int) ((x + dim.width / 2) / blockWidth), (int) ((y + dim.height / 2) / blockWidth));
        } else onDeath();
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
     * @param block the <code>Block</code> collided with
     * @param side  the <code>Side</code> of the collision
     * @return whether the collision was processed
     */
    public boolean onCollidedWithBlock(Block block, Side side) {

        if (block.isOpaque()) {
            switch (side) {
                case TOP:
                    y = block.getY() * blockWidth - dim.height;
                    motionY = 0;
                    collidedY = true;
                    break;
                case BOTTOM:
                    y = block.getY() * blockWidth + blockWidth;
                    motionY = 0;
                    collidedY = true;
                    break;
                case LEFT:
                    x = block.getX() * blockWidth - dim.width;
                    motionX = 0;
                    collidedX = true;
                    break;
                case RIGHT:
                    x = block.getX() * blockWidth + blockWidth;
                    motionX = 0;
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

            switch (side) {
                case TOP:
                    y = other.y - dim.height;
                    motionY = 0;
                    other.motionY = 0;
                    collidedY = true;
                    break;
                case BOTTOM:
                    if (!onGround) y = other.y + other.dim.height;
                    if (onGround) motionY = 0;
                    else if (motionY < 0) motionY = 0;
                    other.motionY = 0;
                    other.onGround = onGround;
                    collidedY = true;
                    break;
                case LEFT:
                    x = other.x - dim.width;
                    motionX = 0;
                    other.motionX = 0;
                    collidedX = true;
                    break;
                case RIGHT:
                    x = other.x + other.dim.width;
                    motionX = 0;
                    other.motionX = 0;
                    collidedX = true;
                    break;
            }

            lastCollidedWith = other;

//            System.out.println(getClass() + " " + onGround + "  " + other.getClass() + " " + other.onGround);

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
    public abstract void notifyCollision(Entity other, Side side);

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
    void move(Direction direction, double value) {

        switch (direction) {
            case UP:
                jump();
                break;
            case DOWN:
                if (!onGround) motionY += value;
                break;
            case RIGHT:
                motionX += value;
                facing = direction;
                break;
            case LEFT:
                motionX -= value;
                facing = direction;
                break;
        }

        motionX = motionX < -maxVelocity ? -maxVelocity : motionX > maxVelocity ? maxVelocity : motionX;
    }

    /**
     * Makes this <code>Entity</code> jump if it is on the ground.
     */
    public void jump() {
        if (onGround) motionY = -18;
    }

    /**
     * Draws this <code>Entity</code> using the supplied <code>Graphics</code>
     *
     * @param g     the <code>Graphics</code> to draw with
     * @param min   the relative origin of the draw surface
     * @param debug whether this <code>Entity</code>'s bounding box should be drawn
     */
    public void draw(Graphics g, Point min, boolean debug) {

        int x = (int) this.x - min.x;
        int y = (int) this.y - min.y;

        g.fillRect(x, y, dim.width, dim.height);

        if (debug) drawBoundingBox(g, x, y);
    }

    /**
     * Outlines this <code>Entity</code>'s <code>AxisAlignedBoundingBox</code>. Called only in debug mode.
     *
     * @param g the <code>Graphics</code> to draw with
     * @param x the top-left corner's x coordinate
     * @param y the top-left corner's y coordinate
     */
    void drawBoundingBox(Graphics g, int x, int y) {

        Color c = g.getColor();

        g.setColor(Color.red);

        g.drawRect(x, y, dim.width, dim.height);

        g.setColor(c);
    }

    /**
     * Draws the given image at the specified location, rotating it vertically
     * according to the facing <code>Direction</code>.
     *
     * @param g     the <code>Graphics</code> to draw with
     * @param image the <code>BufferedImage</code> to draw
     * @param x     the top-left corner's x coordinate
     * @param y     the top-left corner's y coordinate
     */
    void drawImage(Graphics g, BufferedImage image, int x, int y) {

        switch (facing) {

            case LEFT:
                g.drawImage(image, x, y, null);
                break;
            case RIGHT:
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(image, x + image.getWidth(), y, -image.getWidth(), image.getHeight(), null);
                break;
        }
    }

    /**
     * Draws the given images alternating over them a given number of times.
     *
     * @param g      the <code>Graphics</code> to draw with
     * @param images the <code>BufferedImage[]</code> to draw
     * @param x      the top-left corner's x coordinate
     * @param y      the top-left corner's y coordinate
     * @param times  the number of game updates before changing image
     * @see this.drawImage
     */
    void drawAnimated(Graphics g, BufferedImage[] images, int x, int y, int times) {

        animationCount++;

        if (animationCount >= times) {
            animationCount = 0;
            imageUsed++;
        }

        if (imageUsed >= images.length) imageUsed = 0;

        drawImage(g, images[imageUsed], x, y);
    }

    /**
     * @param other the <code>Entity</code> to get the distance to
     * @return the euclidean distance from this to the other <code>Entity</code>
     */
    public double distanceTo(Entity other) {
        return posVector().distance(other.posVector());
    }

    /**
     * @return this <code>Entity</code>'s bounding box as it will be in the next game update
     */
    public AxisAlignedBB getNextBoundingBox() {
        return boundingBox.offset(motionX, motionY);
    }

    /**
     * @return a <code>Vec2d</code> containing this <code>Entity</code>'s coordinates
     */
    public Vec2d posVector() {
        return new Vec2d(x, y);
    }

    //GETTERS AND SETTERS

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public boolean isDead() {
        return dead;
    }
}
