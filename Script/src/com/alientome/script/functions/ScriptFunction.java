package com.alientome.script.functions;

import com.alientome.script.ScriptException;
import com.alientome.script.values.Value;

@FunctionalInterface
public interface ScriptFunction {

    Value apply(Value[] args, Object c) throws ScriptException;
}
