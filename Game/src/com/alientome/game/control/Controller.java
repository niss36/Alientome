package com.alientome.game.control;

import com.alientome.core.keybindings.MappedKeyEvent;
import com.alientome.game.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final List<Runnable> controlledDeathListeners = new ArrayList<>();
    private final List<Control> controls = new ArrayList<>();
    private final Entity controlled;
    private boolean enabled = true;

    public Controller(Entity controlled) {
        this.controlled = controlled;
    }

    public void addControl(Control control) {
        controls.add(control);
    }

    public void addControlOverride(Control control) {
        controls.add(0, control);
    }

    public boolean submitEvent(MappedKeyEvent e) {

        if (enabled)
            for (Control control : controls)
                if (control.isInterestedIn(e) && control.react(e)) return true;

        return false;
    }

    public void update() {

        if (enabled)
            for (Control control : controls)
                control.update();
    }

    public void reset() {

        for (Control control : controls)
            control.reset();
    }

    public void disable() {
        enabled = false;
        reset();
    }

    public void enable() {
        enabled = true;
    }

    public void copyControls(Controller controller) {

        controls.clear();
        controls.addAll(controller.controls);
    }

    public void notifyControlledDeath() {
        controlledDeathListeners.forEach(Runnable::run);
    }

    public void addControlledDeathListener(Runnable listener) {
        controlledDeathListeners.add(listener);
    }

    public Entity getControlled() {
        return controlled;
    }
}
