package com.alientome.script.values;

import com.alientome.script.ScriptException;
import com.alientome.script.arithmetic.ArithmeticValue;

public class DoubleValue implements ArithmeticValue {

    private final Double value;

    public DoubleValue(Double value) {
        this.value = value;
    }

    @Override
    public Object objValue() throws ScriptException {
        return value;
    }

    @Override
    public Number numValue() throws ScriptException {
        return value;
    }

    @Override
    public boolean isInt() {
        return false;
    }
}
