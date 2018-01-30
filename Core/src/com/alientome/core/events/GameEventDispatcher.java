package com.alientome.core.events;

import com.alientome.core.util.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A GameEventDispatcher is a centralized message exchange system that links different parts of the program together.
 * GameEvents can be submitted to it in order to be dispatched asynchronously. GameEventListeners can be attached in order
 * to react to specific events.
 *
 * @see GameEvent
 * @see GameEventType
 * @see GameEventListener
 */
public abstract class GameEventDispatcher {

    /**
     * Logging utility.
     *
     * @see Logger
     */
    protected static final Logger log = Logger.get();

    /**
     * A Map used for storing away listeners in order to unregister them all at once when the linked object is not used anymore.
     */
    protected final Map<Object, List<GameEventListener>> listenersCache = new HashMap<>();

    /**
     * The Queue to which the GameEvents are added and from which they are retrieved asynchronously. We use a BlockingQueue
     * in order to wait until new events are available when not already the case.
     */
    protected final BlockingQueue<GameEvent> eventsQueue = new LinkedBlockingQueue<>();

    /**
     * The Thread instance on which events are dispatched.
     */
    protected final Thread dispatchThread;

    /**
     * A boolean flag indicating whether we should keep on dispatching. If set to false, the dispatch loop will end on the
     * next iteration.
     */
    protected boolean doDispatch = true;

    public GameEventDispatcher() {

        dispatchThread = new Thread(() -> {

            while (doDispatch) {

                try {
                    GameEvent e = eventsQueue.take();

                    dispatch(e);

                } catch (InterruptedException e) {
                    log.w("The dispatch thread was interrupted :");
                    e.printStackTrace();
                    doDispatch = false;
                } catch (Exception e) {
                    log.e("Uncaught exception :");
                    e.printStackTrace();
                }
            }
        }, "Thread-Dispatcher");

        dispatchThread.setDaemon(true);
        dispatchThread.start();
    }

    /**
     * Adds the given listener to the object's associated listener cache. This is used to unregister all listeners
     * linked to the object in order to properly dispose from it and avoid memory leaks.
     * @param object the object the listener is associated to. This should be the object that instantiated the listener.
     * @param listener the listener to cache.
     * @see #removeFromCache(Object)
     */
    public void addToCache(Object object, GameEventListener listener) {

        List<GameEventListener> listeners = listenersCache.computeIfAbsent(object, o -> new ArrayList<>());

        listeners.add(listener);
    }

    /**
     * Remove the given object from the listeners cache, and unregister all the cached listeners associated with the
     * removed object. This ensures no references to the listener or to the object persist within this dispatcher.
     * @param object the object to remove all associated listeners from.
     */
    public void removeFromCache(Object object) {

        List<GameEventListener> listeners = listenersCache.remove(object);

        if (listeners == null)
            throw new IllegalArgumentException("Trying to remove a non added object");

        for (GameEventListener listener : listeners)
            unregister(listener);
    }

    /**
     * Indicates whether the calling thread is the dispatch thread.
     *
     * @return true if the current thread is the dispatch thread.
     */
    public boolean isDispatchThread() {
        return Thread.currentThread() == dispatchThread;
    }

    /**
     * Adds the given GameEvent to the events queue. It will be dispatched asynchronously. Therefore, there is no guarantee
     * the event will have be dispatched right after this method returns. The event must be non-null. Otherwise a
     * NullPointerException is thrown.
     *
     * @param e the event to submit
     * @throws NullPointerException if e is null
     * @see #submitAndWait(GameEvent)
     */
    public void submit(GameEvent e) {

        Objects.requireNonNull(e, "Event cannot be null !");

        eventsQueue.add(e);
    }

    /**
     * Adds the given GameEvent to the events queue. It will be dispatched synchronously. That is to say that after this
     * method returns, the event will have been dispatched and all listeners will have taken appropriate action. The event
     * must be non-null, otherwise a NullPointerException is thrown.
     *
     * @param e the event to submit
     * @throws NullPointerException if e is null
     * @throws InterruptedException if the waiting thread is interrupted
     * @see #submit(GameEvent)
     */
    public abstract void submitAndWait(GameEvent e) throws InterruptedException;

    /**
     * Binds the given listener to the given type of events. The listener's {@link GameEventListener#executeEvent(GameEvent)}
     * method will be called upon dispatch of such events.
     *
     * @param type     the type of GameEvents to listen to
     * @param listener the listener to register
     * @see #unregister(GameEventType, GameEventListener)
     * @see #unregister(GameEventListener)
     */
    public abstract void register(GameEventType type, GameEventListener listener);

    /**
     * Unbinds the given listener from the given type. If the listener is not yet registered, an IllegalArgumentException
     * is to be thrown.
     *
     * @param type     the type of GameEvents the listener was attached to
     * @param listener the listener to unregister
     * @throws IllegalArgumentException if the listener wasn't registered before.
     * @see #register(GameEventType, GameEventListener)
     * @see #unregister(GameEventListener)
     */
    public abstract void unregister(GameEventType type, GameEventListener listener);

    /**
     * Unbinds the given listener. This method is to be used if the type of events the listener was attached to is unknown.
     * However, if it is known, {@link #unregister(GameEventType, GameEventListener)} should be preferred.
     *
     * @param listener the listener to unregister
     * @throws IllegalArgumentException if the listener wasn't registered before.
     * @see #register(GameEventType, GameEventListener)
     * @see #unregister(GameEventType, GameEventListener)
     */
    public abstract void unregister(GameEventListener listener);

    /**
     * This method synchronously dispatches the event, that is it takes any action associated with such dispatch.
     * It is intended for internal use and should only be called from the dispatch thread.
     * The given event is assumed to be non-null.
     *
     * @param e the event to dispatch
     */
    protected abstract void dispatch(GameEvent e);
}
