package com.alientome.script.arithmetic;

import com.alientome.script.ScriptException;
import com.alientome.script.values.Value;

public interface ArithmeticValue extends Value {

    Number numValue() throws ScriptException;

    boolean isInt();
}
