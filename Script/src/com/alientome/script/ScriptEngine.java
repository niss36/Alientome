package com.alientome.script;

import com.alientome.script.functions.ScriptFunction;
import com.alientome.script.values.Value;

import java.util.HashMap;
import java.util.Map;

public class ScriptEngine {

    private final Map<String, ScriptFunction> bindings = new HashMap<>();

    public ScriptEngine() {
        addAllBindings(StandardLibrary.bindings);
    }

    public ScriptParser newParser() {
        return new ScriptParser(this);
    }

    public void addAllBindings(Map<String, ScriptFunction> other) {
        bindings.putAll(other);
    }

    public void addBinding(String name, ScriptFunction function) {
        bindings.put(name, function);
    }

    public Value call(String functionName, Value[] arguments, Object context) throws ScriptException {
        ScriptFunction function = bindings.get(functionName);
        if (function == null)
            throw new ScriptException("Unregistered function : " + functionName);
        return function.apply(arguments, context);
    }
}
