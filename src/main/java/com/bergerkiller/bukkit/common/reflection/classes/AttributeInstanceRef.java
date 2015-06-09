package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

/**
 * Created by Matthijs on 9-6-2015.
 */
public class AttributeInstanceRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("AttributeInstance");
    public static final MethodAccessor<Double> setValue = TEMPLATE.getMethod("setValue", double.class);
}
