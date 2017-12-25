package com.alientome.impl;

import com.alientome.core.events.*;

import java.util.*;

public class DefaultGameEventDispatcher extends GameEventDispatcher {

    protected final Map<GameEventType, List<GameEventListener>> listenersMap = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void submitAndWait(GameEvent e) throws InterruptedException {

        Objects.requireNonNull(e, "Event cannot be null !");

        if (isDispatchThread())
            dispatch(e);
        else {
            synchronized (e) {
                eventsQueue.add(e);
                e.wait();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(GameEventType type, GameEventListener listener) {

        synchronized (listenersMap) {
            List<GameEventListener> list = listenersMap.computeIfAbsent(type, k -> new ArrayList<>());

            list.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(GameEventType type, GameEventListener listener) {

        synchronized (listenersMap) {
            List<GameEventListener> list = listenersMap.get(type);

            if (list == null || !list.remove(listener))
                throw new IllegalArgumentException("Trying to unregister a non-registered listener");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(GameEventListener listener) {

        boolean removed = false;

        synchronized (listenersMap) {

            for (List<GameEventListener> listeners : listenersMap.values())
                if (listeners.remove(listener)) removed = true;
        }

        if (!removed)
            throw new IllegalArgumentException("Trying to unregister a non-registered listener");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatch(GameEvent e) {

        System.out.println(e.type);

        synchronized (listenersMap) {

            List<GameEventListener> list = listenersMap.get(e.type);

            if (list != null)
                list.forEach(listener -> listener.executeEvent(e));
        }

        if (e instanceof QuitRequestEvent && !((QuitRequestEvent) e).isCancelled())
            dispatch(new QuitEvent());

        synchronized (e) {
            e.notify();
        }
    }
}
