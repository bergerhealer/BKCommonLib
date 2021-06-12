package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;

public class SoundEffect_1_8_8 {
    private final MinecraftKeyHandle name;

    public SoundEffect_1_8_8(MinecraftKeyHandle name) {
        this.name = name;
    }

    public MinecraftKeyHandle getKey() {
        return this.name;
    }

}
