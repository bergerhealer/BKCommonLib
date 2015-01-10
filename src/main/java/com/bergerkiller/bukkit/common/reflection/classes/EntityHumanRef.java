package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.HashMap;

import com.mojang.authlib.GameProfile;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.type.HandleConverter;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

public class EntityHumanRef extends EntityLivingRef {

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
    public static final TranslatorFieldAccessor<PlayerAbilities> abilities = TEMPLATE.getField("abilities").translate(ConversionPairs.playerAbilities);
    public static final FieldAccessor<GameProfile> gameProfile = TEMPLATE.getField("i");

    public static boolean canInstaBuild(HumanEntity human) {
        return abilities.get(HandleConverter.toEntityHandle.convert(human)).canInstantlyBuild();
    }
}
