package com.game.control;

import com.game.entities.Entity;
import com.keybindings.MappedKeyEvent;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final List<Runnable> controlledDeathListeners = new ArrayList<>();
    private final List<Control> controls = new ArrayList<>();
    private final Entity controlled;

    public Controller(Entity controlled) {
        this.controlled = controlled;
    }

    public void addControl(Control control) {
        controls.add(control);
    }

    public boolean submitEvent(MappedKeyEvent e) {

        for (Control control : controls)
            if (control.isInterestedIn(e) && control.react(e)) return true;

        return false;
    }

    public void update() {

        for (Control control : controls)
            control.update();
    }

    public void reset() {

        for (Control control : controls)
            control.reset();
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
