package com.alientome.core.settings;

import javafx.beans.property.Property;

public interface PropertyProvider<T> {

    Property<T> parse(Property<T> current, String s);
}
