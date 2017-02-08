package com.util.listeners;

import java.util.EventListener;

public interface SaveListener extends EventListener {

    void saveChanged(int index);
}
