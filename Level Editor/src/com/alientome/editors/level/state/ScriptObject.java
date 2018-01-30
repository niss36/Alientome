package com.alientome.editors.level.state;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.editors.level.util.Copyable;

import java.awt.*;

public class ScriptObject implements Copyable<ScriptObject> {

    public String id;
    public boolean enabled;
    public AxisAlignedBoundingBox aabb;
    public String affected;
    public String content;

    public ScriptObject(String id, boolean enabled, AxisAlignedBoundingBox aabb, String affected, String content) {
        this.id = id;
        this.enabled = enabled;
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
        return new ScriptObject(id, enabled, aabb, affected, content);
    }

    @Override
    public String toString() {
        String s = "";
        if (id != null)
            s = "#" + id + " - ";
        s += getBounds() + " on " + affected;
        if (!enabled)
            s += " - disabled";
        return s;
    }
}
