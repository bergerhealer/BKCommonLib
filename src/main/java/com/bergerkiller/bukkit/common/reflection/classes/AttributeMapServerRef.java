package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import net.minecraft.server.v1_8_R3.AttributeMapServer;

public class AttributeMapServerRef extends AttributeMapServer {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("AttributeMapServer");
    public static final MethodAccessor<AttributeInstanceRef> a = TEMPLATE.getMethod("a", GenericAtrributesRef.class);
    public static final MethodAccessor<AttributeInstanceRef> b = TEMPLATE.getMethod("b", GenericAtrributesRef.class);
}