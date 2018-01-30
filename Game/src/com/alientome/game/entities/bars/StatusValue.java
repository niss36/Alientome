package com.alientome.game.entities.bars;

@FunctionalInterface
public interface StatusValue {

    float percentValue();

    default float percentValue(double interpolation) {
        return percentValue();
    }

    default StatusValue reversed() {
        return new StatusValue() {
            @Override
            public float percentValue() {
                return 1 - StatusValue.this.percentValue();
            }

            @Override
            public float percentValue(double interpolation) {
                return 1 - StatusValue.this.percentValue(interpolation);
            }
        };
    }
}
