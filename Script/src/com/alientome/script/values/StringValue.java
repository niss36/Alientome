package com.alientome.script.values;

import com.alientome.script.ScriptException;

public class StringValue implements Value {

    private final String value;

    public StringValue(String value) {
        this.value = value.substring(1, value.length() - 1);
    }

    @Override
    public String objValue() throws ScriptException {
        return value;
    }
}
