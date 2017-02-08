package com.game.level;

import com.game.blocks.Block;
import com.game.entities.Entity;
import com.util.Line;
import com.util.Vec2;

import static com.util.profile.ExecutionTimeProfiler.theProfiler;

/**
 * Static util class for operations related to the <code>Level</code>
 */
public class LevelUtils {

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

        theProfiler.startSection("Sight Test");
        Vec2 pos = new Vec2(entity.getPos().x + entity.dimension.getWidth() / 2, entity.getPos().y);
        Vec2 pos1 = new Vec2(other.getPos().x + other.dimension.getWidth() / 2, other.getPos().y);

        Line line = new Line(pos, pos1);

        entity.level.addLine(line);

        Block[] blocksLine = line(pos, pos1, entity.level.map);

        for (Block block : blocksLine)
            if (block.isOpaque() && block.getBoundingBox().intersects(line)) {
                line.see = false;
                theProfiler.endSection("Sight Test");
                return false;
            }

        line.see = true;

        theProfiler.endSection("Sight Test");
        return true;
    }

    //Linear interpolation
    private static double lERP(double start, double end, double t) {
        return start + t * (end - start);
    }

    //Linear interpolation for Vec2s
    private static Vec2 lERPVec2(Vec2 pos, Vec2 pos1, double t) {
        return new Vec2(lERP(pos.x, pos1.x, t), lERP(pos.y, pos1.y, t));
    }

    /**
     * @param pos  a <code>Point</code>
     * @param pos1 an other <code>Point</code>
     * @return the diagonal distance between <code>pos</code> and <code>pos1</code>
     */
    private static int diagonalDistance(Vec2 pos, Vec2 pos1) {
        double dx = pos1.x - pos.x, dy = pos1.y - pos.y;
        return (int) Math.max(Math.abs(dx), Math.abs(dy));
    }

    /**
     * Get the <code>Block</code>s forming the line from <code>pos</code> to <code>pos1</code>
     *
     * @param pos  a <code>Point</code>
     * @param pos1 an other <code>Point</code>
     * @return an array of <code>Block</code>s corresponding to
     * the line between <code>pos</code> and <code>pos1</code>
     */
    private static Block[] line(Vec2 pos, Vec2 pos1, LevelMap map) {
        int n = diagonalDistance(pos, pos1) / Block.WIDTH + 2;

        Block[] tiles = new Block[n];

        for (int i = 0; i < n; i++) {
            double t = (double) i / n;

            Vec2 p = lERPVec2(pos, pos1, t);

            tiles[i] = map.getBlockAbsCoordinates(p.x, p.y);
        }
        return tiles;
    }
}
