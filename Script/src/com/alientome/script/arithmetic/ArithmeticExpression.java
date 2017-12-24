package com.alientome.script.arithmetic;

import com.alientome.script.ScriptException;

public class ArithmeticExpression implements ArithmeticValue {

    private final ArithmeticValue a, b;
    private final ArithmeticOperation op;
    private boolean isInt = false;

    public ArithmeticExpression(ArithmeticValue a, ArithmeticValue b, ArithmeticOperation op) {
        this.a = a;
        this.b = b;
        this.op = op;
    }

    @Override
    public Object objValue() throws ScriptException {
        return numValue();
    }

    @Override
    public Number numValue() throws ScriptException {
        Number numA = a.numValue(), numB = b.numValue();
        if (a.isInt() && b.isInt()) {
            isInt = true;
            return op.apply((int) numA, (int) numB);
        } else {
            isInt = false;
            return op.apply((double) numA, (double) numB);
        }
    }

    @Override
    public boolean isInt() {
        return isInt;
    }
}
