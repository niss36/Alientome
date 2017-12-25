package com.alientome.game.parse;

import com.alientome.core.collisions.AxisAlignedBoundingBox;

@FunctionalInterface
public interface XMLScriptParser {

    void parse(AxisAlignedBoundingBox aabb, String affected, String content);
}
