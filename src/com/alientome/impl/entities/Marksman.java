package com.alientome.impl.entities;

import com.alientome.game.entities.Entity;

public interface Marksman {

    Entity getEntity();

    void startCharging();

    void stopCharging();

    void shoot();
}
