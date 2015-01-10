package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.NBTTagCompound;

public class CommonDisabledEntity extends Entity {

    public static final CommonDisabledEntity INSTANCE = ClassTemplate.create(CommonDisabledEntity.class).newInstanceNull();

    private CommonDisabledEntity() {
        super(null);
    }

    @Override
    protected void a(NBTTagCompound arg0) {
    }

    @Override
    protected void b(NBTTagCompound arg0) {
    }

    @Override
    protected void h() {
    }
}
