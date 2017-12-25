package com.alientome.game.control;


import com.alientome.core.keybindings.MappedKeyEvent;
import com.alientome.core.util.Direction;
import com.alientome.game.actions.Action;
import com.alientome.game.entities.Entity;

import java.util.function.Consumer;

public abstract class Control<T extends Entity> {

    protected final T controlled;
    protected final String id;

    public Control(T controlled, String id) {
        this.controlled = controlled;
        this.id = id;
    }

    public static <T extends Entity> Control<T> createPunctualControl(T controlled, String id, Consumer<T> action) {

        return new Control<T>(controlled, id) {
            @Override
            public boolean react(MappedKeyEvent e) {
                if (e.released) {
                    action.accept(controlled);
                    return true;
                }

                return false;
            }

            @Override
            public void update() {
            }
        };
    }

    public static <T extends Entity> Control<T> createContinuousControl(T controlled, String id, Consumer<T> action) {

        return new Control<T>(controlled, id) {

            private boolean acting = false;

            @Override
            public boolean react(MappedKeyEvent e) {
                if (e.pressed) {
                    acting = true;
                    return true;
                } else if (e.released) {
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

    public static Control<Entity> createJumpControl(Entity controlled, String id) {

        return createContinuousControl(controlled, id, Entity::jump);
    }

    public static Control<Entity> createMoveControl(Entity controlled, String id, Direction direction) {

        return createContinuousControl(controlled, id, entity -> entity.move(direction));
    }

    public static <T extends Entity> Control<T> createChargeControl(T controlled, String id, Consumer<T> startCharge, Consumer<T> stopCharge) {

        return new Control<T>(controlled, id) {
            @Override
            public boolean react(MappedKeyEvent e) {
                if (e.pressed) {
                    startCharge.accept(controlled);
                    return true;
                } else if (e.released) {
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

    public static Control<Entity> createActionControl(Entity controlled, String id, Action action) {

        return new Control<Entity>(controlled, id) {

            private boolean acting = false;

            @Override
            public boolean react(MappedKeyEvent e) {

                if (e.pressed) {
                    acting = true;
                    return true;
                } else if (e.released) {
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

    public static <T extends Entity> Control<T> createDoublePressControl(T controlled, String id, int speed, Consumer<T> action) {

        return new Control<T>(controlled, id) {

            private MappedKeyEvent lastEvent = null;
            private int sequenceStatus = 0;
            private boolean act = false;
            private long lastPress = 0;

            @Override
            public boolean react(MappedKeyEvent e) {

                long time = System.currentTimeMillis();

                if (lastEvent == null) {
                    if (e.pressed) {
                        lastEvent = e;
                        lastPress = time;
                        sequenceStatus = 1;
                    }
                } else if (sequenceStatus < 4) {

                    if (time - lastPress < speed && (lastEvent.released && e.pressed || lastEvent.pressed && e.released)) {
                        sequenceStatus++;
                        lastEvent = e;
                        lastPress = time;
                    } else {
                        sequenceStatus = 0;
                        lastEvent = null;
                    }
                }

                if (sequenceStatus == 4) {
                    act = true;
                    sequenceStatus = 0;
                    lastEvent = null;
                }

                /*if (e.isKeyReleased()) {



                    if (time - lastPress < speed) {
                        act = true;
                        lastPress = 0;
//                        return true;
                    } else
                        lastPress = time;
                }*/

                return false;
            }

            @Override
            public void update() {

                if (act)
                    action.accept(controlled);

                act = false;
            }

            @Override
            public void reset() {
                act = false;
                lastPress = 0;
            }
        };
    }

    boolean isInterestedIn(MappedKeyEvent e) {
        return e.bindingID.equals(id);
    }

    public abstract boolean react(MappedKeyEvent e);

    public abstract void update();

    public void reset() {
        //No-op on base class
    }
}
