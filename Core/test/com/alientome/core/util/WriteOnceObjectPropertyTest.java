package com.alientome.core.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WriteOnceObjectPropertyTest {

    // Only test the constructors for null initial value.

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor1NullThrows() {

        thrown.expect(NullPointerException.class);
        new WriteOnceObjectProperty<>(null);
    }

    @Test
    public void testConstructor2NullThrows() {

        thrown.expect(NullPointerException.class);
        new WriteOnceObjectProperty<>(new Object(), "", null);
    }

    @Test
    public void testFirstSetNoThrows() {

        WriteOnceObjectProperty<Object> property = new WriteOnceObjectProperty<>();

        property.set(new Object());
    }

    @Test
    public void testSubsequentSetThrows() {

        WriteOnceObjectProperty<Object> property = new WriteOnceObjectProperty<>(new Object());
        thrown.expect(IllegalStateException.class);

        property.set(new Object());
    }

    @Test
    public void testSetNullThrows() {

        WriteOnceObjectProperty<Object> property = new WriteOnceObjectProperty<>();
        thrown.expect(NullPointerException.class);

        property.set(null);
    }
}
