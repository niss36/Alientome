package com.alientome.script.arithmetic;

import java.util.HashMap;
import java.util.Map;

public class Operations {

    private static final Map<String, ArithmeticOperation> operations = new HashMap<>();

    public static final ArithmeticOperation ADD = new ArithmeticOperation() {
        @Override
        public int apply(int a, int b) {
            return a + b;
        }

        @Override
        public double apply(double a, double b) {
            return a + b;
        }
    };

    public static final ArithmeticOperation SUBTRACT = new ArithmeticOperation() {
        @Override
        public int apply(int a, int b) {
            return a - b;
        }

        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    };

    public static final ArithmeticOperation MULTIPLY = new ArithmeticOperation() {
        @Override
        public int apply(int a, int b) {
            return a * b;
        }

        @Override
        public double apply(double a, double b) {
            return a * b;
        }
    };

    public static final ArithmeticOperation DIVIDE = new ArithmeticOperation() {
        @Override
        public int apply(int a, int b) {
            return a / b;
        }

        @Override
        public double apply(double a, double b) {
            return a / b;
        }
    };

    static {
        operations.put("+", ADD);
        operations.put("-", SUBTRACT);
        operations.put("*", MULTIPLY);
        operations.put("/", DIVIDE);
    }

    public static ArithmeticOperation get(String s) {
        return operations.get(s);
    }
}
