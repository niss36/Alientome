package com.alientome.core.keybindings;

@FunctionalInterface
public interface InputListener {

    boolean consumeEvent(MappedKeyEvent e);
}
