package com.alientome.editors.level.state;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.editors.level.util.Copyable;

import java.awt.*;

public class ScriptObject implements Copyable<ScriptObject> {

    public AxisAlignedBoundingBox aabb;
    public String affected;
    public String content;

    public ScriptObject(AxisAlignedBoundingBox aabb, String affected, String content) {
        this.aabb = aabb;
        this.affected = affected;
        this.content = content;
    }

    public void draw(Graphics g) {
        g.drawRect((int) aabb.getMinX(), (int) aabb.getMinY(), (int) aabb.getWidth(), (int) aabb.getHeight());
    }

    public String getBounds() {
        return (int) aabb.getMinX() + "; " + (int) aabb.getMinY() + " -> " + (int) aabb.getMaxX() + "; " + (int) aabb.getMaxY();
    }

    @Override
    public ScriptObject copy() {
        return new ScriptObject(aabb, affected, content);
    }
}
