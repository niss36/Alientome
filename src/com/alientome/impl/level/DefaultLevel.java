package com.alientome.impl.level;

import com.alientome.core.collisions.Line;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.keybindings.MappedKeyEvent;
import com.alientome.core.util.MathUtils;
import com.alientome.core.vecmath.Vec2;
import com.alientome.game.blocks.Block;
import com.alientome.game.buffs.Buff;
import com.alientome.game.camera.Camera;
import com.alientome.game.entities.Entity;
import com.alientome.game.level.LevelManager;
import com.alientome.game.particles.Particle;
import com.alientome.game.scripts.ScriptObject;
import com.alientome.impl.level.source.LevelSource;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.alientome.game.profiling.ExecutionTimeProfiler.theProfiler;

public class DefaultLevel extends AbstractLevel {

    protected final Point origin = new Point();
    protected final List<Line> lines = new ArrayList<>();
    protected Camera playerCamera;

    public DefaultLevel(LevelManager manager, LevelSource source) throws IOException {
        super(manager, source);
    }

    @Override
    public void reset() {
        super.reset();

        playerCamera = source.newCamera(this);
    }

    @Override
    public void update() {

        lines.clear();

        super.update();

        playerCamera.update();
    }

    @Override
    public void draw(Graphics g, boolean debug, double interpolation) {

        theProfiler.startSection("Rendering/Drawing Level");

        Rectangle clipBounds = g.getClipBounds();

        playerCamera.transform(origin, interpolation, clipBounds, map.getBounds());

        GameGraphics graphics = new GameGraphics(g, origin, timeTicks, interpolation);

        theProfiler.startSection("Rendering/Drawing Level/Background");
        if (background == null || background.isEmpty()) {
            g.fillRect(0, 0, clipBounds.width, clipBounds.height);
        } else {
            background.draw(graphics, clipBounds, map.getBounds());
        }
        theProfiler.endSection("Rendering/Drawing Level/Background");

        theProfiler.startSection("Rendering/Drawing Level/Drawing Blocks");
        for (int x = origin.x / Block.WIDTH - 1; x < (origin.x + clipBounds.width) / Block.WIDTH + 1; x++)
            for (int y = origin.y / Block.WIDTH - 1; y < (origin.y + clipBounds.height) / Block.WIDTH + 1; y++)
                if (map.checkBounds(x, y))
                    map.getBlock(x, y, false).draw(graphics, debug);
        theProfiler.endSection("Rendering/Drawing Level/Drawing Blocks");

        theProfiler.startSection("Rendering/Drawing Level/Drawing Objects");

        for (Buff buff : buffs) buff.draw(graphics, debug);

        for (Entity entity : entities) entity.draw(graphics, debug);

        for (Particle particle : particles) particle.draw(graphics, debug);

        for (ScriptObject script : scripts) script.draw(graphics, debug);

        theProfiler.endSection("Rendering/Drawing Level/Drawing Objects");

        if (debug && getContext().getConfig().getAsBoolean("showSightLines"))
            for (Line line : lines)
                line.draw(graphics);

        theProfiler.endSection("Rendering/Drawing Level");
    }

    @Override
    public boolean sightTest(Entity entity0, Entity entity1) {

        Vec2 pos0 = new Vec2(entity0.getPos().getX() + entity0.dimension.getWidth() / 2, entity0.getPos().getY());
        Vec2 pos1 = new Vec2(entity1.getPos().getX() + entity1.dimension.getWidth() / 2, entity1.getPos().getY());

        Line line = new Line(pos0, pos1);

        lines.add(line);

        Block[] blocksLine = line(pos0, pos1);

        for (Block block : blocksLine)
            if (block.isOpaque() && block.getBoundingBox().intersects(line))
                return false; // Line#see is false by default.

        line.see = true;
        return true;
    }

    @Override
    public boolean submitEvent(MappedKeyEvent e) {
        return playerController.submitEvent(e);
    }

    @Override
    public void setCamera(Camera camera) {
        playerCamera = camera;
    }

    private Block[] line(Vec2 pos0, Vec2 pos1) {

        int n = (int) MathUtils.diagonalDistance(pos0, pos1) / Block.WIDTH + 2;

        Block[] blocks = new Block[n];

        for (int i = 0; i < n; i++) {

            double t = (double) i / n;

            Vec2 v = MathUtils.lerpVec2(pos0, pos1, t);

            blocks[i] = map.getBlockAbsCoordinates(v.getX(), v.getY());
        }

        return blocks;
    }
}
