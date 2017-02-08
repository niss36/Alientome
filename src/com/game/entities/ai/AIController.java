package com.game.entities.ai;

import com.game.control.Control;
import com.game.control.Controller;
import com.keybindings.MappedKeyEvent;

public class AIController extends Controller {

    private final AI ai;

    public AIController(AI ai) {
        super(ai.entity);

        this.ai = ai;
    }

    @Override
    public void addControl(Control control) {
        //No-op
    }

    @Override
    public boolean submitEvent(MappedKeyEvent e) {
        return false;
    }

    @Override
    public void update() {
        if (ai.getState() == null) ai.start();
        ai.act();
    }

    @Override
    public void reset() {
        ai.reset();
    }

    @Override
    public void copyControls(Controller controller) {
        //No-op
    }

    @Override
    public void notifyControlledDeath() {
        //No-op
    }

    @Override
    public void addControlledDeathListener(Runnable listener) {
        //No-op
    }
}
