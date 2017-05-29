package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.mojang.authlib.GameProfile;
import org.bukkit.entity.HumanEntity;

public class NMSEntityHuman extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityHuman");

    public static final FieldAccessor<Object> inventory = EntityHumanHandle.T.inventoryRaw.toFieldAccessor();
    public static final FieldAccessor<Object> enderChest = EntityHumanHandle.T.enderChestRaw.toFieldAccessor();

    public static final FieldAccessor<Object> foodData = EntityHumanHandle.T.foodDataRaw.toFieldAccessor();

    public static final FieldAccessor<Boolean> sleeping = EntityHumanHandle.T.sleeping.toFieldAccessor();
    public static final FieldAccessor<IntVector3> bedPosition = EntityHumanHandle.T.bedPosition.toFieldAccessor();
    public static final FieldAccessor<Integer> sleepTicks = EntityHumanHandle.T.sleepTicks.toFieldAccessor();

    public static final TranslatorFieldAccessor<IntVector3> spawnCoord = EntityHumanHandle.T.spawnCoord.toFieldAccessor();
    public static final FieldAccessor<Boolean> spawnForced = EntityHumanHandle.T.spawnForced.toFieldAccessor();

    public static final TranslatorFieldAccessor<PlayerAbilities> abilities = EntityHumanHandle.T.abilities.toFieldAccessor();
    public static final FieldAccessor<Integer> expLevel = EntityHumanHandle.T.expLevel.toFieldAccessor();
    public static final FieldAccessor<Integer> expTotal = EntityHumanHandle.T.expTotal.toFieldAccessor();
    public static final FieldAccessor<Float> exp = EntityHumanHandle.T.exp.toFieldAccessor();

    public static final FieldAccessor<GameProfile> gameProfile = EntityHumanHandle.T.gameProfile.toFieldAccessor();

    public static final FieldAccessor<String> spawnWorld = EntityHumanHandle.T.spawnWorld.toFieldAccessor();

    public static boolean canInstaBuild(HumanEntity human) {
        return abilities.get(Conversion.toEntityHandle.convert(human)).canInstantlyBuild();
    }
}
