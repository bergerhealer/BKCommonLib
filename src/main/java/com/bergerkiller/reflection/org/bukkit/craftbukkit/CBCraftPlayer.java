package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class CBCraftPlayer {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("entity.CraftPlayer");
    public static final MethodAccessor<Void> setFirstPlayed = T.getMethod("setFirstPlayed", long.class);
    public static final FieldAccessor<Boolean> hasPlayedBefore = T.getField("hasPlayedBefore");
}
