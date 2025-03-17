package com.bergerkiller.bukkit.common.internal.proxy;

public class CustomModelData_pre_1_20_5 {
    public static final CustomModelData_pre_1_20_5 DEFAULT = new CustomModelData_pre_1_20_5(0);
    private final int value;

    public CustomModelData_pre_1_20_5(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CustomModelData_pre_1_20_5 && ((CustomModelData_pre_1_20_5) o).value == value;
    }
}
