package com.game.entities;

import com.game.level.Level;
import com.util.Vec2;
import com.util.collisions.Contact;
import com.util.visual.GameGraphics;

import java.awt.*;

public class EntityDummy extends Entity {

    EntityDummy(Vec2 pos, Level level) {
        super(pos, new Dimension(16, 24), level);
    }

    @Override
    protected void drawSpecial(GameGraphics g) {
        if (onGround) g.graphics.setColor(Color.cyan);
        super.drawSpecial(g);
    }

    @Override
    protected void notifyCollision(Entity other, Contact contact) {

    }
}
