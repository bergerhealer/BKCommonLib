package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class PlayerAbilitiesRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerAbilities");
	public static final FieldAccessor<Boolean> isInvulnerable = TEMPLATE.getField("isInvulnerable");
	public static final FieldAccessor<Boolean> isFlying = TEMPLATE.getField("isFlying");
	public static final FieldAccessor<Boolean> canFly = TEMPLATE.getField("canFly");
	public static final FieldAccessor<Boolean> canInstantlyBuild = TEMPLATE.getField("canInstantlyBuild");
	public static final FieldAccessor<Boolean> mayBuild = TEMPLATE.getField("mayBuild");
	public static final FieldAccessor<Float> flySpeed = TEMPLATE.getField("flySpeed");
	public static final FieldAccessor<Float> walkSpeed = TEMPLATE.getField("walkSpeed");
}
