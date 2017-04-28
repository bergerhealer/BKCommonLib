package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

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
	public MinecraftServer B_() {
		return null;
	}

	@Override
	protected void i() {
	}
}
