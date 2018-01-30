package com.alientome.script;

import com.alientome.script.arithmetic.ArithmeticExpression;
import com.alientome.script.arithmetic.ArithmeticOperation;
import com.alientome.script.arithmetic.ArithmeticValue;
import com.alientome.script.arithmetic.Operations;
import com.alientome.script.values.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private static final Pattern lineEnd = Pattern.compile("\\s*\n");
    private static final Pattern expression = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)\\s*\\((.*)\\)$");
    private static final Pattern argument = Pattern.compile("(true|false)|(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)|((?:\"[^\"]*\")|(?:\'[^']*\'))|([a-zA-Z][a-zA-Z0-9]*\\s*\\(.*\\))|\\(\\s*(.+[+\\-*/].+)\\s*\\)|\\((.+)\\)");
    private static final Pattern argumentsValidator = Pattern.compile("\\s*((" + argument.pattern() + ")\\s*,\\s*)*(" + argument.pattern() + ")\\s*$");
    private static final Pattern argumentsMatcher = Pattern.compile("(?:" + argument.pattern() + ")");
    private static final Pattern arithmeticArgument = Pattern.compile("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)|([a-zA-Z][a-zA-Z0-9]*\\s*\\(.*\\))|\\(\\s*(.+[+\\-*/].+)\\s*\\)|\\((.+)\\)");
    private static final Pattern arithmeticMatcher = Pattern.compile("(?:\\s*(.+)\\s*([+\\-*/])\\s*(.+)\\s*)");

    private final ScriptEngine engine;
    private final ArithmeticValueCreator[] arithmeticCreators = {
            (script, s) -> new IntegerValue(Integer.valueOf(s)),
            (script, s) -> new DoubleValue(Double.valueOf(s)),
            this::parseExpression,
            this::parseArithmetic,
            this::parseArithmeticValue};
    private final ValueCreator[] creators = {
            (script, s) -> new BooleanValue(Boolean.valueOf(s)),
            (script, s) -> new IntegerValue(Integer.valueOf(s)),
            (script, s) -> new DoubleValue(Double.valueOf(s)),
            (script, s) -> new StringValue(s),
            this::parseExpression,
            this::parseArithmetic,
            this::parseValue};

    protected ScriptParser(ScriptEngine engine) {
        this.engine = engine;
    }

    public Script parseScript(String s) throws ScriptException {

        String[] lines = lineEnd.split(s);

        Expression[] expressions = new Expression[lines.length];

        Script script = new Script(engine, expressions);

        for (int i = 0; i < lines.length; i++) {
            String expr = lines[i];

            expressions[i] = parseExpression(script, expr);
        }

        return script;
    }

    private Expression parseExpression(Script script, String s) throws ScriptException {

        Matcher exprM = expression.matcher(s);

        if (exprM.find()) {

            String functionName = exprM.group(1);
            String args = exprM.group(2).trim();

            List<Value> arguments = new ArrayList<>();

            if (args.length() != 0) {

                if (argumentsValidator.matcher(args).matches()) {

                    Matcher argsMatcher = argumentsMatcher.matcher(args);

                    while (argsMatcher.find()) {

                        arguments.add(parseValue(script, argsMatcher));
                    }
                } else {
                    throw new ScriptException("Invalid arguments : " + args);
                }
            }

            return new Expression(script, functionName, arguments.toArray(new Value[arguments.size()]));
        }

        throw new ScriptException("Expression '" + s + "' didn't match '" + expression + "'.");
    }

    private Value parseValue(Script script, String s) throws ScriptException {

        Matcher matcher = argument.matcher(s);

        if (matcher.find())
            return parseValue(script, matcher);

        throw new IllegalArgumentException("Invalid argument : " + s);
    }

    private Value parseValue(Script script, Matcher matcher) throws ScriptException {

        for (int i = 0; i < creators.length; i++) {

            String group = matcher.group(i + 1);
            if (group != null)
                return creators[i].from(script, group);
        }

        throw new IllegalArgumentException("Invalid argument : " + matcher.group(0));
    }

    private ArithmeticExpression parseArithmetic(Script script, String expr) throws ScriptException {

        Matcher matcher = arithmeticMatcher.matcher(expr);

        if (matcher.find()) {
            String a = matcher.group(1).trim();
            String op = matcher.group(2);
            String b = matcher.group(3).trim();

            ArithmeticValue argumentA = parseArithmeticValue(script, a);
            ArithmeticValue argumentB = parseArithmeticValue(script, b);
            ArithmeticOperation operation = Operations.get(op);

            return new ArithmeticExpression(argumentA, argumentB, operation);
        }

        throw new ScriptException("Invalid arithmetic operation : " + expr);
    }

    private ArithmeticValue parseArithmeticValue(Script script, String s) throws ScriptException {

        Matcher matcher = arithmeticArgument.matcher(s);

        if (matcher.find())
            return parseArithmeticValue(script, matcher);

        throw new ScriptException("Invalid arithmetic argument : " + s);
    }

    private ArithmeticValue parseArithmeticValue(Script script, Matcher matcher) throws ScriptException {

        for (int i = 0; i < arithmeticCreators.length; i++) {

            String group = matcher.group(i + 1);
            if (group != null)
                return arithmeticCreators[i].from(script, group);
        }

        throw new ScriptException("Invalid arithmetic argument : " + matcher.group(0));
    }

    private interface ValueCreator {

        Value from(Script script, String s) throws ScriptException;
    }

    private interface ArithmeticValueCreator {

        ArithmeticValue from(Script script, String s) throws ScriptException;
    }
}
