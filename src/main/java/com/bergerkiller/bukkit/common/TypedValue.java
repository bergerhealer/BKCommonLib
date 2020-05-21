package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * Stores a Class Type and a value for type-safe generics
 *
 * @param <T> - type of value
 */
public class TypedValue<T> {

    public final Class<T> type;
    public T value;

    @SuppressWarnings("unchecked")
    public TypedValue(T value) {
        this((Class<T>) value.getClass(), value);
    }

    public TypedValue(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Sets the value using user text input
     *
     * @param text to set to
     */
    public void parseSet(String text) {
        if (this.type == boolean.class || this.type == Boolean.class) {
            value = CommonUtil.unsafeCast(ParseUtil.parseBool(text));
        } else {
            value = Conversion.convert(text, type, value);
        }
    }

    @Override
    public String toString() {
        if (this.type == boolean.class || this.type == Boolean.class) {
            return (this.value == Boolean.TRUE) ? "true" : "false";
        } else {
            return Conversion.toString.convert(value, "null");
        }
    }
}
