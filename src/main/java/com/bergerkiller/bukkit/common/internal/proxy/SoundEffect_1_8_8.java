package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;

public class SoundEffect_1_8_8 {
    private final IdentifierHandle name;

    public SoundEffect_1_8_8(IdentifierHandle name) {
        this.name = name;
    }

    public IdentifierHandle getKey() {
        return this.name;
    }

    public static SoundEffect_1_8_8 createVariableRangeEvent(Object minecraftKey) {
        return new SoundEffect_1_8_8(IdentifierHandle.createHandle(minecraftKey));
    }
}
