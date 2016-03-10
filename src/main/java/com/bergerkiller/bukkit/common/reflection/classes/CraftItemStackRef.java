package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;

public class CraftItemStackRef {

    public static final FieldAccessor<Object> handle = new SafeField<Object>(CraftItemStack.class, "handle");
}
