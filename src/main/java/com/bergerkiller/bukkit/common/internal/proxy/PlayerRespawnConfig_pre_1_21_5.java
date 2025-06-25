package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

public final class PlayerRespawnConfig_pre_1_21_5 {
    private final Object dimension;
    private final String worldName;
    private final Object position;
    private final float angle;
    private final boolean forced;

    public PlayerRespawnConfig_pre_1_21_5(Object dimension, Object position, float angle, boolean forced) {
        this(dimension, null, position, angle, forced);
    }

    public PlayerRespawnConfig_pre_1_21_5(Object dimension, String worldName, Object position, float angle, boolean forced) {
        this.dimension = dimension;
        this.worldName = worldName;
        this.position = position;
        this.angle = angle;
        this.forced = forced;
    }

    public Object dimension() {
        return dimension;
    }

    public String worldName() {
        return worldName;
    }

    public Object pos() {
        return position;
    }

    public float angle() {
        return angle;
    }

    public boolean forced() {
        return forced;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerRespawnConfig_pre_1_21_5) {
            PlayerRespawnConfig_pre_1_21_5 other = (PlayerRespawnConfig_pre_1_21_5) o;
            return LogicUtil.bothNullOrEqual(this.dimension, other.dimension) &&
                    LogicUtil.bothNullOrEqual(this.position, other.position) &&
                    this.angle == other.angle && this.forced == other.forced;
        } else {
            return false;
        }
    }
}
