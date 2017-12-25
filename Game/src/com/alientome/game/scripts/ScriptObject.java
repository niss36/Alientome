package com.alientome.game.scripts;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.graphics.GameGraphics;
import com.alientome.core.util.Colors;
import com.alientome.core.util.Logger;
import com.alientome.game.GameObject;
import com.alientome.game.entities.Entity;
import com.alientome.script.Script;
import com.alientome.script.ScriptException;

public class ScriptObject extends GameObject {

    private static final Logger log = Logger.get();

    private final Class<? extends Entity> affected;
    private final Script script;

    public ScriptObject(AxisAlignedBoundingBox aabb, Class<? extends Entity> affected, Script script) {
        super(aabb.getMinX(), aabb.getMinY());
        this.boundingBox = aabb;
        this.affected = affected;
        this.script = script;
    }

    public void reset() {
        script.reset();
    }

    public void update() {
        try {
            script.update();
        } catch (ScriptException e) {
            log.e("Unexpected exception while running script : ");
            e.printStackTrace();
        }
    }

    public void runOn(Entity entity) {

        if (affected.isInstance(entity) && entity.getNextBoundingBox().intersects(boundingBox)) {
            try {
                script.run(entity);
            } catch (ScriptException e) {
                log.e("Unexpected exception while running script : ");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected AxisAlignedBoundingBox boundingBox() {
        return boundingBox;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void draw(GameGraphics g, boolean debug) {

        if (debug) {
            drawDebug(g);
            g.graphics.setColor(Colors.DEFAULT);
        }
    }

    @Override
    protected void draw(GameGraphics g, int x, int y) {
    }

    @Override
    protected void drawDebug(GameGraphics g) {

        g.graphics.setColor(Colors.SCRIPT);

        boundingBox.draw(g);
    }

    @Override
    protected boolean canDraw() {
        return false;
    }

    @Override
    protected void drawSpecial(GameGraphics g, int x, int y) {
    }
}
