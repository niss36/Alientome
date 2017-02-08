package com.util.listeners;

import com.keybindings.MappedKeyEvent;

import java.util.EventListener;

public interface InputListener extends EventListener {

    boolean consumeEvent(MappedKeyEvent e);
}
