package com.game.level;

import com.game.Block;
import com.game.Game;
import com.game.entities.Entity;
import com.game.entities.EntityEnemy;
import com.game.entities.EntityPlayer;
import com.gui.Panel;
import com.util.Direction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Level {

    private static final Level instance = new Level();
    private final ArrayList<Point> spawnList = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    public EntityPlayer player;
    private int[][] blocks;
    private int x;
    private int y;
    private int spawnX;
    private int spawnY;

    private int blockWidth;

    private Level() {
    }

    public static Level getInstance() {
        return instance;
    }

    public void init(String path) {

        Scanner sc = new Scanner(new InputStreamReader(getClass().getResourceAsStream(path)));

        sc.next();
        x = sc.nextInt();
        y = sc.nextInt();

        sc.next();
        spawnX = sc.nextInt();
        spawnY = sc.nextInt();

        sc.next();
        blocks = new int[x][y];

        for (int i = 0; i < y; i++)
            for (int j = 0; j < x; j++)
                blocks[j][i] = sc.nextInt();

        sc.next();
        while (sc.hasNext()) {

            spawnList.add(new Point(sc.nextInt(), sc.nextInt()));
        }
    }

    public int get(int x, int y) {
        return x >= 0 && x < this.x && y >= 0 && y < this.y ? blocks[x][y] : Integer.MIN_VALUE;
    }

    public void reset() {

        blockWidth = Block.width;

        entities.clear();

        spawnPlayer();

        for (Point p : spawnList) {

            spawnEntity(new EntityEnemy(p.x * Block.width, p.y * Block.width, this, 300));
        }
    }

    private void spawnPlayer() {

        player = new EntityPlayer(spawnX * Block.width, spawnY * Block.width, this);
    }

    public void spawnEntity(Entity entity) {

        entities.add(entity);
    }

    public void removeEntity(Entity entity) {

        entities.remove(entity);
    }

    public void processEntityCollisions(Entity entity) {

        @SuppressWarnings("unchecked")
        ArrayList<Entity> aL = (ArrayList<Entity>) entities.clone();
        aL.remove(entity);

        for (Entity entity1 : aL) {

            if (entity1.getNextBoundingBox().intersectsWith(entity.getNextBoundingBox())) {

                //entity.onCollidedWithEntity(entity1, entity1.getNextBoundingBox().intersectionSideWith(entity.getNextBoundingBox()));
                entity1.onCollidedWithEntity(entity, entity.getNextBoundingBox().intersectionSideWith(entity1.getNextBoundingBox()));
            }
        }
    }

    public boolean canSeeEntity(Entity entity, Entity other) {

        Point point = new Point((int) (entity.getX() / blockWidth),
                (int) (entity.getY() / blockWidth));
        Point point1 = new Point((int) (other.getX() / blockWidth),
                (int) (other.getY() / blockWidth));

        Block[] tiles = line(point, point1);

        for (Block tile : tiles) if (tile.isOpaque()) return false;

        return true;
    }

    private double lERP(double start, double end, double t) {
        return start + t * (end-start);
    }

    private Point lERPPoint(Point point, Point point1, double t) {
        return new Point((int) lERP(point.x, point1.x, t), (int) lERP(point.y, point1.y, t));
    }

    private int diagonalDistance(Point point, Point point1) {
        int dx = point1.x - point.x, dy = point1.y - point.x;
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    private Block[] line(Point point, Point point1) {
        int n = diagonalDistance(point, point1);

        Block[] tiles = new Block[n];

        for(int i = 0; i < n; i++) {
            double t = (double) i / n;
            tiles[i] = new Block(lERPPoint(point, point1, t));
        }
        return tiles;
    }

    public void update(Game game, Panel panel) {

        for (int i = 0; i < game.pressedKeys.size(); i++) {

            int key = game.pressedKeys.get(i);

            if (key == KeyEvent.VK_SPACE) {
                player.jump();
            }

            Direction d = Direction.toDirection(key);

            if (d != null) {
                player.move(d);
            }
        }

        panel.update(player, entities);
    }
}