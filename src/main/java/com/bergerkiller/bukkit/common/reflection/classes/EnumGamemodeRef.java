package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class EnumGamemodeRef {
    public static final ClassTemplate<?> TEMPLATE = new ClassTemplate(EnumGamemode.class);
    public static final FieldAccessor<Integer> egmId = TEMPLATE.getField("f");
    public static final MethodAccessor<Object> getFromId = TEMPLATE.getMethod("getById", int.class);
}
