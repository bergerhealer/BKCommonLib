package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class LongAbstract {

    /**
     * Gets the value
     *
     * @return value
     */
    public abstract long get();

    /**
     * Sets the value
     *
     * @param value to set to
     * @return this instance
     */
    public abstract LongAbstract set(long value);

    public long squared() {
        final long value = get();
        return value * value;
    }

    public long abs() {
        return Math.abs(get());
    }

    /**
     * Sets the value to 0.0
     *
     * @return this instance
     */
    public LongAbstract setZero() {
        return set(0L);
    }

    public LongAbstract clamp(long limit) {
        return set(getClamped(limit));
    }

    public LongAbstract clamp(long min, long max) {
        return set(getClamped(min, max));
    }

    public long getClamped(long limit) {
        return MathUtil.clamp(get(), limit);
    }

    public long getClamped(long min, long max) {
        return MathUtil.clamp(get(), min, max);
    }

    public LongAbstract add(long value) {
        return set(get() + value);
    }

    public LongAbstract subtract(long value) {
        return set(get() - value);
    }

    public LongAbstract multiply(long value) {
        return set(get() * value);
    }

    public LongAbstract divide(long value) {
        return set(get() / value);
    }

    /**
     * Checks whether the modulus of the current value equals 0. This is the
     * case when the current value is dividable by the modulus specified, and no
     * remainder is left. For example:<br>
     * - {12}.isMod(6) == True<br>
     * - {11}.isMod(2) == False<br>
     * - {0}.isMod(6) == True<br><br>
     * <p/>
     * This functionality can be used to perform something on an interval using
     * a given time. For example, {timesecond}.isMod(60) would trigger whenever
     * a new minute is passed.
     *
     * @param modulus to check
     * @return True if dividable without remainder, False if not
     */
    public boolean isMod(long modulus) {
        return (get() % modulus) == 0;
    }

    public boolean equals(long value) {
        return get() == value;
    }

    @Override
    public boolean equals(Object value) {
        if (value instanceof Number) {
            return equals(((Number) value).intValue());
        } else if (value instanceof LongAbstract) {
            return equals(((LongAbstract) value).get());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Long.toString(get());
    }
}
