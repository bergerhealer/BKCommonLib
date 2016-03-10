package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import net.minecraft.server.v1_9_R1.WorldSettings.EnumGamemode;

public class EnumGamemodeRef {
    public static final ClassTemplate<?> TEMPLATE = new ClassTemplate(EnumGamemode.class);
    public static final FieldAccessor<Integer> egmId = TEMPLATE.getField("f");
    public static final MethodAccessor<Object> getFromId = TEMPLATE.getMethod("getById", int.class);
}
