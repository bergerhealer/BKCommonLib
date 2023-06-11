package com.bergerkiller.bukkit.common.block;

import java.util.Arrays;
import java.util.List;

/**
 * Stores two values for both sides of a sign. Makes it easier
 * to track information about both sides of a sign.
 */
public class SignSideMap<T> {
    private T front, back;

    public SignSideMap() {
    }

    public SignSideMap(T front, T back) {
        this.front = front;
        this.back = back;
    }

    public T front() {
        return front;
    }

    public T back() {
        return back;
    }

    public T side(SignSide side) {
        return side == SignSide.BACK ? back : front;
    }

    public void set(T front, T back) {
        this.front = front;
        this.back = back;
    }

    public void setSide(SignSide side, T value) {
        if (side == SignSide.BACK) {
            back = value;
        } else {
            front = value;
        }
    }

    public void setFront(T front) {
        this.front = front;
    }

    public void setBack(T back) {
        this.back = back;
    }

    public List<T> both() {
        return Arrays.asList(front, back);
    }
}
