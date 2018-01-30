package com.alientome.script;

import com.alientome.script.functions.ArithmeticFunction;
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

        temp.put("print", (VoidFunction) (args, c) -> System.out.println(asString(args)));

        ArithmeticFunction min = new ArithmeticFunction() {
            @Override
            public Value apply(int[] args, Object c) {
                return new IntegerValue(Math.min(args[0], args[1]));
            }

            @Override
            public Value apply(double[] args, Object c) {
                return new DoubleValue(Math.min(args[0], args[1]));
            }
        };

        temp.put("min", min);

        ArithmeticFunction max = new ArithmeticFunction() {
            @Override
            public Value apply(int[] args, Object c) {
                return new IntegerValue(Math.max(args[0], args[1]));
            }

            @Override
            public Value apply(double[] args, Object c) {
                return new DoubleValue(Math.max(args[0], args[1]));
            }
        };

        temp.put("max", max);

        bindings = Collections.unmodifiableMap(temp);
    }

    private static String asString(Object[] args) {

        StringBuilder builder = new StringBuilder(args.length * 10);

        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1)
                builder.append(" ");
        }

        return builder.toString();
    }
}
