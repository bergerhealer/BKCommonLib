package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;

public class SoundEffect {
    private final MinecraftKeyHandle name;

    public SoundEffect(MinecraftKeyHandle name) {
        this.name = name;
    }

    public MinecraftKeyHandle getKey() {
        return this.name;
    }

}
