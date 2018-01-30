package com.alientome.script.values;

import com.alientome.script.Script;
import com.alientome.script.ScriptException;
import com.alientome.script.arithmetic.ArithmeticValue;

public class Expression implements ArithmeticValue {

    private final Script script;
    private final String functionName;
    private final Value[] arguments;
    private boolean isInt;

    public Expression(Script script, String functionName, Value[] arguments) {

        this.script = script;
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public void execute() throws ScriptException {
        script.call(functionName, arguments);
    }

    @Override
    public Object objValue() throws ScriptException {
        Object result = script.call(functionName, arguments).objValue();
        isInt = result instanceof Integer;
        return result;
    }

    @Override
    public Number numValue() throws ScriptException {
        return (Number) objValue();
    }

    @Override
    public boolean isInt() {
        return isInt;
    }
}
