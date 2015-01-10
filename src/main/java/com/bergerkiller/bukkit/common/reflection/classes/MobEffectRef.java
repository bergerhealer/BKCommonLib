package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class MobEffectRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("MobEffect");
    public static final FieldAccessor<Integer> effectId = TEMPLATE.getField("effectId");
    public static final FieldAccessor<Integer> duration = TEMPLATE.getField("duration");
    public static final FieldAccessor<Integer> amplification = TEMPLATE.getField("amplification");
    public static final FieldAccessor<Boolean> splash = TEMPLATE.getField("splash");
    public static final FieldAccessor<Boolean> ambient = TEMPLATE.getField("ambient");
}
