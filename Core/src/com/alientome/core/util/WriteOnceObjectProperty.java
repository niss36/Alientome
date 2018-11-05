package com.alientome.core.util;

import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

public class WriteOnceObjectProperty<T> extends SimpleObjectProperty<T> {

    public WriteOnceObjectProperty() {
        super();
    }

    public WriteOnceObjectProperty(T initialValue) {
        super(Objects.requireNonNull(initialValue));
    }

    public WriteOnceObjectProperty(Object bean, String name) {
        super(bean, name);
    }

    public WriteOnceObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, Objects.requireNonNull(initialValue));
    }

    @Override
    public final void set(T newValue) {

        if (get() == null)
            super.set(Objects.requireNonNull(newValue));
        else
            throw new IllegalStateException("This value has already been set.");
    }
}
