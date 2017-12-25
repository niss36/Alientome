package com.alientome.editors.animations.util;

public class TreeView {

    public static final int ROOT = 0, PACKAGE = 1, CLASS = 2, ANIMATION = 3;

    private final String view;
    private final Object value;
    private final int type;

    public TreeView(String view, Object value, int type) {
        this.view = view;
        this.value = value;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return view;
    }
}
