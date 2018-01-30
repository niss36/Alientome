package com.alientome.script.values;

import com.alientome.script.ScriptException;

public class BooleanValue implements Value {

    private final Boolean value;

    public BooleanValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean objValue() throws ScriptException {
        return value;
    }
}
