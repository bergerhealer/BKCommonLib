package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class CBCraftPlayer {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.entity.CraftPlayer");
    public static final MethodAccessor<Void> setFirstPlayed = T.getMethod("setFirstPlayed", long.class);
    public static final FieldAccessor<Boolean> hasPlayedBefore = T.getField("hasPlayedBefore");
}
