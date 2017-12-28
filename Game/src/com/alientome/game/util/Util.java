package com.alientome.game.util;

import com.alientome.core.collisions.AxisAlignedBoundingBox;
import com.alientome.core.util.Direction;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.entities.Entity;
import com.alientome.game.registry.Registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class Util {

    public static <T> T create(Registry<Class<? extends T>> registry, String id, Class<?>[] constructorTypes, Object[] args) {

        Class<? extends T> tClass = registry.get(id);

        if (tClass == null)
            throw new IllegalArgumentException("Unregistered ID : " + id);

        if (Modifier.isAbstract(tClass.getModifiers()))
            throw new IllegalArgumentException("id '" + id + "' denotes an abstract type and cannot be instantiated.");

        try {
            Constructor<? extends T> constructor = tClass.getDeclaredConstructor(constructorTypes);

            return constructor.newInstance(args);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Class " + tClass.getName() + " does not correctly implement expected constructor", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static AxisAlignedBoundingBox getNextFrontBoundingBox(Entity entity, double width, double heightMod) {

        AxisAlignedBoundingBox next = entity.getNextBoundingBox();

        double x1, x2;

        if (entity.facing == Direction.LEFT) {
            x1 = next.getMinX() - width;
            x2 = next.getMinX();
        } else {
            x1 = next.getMaxX();
            x2 = next.getMaxX() + width;
        }

        return new StaticBoundingBox(x1, next.getMinY() - heightMod, x2, next.getMaxY() + heightMod);
    }
}
