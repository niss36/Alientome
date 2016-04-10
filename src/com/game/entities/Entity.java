package com.game.entities;

import com.game.Block;
import com.game.level.Level;
import com.sun.javafx.geom.Vec2d;
import com.util.AxisAlignedBB;
import com.util.Direction;
import com.util.Side;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class Entity {

    public final boolean collide;
    protected final Random entityRandom = new Random();
    final Dimension dim;
    final int blockWidth;
    public final Level level;
    protected boolean onGround;
    protected boolean dead = false;
    double x;
    double y;
    double motionX;
    double motionY;
    double maxVelocity;
    Block blockIn;
    boolean gravity;
    public boolean collidedX;
    public boolean collidedY;
    Direction facing = Direction.RIGHT;
    int imageUsed;
    private AxisAlignedBB boundingBox;

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

    private static double decrease(double d, double value) {
        return d < 0 ? d + value : d > 0 ? d - value : 0;
    }

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

    public void setDead() {

        dead = true;
    }

    void onDeath() {
        level.removeEntity(this);
    }

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

            return true;
        }

        return false;
    }

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

//            System.out.println(getClass() + " " + onGround + "  " + other.getClass() + " " + other.onGround);

            return true;
        }

        return false;
    }

    public abstract void notifyCollision(Entity other, Side side);

    public void move(Direction direction) {
        move(direction, 1);
    }

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

    public void jump() {
        if (onGround) motionY = -18;
    }

    public void draw(Graphics g, Point min, boolean debug) {

        int x = (int) this.x - min.x;
        int y = (int) this.y - min.y;

        g.fillRect(x, y, dim.width, dim.height);

        if (debug) drawHitBox(g, x, y);
    }

    public void drawHitBox(Graphics g, int x, int y) {

        Color c = g.getColor();

        g.setColor(Color.red);

        g.drawRect(x, y, dim.width, dim.height);

        g.setColor(c);
    }

    public void draw(Graphics g, BufferedImage image, int x, int y) {

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

    public void drawAnimated(Graphics g, BufferedImage[] images, int x, int y) {

        draw(g, images[(imageUsed / (images.length * 4)) % images.length], x, y);

        imageUsed = imageUsed > images.length * 8 - 1 ? 0 : imageUsed + 1;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Entity other) {
        return posVector().distance(other.posVector());
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public AxisAlignedBB getNextBoundingBox() {
        return boundingBox.offset(motionX, motionY);
    }

    public Vec2d posVector() {
        return new Vec2d(x, y);
    }

    public boolean isDead() {
        return dead;
    }
}
