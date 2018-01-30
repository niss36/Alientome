package com.alientome.core.util;

@FunctionalInterface
public interface ArrayCreator<T> {

    T[] create(int size);
}
