package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import org.bukkit.entity.HumanEntity;

/**
 * Deprecated: use {@link EntityHumanHandle} instead
 */
@Deprecated
public class NMSEntityHuman extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.create(EntityHumanHandle.T.getType());

    public static final FieldAccessor<Object> inventory = EntityHumanHandle.T.inventoryRaw.toFieldAccessor();
    public static final FieldAccessor<Object> enderChest = EntityHumanHandle.T.enderChestRaw.toFieldAccessor();

    public static final FieldAccessor<Object> foodData = EntityHumanHandle.T.foodDataRaw.toFieldAccessor();

    public static final FieldAccessor<Integer> sleepTicks = EntityHumanHandle.T.sleepTicks.toFieldAccessor();

    public static final TranslatorFieldAccessor<PlayerAbilities> abilities = EntityHumanHandle.T.abilities.toFieldAccessor();
    public static final FieldAccessor<Integer> expLevel = EntityHumanHandle.T.expLevel.toFieldAccessor();
    public static final FieldAccessor<Integer> expTotal = EntityHumanHandle.T.expTotal.toFieldAccessor();
    public static final FieldAccessor<Float> exp = EntityHumanHandle.T.exp.toFieldAccessor();

    public static final FieldAccessor<GameProfileHandle> gameProfile = EntityHumanHandle.T.gameProfile.toFieldAccessor();

    public static boolean canInstaBuild(HumanEntity human) {
        return abilities.get(HandleConversion.toEntityHandle(human)).canInstantlyBuild();
    }
}
