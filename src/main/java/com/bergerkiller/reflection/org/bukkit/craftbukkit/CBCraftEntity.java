package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import org.bukkit.entity.Entity;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class CBCraftEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("entity.CraftEntity");
    public static final MethodAccessor<Entity> getEntity = T.selectMethod(
    		"public static CraftEntity getEntity(org.bukkit.craftbukkit.CraftServer server," +
    		                                    "net.minecraft.server.Entity entity)");
}
