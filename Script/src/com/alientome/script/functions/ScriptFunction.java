package com.alientome.script.functions;

import com.alientome.script.ScriptException;
import com.alientome.script.values.Value;

@FunctionalInterface
public interface ScriptFunction {

    default Value apply(Value[] args, Object c) throws ScriptException {
        Object[] evaluated = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            evaluated[i] = args[i].objValue();
        return apply(evaluated, c);
    }

    Value apply(Object[] args, Object c) throws ScriptException;
}
