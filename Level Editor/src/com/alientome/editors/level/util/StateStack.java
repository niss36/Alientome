package com.alientome.editors.level.util;

import com.alientome.editors.level.state.LevelState;
import javafx.scene.control.MenuItem;

import java.util.Stack;

public class StateStack {

    private final Stack<LevelState> stack = new Stack<>();
    private final MenuItem button;
    private final String baseText;

    public StateStack(MenuItem button, String baseText) {
        this.button = button;
        this.baseText = baseText;
    }

    public void clear() {
        stack.clear();
        button.setText(baseText);
        button.setDisable(true);
    }

    public void push(LevelState state) {
        stack.push(state);
        button.setText(baseText + " " + state.name);
        button.setDisable(false);
    }

    public LevelState pop() {
        LevelState pop = stack.pop();
        if (stack.empty()) {
            button.setText(baseText);
            button.setDisable(true);
        } else
            button.setText(baseText + " " + stack.peek().name);

        return pop;
    }
}
