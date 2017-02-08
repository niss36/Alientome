package com.util.listeners;

import java.util.EventListener;

public interface BooleanValueListener extends EventListener {

    void valueChanged(boolean newValue);
}
