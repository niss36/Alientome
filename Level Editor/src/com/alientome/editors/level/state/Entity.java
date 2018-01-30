package com.alientome.editors.level.state;

import com.alientome.core.util.Direction;
import com.alientome.editors.level.util.Colors;
import com.alientome.editors.level.util.Copyable;
import com.alientome.game.util.EntityTags;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.alientome.editors.level.state.BlockState.WIDTH;

public class Entity implements Copyable<Entity> {

    public final EntityState state;
    public final int x;
    public final int y;
    public final EntityTags tags;
    public final Map<String, String> tagsMap;

    public Entity(EntityState state, int x, int y, Map<String, String> tags) {
        this.state = state;
        this.x = x;
        this.y = y;
        this.tags = new EntityTags(tags);
        tagsMap = tags;
    }

    public void draw(Graphics g, boolean showGrid) {

        int[] coordinates = getScreenCoordinates();

        Direction direction = tags.getAs("orientation", Direction.RIGHT, Direction::requireHorizontal);

        state.sprite.draw(g, coordinates[0], coordinates[1], direction, Colors.ENTITY_BOX, showGrid);
    }

    public int[] getScreenCoordinates() {
        int x = this.x * WIDTH + WIDTH / 2 - state.sprite.dimension.width / 2;
        int y = this.y * WIDTH + WIDTH - state.sprite.dimension.height;

        try {
            String[] offsets = tags.get("offsets", "0;0").split(";");
            int offsetX = Integer.parseInt(offsets[0]);
            int offsetY = Integer.parseInt(offsets[1]);

            return new int[] {x + offsetX, y + offsetY};
        } catch (Exception e) {
            return new int[] {x, y};
        }
    }

    @Override
    public Entity copy() {
        return new Entity(state, x, y, new HashMap<>(tagsMap));
    }
}
