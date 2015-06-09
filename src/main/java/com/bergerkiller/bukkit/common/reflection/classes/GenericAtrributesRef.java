package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import net.minecraft.server.v1_8_R3.IAttribute;

public class GenericAtrributesRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("GenericAttributes");
    public static final FieldAccessor<IAttribute> maxHealth = TEMPLATE.getField("maxHealth");
    public static final FieldAccessor<IAttribute> FOLLOW_RANGE = TEMPLATE.getField("FOLLOW_RANGE");
    public static final FieldAccessor<IAttribute> c = TEMPLATE.getField("c");
    public static final FieldAccessor<IAttribute> MOVEMENT_SPEED = TEMPLATE.getField("MOVEMENT_SPEED");
    public static final FieldAccessor<IAttribute> ATTACKT_DAMAGE = TEMPLATE.getField("ATTACK_DAMAGE");
}
