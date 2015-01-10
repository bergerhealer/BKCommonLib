package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class EnumGamemodeRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EnumGamemode");
    public static final FieldAccessor<Integer> egmId = TEMPLATE.getField("e");
    public static final MethodAccessor<Object> getFromId = TEMPLATE.getMethod("getById", int.class);
}
