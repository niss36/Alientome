package com.game.buffs;

import com.game.level.Level;
import org.w3c.dom.Element;

public class BuffBuilder {

    private final int type;
    private final int x;
    private final int y;
    private final Level level;

    private BuffBuilder(int type, int x, int y, Level level) {

        this.type = type;
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public static BuffBuilder parse(Element buffNode, Level level) {

        int type = Integer.parseInt(buffNode.getAttribute("type"));
        int x = Integer.parseInt(buffNode.getAttribute("spawnX"));
        int y = Integer.parseInt(buffNode.getAttribute("spawnY"));

        return new BuffBuilder(type, x, y, level);
    }

    public Buff create() {
        return Buff.createFromBlockPos(type, x, y, level);
    }
}
