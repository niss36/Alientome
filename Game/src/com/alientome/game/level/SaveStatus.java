package com.alientome.game.level;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;

import static com.alientome.game.level.SaveManager.*;

public class SaveStatus implements ObservableValue<Number> {

    private final SaveManager manager;
    private final int index;
    private final IntegerProperty value = new SimpleIntegerProperty(UNINITIALISED_SAVE);

    public SaveStatus(SaveManager manager, int index) {
        this.manager = manager;
        this.index = index;
    }

    public int get() {
        return value.get();
    }

    public void set(int newValue) throws IOException {

        if (newValue <= UNINITIALISED_SAVE)
            // Can't set to special values (uninitialised and empty) or other non-positive integers
            throw new IllegalArgumentException("Illegal level ID:" + newValue);

        int oldValue = value.get();

        if (newValue != oldValue) {

            if (oldValue != UNINITIALISED_SAVE)
                // Don't save if it's the first time the value is set
                manager.save(index, newValue);

            value.set(newValue);
        }
    }

    public void delete() throws IOException {
        manager.delete(index);
        value.set(EMPTY_SAVE);
    }

    public void actualise() throws IOException {
        value.set(manager.read(index));
    }

    public boolean isNotEmpty() {
        return get() > UNINITIALISED_SAVE;
    }

    public BooleanBinding notEmptyBinding() {
        return value.greaterThan(UNINITIALISED_SAVE);
    }

    public void setToFirst() throws IOException {
        set(FIRST_LEVEL_ID);
    }

    // ------------------------ ObservableValue stuff --------------------------------

    @Override
    public void addListener(ChangeListener<? super Number> listener) {
        value.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
        value.removeListener(listener);
    }

    @Override
    public Number getValue() {
        return value.getValue();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        value.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        value.removeListener(listener);
    }
}
