package com.alientome.script.values;

import com.alientome.script.ScriptException;

public class VoidValue implements Value {

    public static final VoidValue VOID = new VoidValue();

    private VoidValue() {
    }

    @Override
    public Object objValue() throws ScriptException {
        throw new ScriptException("Void value");
    }
}
