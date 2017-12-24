package com.alientome.script.functions;

import com.alientome.script.ScriptException;
import com.alientome.script.values.Value;
import com.alientome.script.values.VoidValue;

@FunctionalInterface
public interface VoidFunction extends ScriptFunction {

    @Override
    default Value apply(Value[] args, Object c) throws ScriptException {
        applyVoid(args, c);
        return VoidValue.VOID;
    }

    void applyVoid(Value[] args, Object c) throws ScriptException;
}
