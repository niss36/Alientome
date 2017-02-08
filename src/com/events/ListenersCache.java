package com.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenersCache {

    private static final Map<Object, List<GameEventListener>> cache = new HashMap<>();

    public static void register(Object object, GameEventListener listener) {

        List<GameEventListener> listeners = cache.computeIfAbsent(object, o -> new ArrayList<>());
        listeners.add(listener);
    }

    public static void unregister(Object object) {

        List<GameEventListener> listeners = cache.remove(object);
        if (listeners == null)
            throw new IllegalArgumentException("Trying to unregister a non-registered object");

        for (GameEventListener listener : listeners) {

            GameEventDispatcher.getInstance().unregister(listener);
        }
    }
}
