package com.alientome.script;

import com.alientome.script.arithmetic.ArithmeticValue;
import com.alientome.script.functions.ScriptFunction;
import com.alientome.script.functions.VoidFunction;
import com.alientome.script.values.Expression;
import com.alientome.script.values.Value;

import java.util.HashMap;
import java.util.Map;

public class Script {

    private final Map<String, ScriptFunction> bindings = new HashMap<>();
    private final ScriptEngine engine;
    private final Expression[] expressions;
    private boolean defaultEnabled;
    private boolean enabled = true;
    private int delay = 0;
    private int delayedLn = -1;
    private Object context;

    public Script(ScriptEngine engine, Expression[] expressions) {
        this.engine = engine;
        this.expressions = expressions;

        bindings.put("disable", (VoidFunction) (args, c) -> enabled = false);

        bindings.put("delay", (VoidFunction) (args, c) -> delay = ((ArithmeticValue) args[0]).numValue().intValue());
    }

    public Value call(String functionName, Value[] arguments) throws ScriptException {
        ScriptFunction function = bindings.get(functionName);
        if (function != null)
            return function.apply(arguments, context);
        return engine.call(functionName, arguments, context);
    }

    public void setDefaultEnabled(boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void reset() {
        enabled = defaultEnabled;
        delay = 0;
        delayedLn = -1;
        context = null;
    }

    public void update() throws ScriptException {

        if (delay > 0)
            delay--;
        else if (delayedLn != -1) {
            try {
                for (int i = delayedLn; i < expressions.length; i++) {
                    expressions[i].execute();
                    if (delay > 0) {
                        delayedLn = i + 1;
                        return;
                    }
                }
                delayedLn = -1;
            } catch (ScriptException e) {
                throw e;
            } catch (Throwable t) {
                throw new ScriptException("Uncaught exception in script", t);
            } finally {
                context = null;
            }
        }
    }

    public void run(Object context) throws ScriptException {

        if (!enabled || delayedLn != -1)
            return;

        try {
            this.context = context;

            for (int i = 0; i < expressions.length; i++) {
                expressions[i].execute();
                if (delay > 0) {
                    delayedLn = i + 1;
                    return;
                }
            }
        } catch (ScriptException e) {
            throw e;
        } catch (Throwable t) {
            throw new ScriptException("Uncaught exception in script", t);
        } finally {
            this.context = null;
        }
    }
}
