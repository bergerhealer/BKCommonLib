package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class FloatAbstract {

    /**
     * Gets the value
     *
     * @return value
     */
    public abstract float get();

    /**
     * Sets the value
     *
     * @param value to set to
     * @return this instance
     */
    public abstract FloatAbstract set(float value);

    public float squared() {
        final float value = get();
        return value * value;
    }

    public float abs() {
        return Math.abs(get());
    }

    /**
     * Gets the block coordinate of this value (floor)
     *
     * @return Block coordinate
     */
    public int block() {
        return getFloor();
    }

    /**
     * Sets the value to 0.0
     *
     * @return this instance
     */
    public FloatAbstract setZero() {
        return set(0.0f);
    }

    public int getFloor() {
        return MathUtil.floor(get());
    }

    public int chunk() {
        return MathUtil.toChunk(get());
    }

    public FloatAbstract clamp(float limit) {
        return set(getClamped(limit));
    }

    public FloatAbstract clamp(float min, float max) {
        return set(getClamped(min, max));
    }

    public float getClamped(float limit) {
        return MathUtil.clamp(get(), limit);
    }

    public float getClamped(float min, float max) {
        return MathUtil.clamp(get(), min, max);
    }

    public FloatAbstract add(float value) {
        return set(get() + value);
    }

    public FloatAbstract subtract(float value) {
        return set(get() - value);
    }

    public FloatAbstract multiply(float value) {
        return set(get() * value);
    }

    public FloatAbstract divide(float value) {
        return set(get() / value);
    }

    public FloatAbstract fixNaN() {
        return set(MathUtil.fixNaN(get()));
    }

    public FloatAbstract fixNaN(float def) {
        return set(MathUtil.fixNaN(get(), def));
    }

    public boolean equals(float value) {
        return get() == value;
    }

    @Override
    public boolean equals(Object value) {
        if (value instanceof Number) {
            return equals(((Number) value).doubleValue());
        } else if (value instanceof FloatAbstract) {
            return equals(((FloatAbstract) value).get());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Float.toString(get());
    }
}
