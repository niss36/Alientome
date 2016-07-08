package com.util;

public class Couple<T> {

    private final T obj1;
    private final T obj2;

    public Couple(T obj1, T obj2) {

        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Couple) {

            try {
                @SuppressWarnings("unchecked")
                Couple<T> c = (Couple<T>) obj;

                return (c.obj1.equals(obj1) && c.obj2.equals(obj2)) || (c.obj2.equals(obj1) && c.obj1.equals(obj2));

            } catch (ClassCastException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
