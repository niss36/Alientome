package com.alientome.script.values;

import com.alientome.script.ScriptException;
import com.alientome.script.arithmetic.ArithmeticValue;

public class IntegerValue implements ArithmeticValue {

    private final Integer value;

    public IntegerValue(Integer value) {
        this.value = value;
    }

    @Override
    public Integer objValue() throws ScriptException {
        return value;
    }

    @Override
    public Integer numValue() throws ScriptException {
        return value;
    }

    @Override
    public boolean isInt() {
        return true;
    }
}
