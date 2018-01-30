package com.alientome.core.util;

import javafx.beans.property.SimpleObjectProperty;

public class WriteOnceObjectProperty<T> extends SimpleObjectProperty<T> {

    public WriteOnceObjectProperty() {
        super();
    }

    public WriteOnceObjectProperty(T initialValue) {
        super(initialValue);
    }

    public WriteOnceObjectProperty(Object bean, String name) {
        super(bean, name);
    }

    public WriteOnceObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    public final void setValue(T v) {
        set(v);
    }

    @Override
    public final void set(T newValue) {

        if (get() == null)
            super.set(newValue);
        else
            throw new IllegalStateException("This value has already been set.");
    }
}
