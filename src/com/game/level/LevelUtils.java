package com.game.level;

import com.game.blocks.Block;
import com.game.entities.Entity;
import com.gui.Frame;
import com.util.Line;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Static util class for operations related to the <code>Level</code>
 */
public final class LevelUtils {

    /**
     * Not instantiable
     */
    private LevelUtils() {

    }

    /**
     * @param entity an <code>Entity</code>
     * @param other  an other <code>Entity</code>
     * @return whether the line of sight from <code>entity</code>
     * to <code>other</code> is obstructed by an opaque <code>Block</code>
     */
    public static boolean canSeeEntity(Entity entity, Entity other) {

        Point2D.Double point = new Point2D.Double(entity.getPos().x + entity.dim.width / 2, entity.getPos().y);
        Point2D.Double point1 = new Point2D.Double(other.getPos().x + other.dim.width / 2, other.getPos().y);

        Line line = new Line(point, point1);

        Frame.getInstance().panelGame.add(line);

        Block[] blocksLine = line(point, point1);

        for (Block block : blocksLine)
            if (block.isOpaque() && block.getBoundingBox().intersects(line)) {
                line.see = false;
                return false;
            }

        line.see = true;

        return true;
    }

    //Linear interpolation
    private static double lERP(double start, double end, double t) {
        return start + t * (end - start);
    }

    //Linear interpolation for Points
    private static Point lERPPoint(Point2D.Double point, Point2D.Double point1, double t) {
        return new Point((int) lERP(point.x, point1.x, t), (int) lERP(point.y, point1.y, t));
    }

    /**
     * @param point  a <code>Point</code>
     * @param point1 an other <code>Point</code>
     * @return the diagonal distance between <code>point</code> and <code>point1</code>
     */
    private static int diagonalDistance(Point2D.Double point, Point2D.Double point1) {
        double dx = point1.x - point.x, dy = point1.y - point.y;
        return (int) Math.max(Math.abs(dx), Math.abs(dy));
    }

    /**
     * Get the <code>Block</code>s forming the line from <code>point</code> to <code>point1</code>
     *
     * @param point  a <code>Point</code>
     * @param point1 an other <code>Point</code>
     * @return an array of <code>Block</code>s corresponding to
     * the line between <code>point</code> and <code>point1</code>
     */
    private static Block[] line(Point2D.Double point, Point2D.Double point1) {
        int n = diagonalDistance(point, point1) / Block.width + 2;

        Block[] tiles = new Block[n];

        for (int i = 0; i < n; i++) {
            double t = (double) i / n;

            Point p = lERPPoint(point, point1, t);

            tiles[i] = LevelMap.getInstance().getBlock(p.x / Block.width, p.y / Block.width, true);
        }
        return tiles;
    }
}
