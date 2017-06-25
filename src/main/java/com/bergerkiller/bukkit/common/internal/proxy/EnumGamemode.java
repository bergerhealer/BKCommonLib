package com.bergerkiller.bukkit.common.internal.proxy;

public enum EnumGamemode {
    NOT_SET(-1), SURVIVAL(0), CREATIVE(1), ADVENTURE(2), SPECTATOR(3);

    int f; // id

    private EnumGamemode(int id) {
        this.f = id;
    }

    public static EnumGamemode getById(int id) {
        for (EnumGamemode mode : values()) {
            if (mode.f == id) {
                return mode;
            }
        }
        return SURVIVAL;
    }
}
