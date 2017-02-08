package com.game.control;

import com.game.entities.Entity;
import com.game.entities.actions.Action;
import com.keybindings.MappedKeyEvent;
import com.util.Direction;

import java.util.function.Consumer;

public abstract class Control {

    protected final Entity controlled;
    protected final String id;

    public Control(Entity controlled, String id) {
        this.controlled = controlled;
        this.id = id;
    }

    boolean isInterestedIn(MappedKeyEvent e) {
        return e.bindingID.equals(id);
    }

    public abstract boolean react(MappedKeyEvent e);

    public abstract void update();

    public void reset() {
        //No-op on base class
    }

    public static Control createXControl(Entity controlled, String id, Consumer<Entity> action) {

        return new Control(controlled, id) {

            private boolean acting = false;

            @Override
            public boolean react(MappedKeyEvent e) {
                if (e.isKeyPressed()) {
                    acting = true;
                    return true;
                } else if (e.isKeyReleased()) {
                    acting = false;
                    return true;
                }

                return false;
            }

            @Override
            public void update() {
                if (acting) action.accept(controlled);
            }

            @Override
            public void reset() {
                acting = false;
            }
        };
    }

    public static Control createJumpControl(Entity controlled, String id) {

        return createXControl(controlled, id, Entity::jump);
    }

    public static Control createMoveControl(Entity controlled, String id, Direction direction) {

        return createXControl(controlled, id, entity -> entity.move(direction));
    }

    public static Control createChargeControl(Entity controlled, String id, Consumer<Entity> startCharge, Consumer<Entity> stopCharge) {

        return new Control(controlled, id) {
            @Override
            public boolean react(MappedKeyEvent e) {
                if (e.isKeyPressed()) {
                    startCharge.accept(controlled);
                    return true;
                } else if (e.isKeyReleased()) {
                    stopCharge.accept(controlled);
                    return true;
                }

                return false;
            }

            @Override
            public void update() {

            }
        };
    }

    public static Control createActionControl(Entity controlled, String id, Action action) {

        return new Control(controlled, id) {

            private boolean acting = false;

            @Override
            public boolean react(MappedKeyEvent e) {

                if (e.isKeyPressed()) {
                    acting = true;
                    return true;
                } else if (e.isKeyReleased()) {
                    acting = false;
                    action.interrupt();
                    return true;
                }

                return false;
            }

            @Override
            public void update() {
                action.update();

                if (acting) action.act();
            }

            @Override
            public void reset() {
                acting = false;
            }
        };
    }
}
