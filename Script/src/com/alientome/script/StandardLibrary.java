package com.alientome.script;

import com.alientome.script.arithmetic.ArithmeticValue;
import com.alientome.script.functions.ScriptFunction;
import com.alientome.script.functions.VoidFunction;
import com.alientome.script.values.DoubleValue;
import com.alientome.script.values.IntegerValue;
import com.alientome.script.values.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StandardLibrary {

    public static final Map<String, ScriptFunction> bindings;

    static {

        Map<String, ScriptFunction> temp = new HashMap<>();

        temp.put("print", (VoidFunction) (args, c) -> System.out.print(asString(args)));

        temp.put("println", (VoidFunction) (args, c) -> System.out.println(asString(args)));

        temp.put("min", (args, c) -> {
            ArithmeticValue a = (ArithmeticValue) args[0], b = (ArithmeticValue) args[1];

            Number numA = a.numValue(), numB = b.numValue();
            if (a.isInt() && b.isInt())
                return new IntegerValue(Math.min((int) numA, (int) numB));
            else
                return new DoubleValue(Math.min((double) numA, (double) numB));
        });

        temp.put("max", (args, c) -> {
            ArithmeticValue a = (ArithmeticValue) args[0], b = (ArithmeticValue) args[1];

            Number numA = a.numValue(), numB = b.numValue();
            if (a.isInt() && b.isInt())
                return new IntegerValue(Math.max((int) numA, (int) numB));
            else
                return new DoubleValue(Math.max((double) numA, (double) numB));
        });

        bindings = Collections.unmodifiableMap(temp);
    }

    private static String asString(Value[] args) {

        StringBuilder builder = new StringBuilder(args.length * 10);

        for (int i = 0; i < args.length; i++) {
            try {
                builder.append(args[i].objValue());
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            if (i < args.length - 1)
                builder.append(" ");
        }

        return builder.toString();
    }
}
