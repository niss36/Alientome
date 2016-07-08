package com.util.listeners;

import java.util.EventListener;

public interface ConfigListener extends EventListener {

    void configReset();

    void configKeysReset();
}
