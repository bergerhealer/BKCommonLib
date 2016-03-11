package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_9_R1.NBTReadLimiter;

public class NBTReadLimiterUnlimited extends NBTReadLimiter {

    public NBTReadLimiterUnlimited(long index) {
        super(index);
    }

    @Override
    public void a(long length) {
    }
}
