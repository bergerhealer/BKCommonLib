package com.bergerkiller.bukkit.common.internal.proxy;

public class MobEffectList {
    private final Integer id;

    public MobEffectList(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static int getId(MobEffectList mobeffectlist) {
        return mobeffectlist.getId().intValue();
    }

    public static MobEffectList fromId(int id) {
        return new MobEffectList(Integer.valueOf(id));
    }
}
