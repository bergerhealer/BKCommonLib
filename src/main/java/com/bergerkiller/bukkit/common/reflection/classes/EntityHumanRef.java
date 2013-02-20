package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.HashMap;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.type.HandleConverter;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityHumanRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityHuman");
	public static final TranslatorFieldAccessor<IntVector3> spawnCoord = TEMPLATE.getField("c").translate(ConversionPairs.chunkCoordinates);
	public static final FieldAccessor<Boolean> spawnForced = TEMPLATE.getField("d");
	public static final FieldAccessor<String> spawnWorld = TEMPLATE.getField("spawnWorld");
	public static final FieldAccessor<Object> foodData = TEMPLATE.getField("foodData");
	public static final FieldAccessor<Float> exp = TEMPLATE.getField("exp");
	public static final FieldAccessor<Integer> expLevel = TEMPLATE.getField("expLevel");
	public static final FieldAccessor<Integer> expTotal = TEMPLATE.getField("expTotal");
	// The below two fields are actually in EntityLiving!
	public static final FieldAccessor<HashMap<Integer, Object>> mobEffects = TEMPLATE.getField("effects");
	public static final FieldAccessor<Boolean> updateEffects = TEMPLATE.getField("updateEffects");

	// Abilities (this really needs a wrapper class!)
	private static final FieldAccessor<Object> abilities = TEMPLATE.getField("abilities");
	private static final FieldAccessor<Boolean> canInstaBuild = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerAbilities"), "canInstantlyBuild");

	public static boolean canInstaBuild(HumanEntity human) {
		return canInstaBuild.get(abilities.get(HandleConverter.toEntityHandle.convert(human)));
	}
}
