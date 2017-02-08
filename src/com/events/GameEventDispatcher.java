package com.events;

import com.util.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameEventDispatcher {

    private static final GameEventDispatcher ourInstance = new GameEventDispatcher();
    private static final Logger log = Logger.get();
    private final BlockingQueue<GameEvent> eventsQueue = new LinkedBlockingQueue<>();
    private final Map<GameEventType, List<GameEventListener>> typeListenersMap = new HashMap<>();
    private boolean doDispatch = true;

    public GameEventDispatcher() {

        new Thread(() -> {

            while (doDispatch) {
                try {
                    GameEvent e = eventsQueue.take();

                    dispatch(e);

                } catch (Exception e) {
                    log.e("Uncaught exception :");
                    e.printStackTrace();
                }
            }
        }, "Thread-Dispatcher").start();
    }

    public static GameEventDispatcher getInstance() {
        return ourInstance;
    }

    public void register(GameEventType type, GameEventListener listener) {

        synchronized (typeListenersMap) {
            List<GameEventListener> list = typeListenersMap.computeIfAbsent(type, k -> new ArrayList<>());

            list.add(listener);
        }
    }

    public void unregister(GameEventType type, GameEventListener listener) {

        synchronized (typeListenersMap) {
            List<GameEventListener> list = typeListenersMap.get(type);

            if (list == null || !list.remove(listener))
                throw new IllegalArgumentException("Trying to unregister a non-registered listener");
        }
    }

    public void unregister(GameEventListener listener) {

        synchronized (typeListenersMap) {

            for (List<GameEventListener> listeners : typeListenersMap.values())
                if (listeners.remove(listener)) return;
        }

        throw new IllegalArgumentException("Trying to unregister a non-registered listener");
    }

    public void submit(Object source, GameEventType type) {

        submit(new GameEvent(source, type));
    }

    public void submit(GameEvent e) {

        Objects.requireNonNull(e, "Event cannot be null");

        eventsQueue.add(e);
    }

    private void dispatch(GameEvent e) {

        Objects.requireNonNull(e, "Event cannot be null");

        System.out.println(e.type);

        synchronized (typeListenersMap) {

            List<GameEventListener> list = typeListenersMap.get(e.type);

            if (list != null)
                list.forEach(listener -> listener.executeEvent(e));
        }
    }
}
