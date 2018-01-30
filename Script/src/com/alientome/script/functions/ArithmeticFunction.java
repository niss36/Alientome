package com.alientome.script.functions;

import com.alientome.script.ScriptException;
import com.alientome.script.arithmetic.ArithmeticValue;
import com.alientome.script.values.Value;

public interface ArithmeticFunction extends ScriptFunction {

    @Override
    default Value apply(Value[] args, Object c) throws ScriptException {
        boolean isInt = true;
        for (Value arg : args)
            if (arg instanceof ArithmeticValue) {
                if (!((ArithmeticValue) arg).isInt())
                    isInt = false;
            } else throw new ScriptException("Non-arithmetic value (" + arg + ")");

        if (isInt) {
            int[] evaluated = new int[args.length];
            for (int i = 0; i < args.length; i++)
                evaluated[i] = ((ArithmeticValue) args[i]).numValue().intValue();
            return apply(evaluated, c);
        } else {
            double[] evaluated = new double[args.length];
            for (int i = 0; i < args.length; i++)
                evaluated[i] = ((ArithmeticValue) args[i]).numValue().doubleValue();
            return apply(evaluated, c);
        }
    }

    @Override
    default Value apply(Object[] args, Object c) throws ScriptException {
        throw new ScriptException("Unsupported");
    }

    Value apply(int[] args, Object c) throws ScriptException;

    Value apply(double[] args, Object c) throws ScriptException;
}
