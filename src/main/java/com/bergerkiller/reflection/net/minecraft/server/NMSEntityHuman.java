package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import org.bukkit.entity.HumanEntity;

/**
 * Deprecated: use {@link PlayerHandle} instead
 */
@Deprecated
public class NMSEntityHuman extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.create(PlayerHandle.T.getType());

    public static final FieldAccessor<Object> inventory = PlayerHandle.T.inventoryRaw.toFieldAccessor();
    public static final FieldAccessor<Object> enderChest = PlayerHandle.T.enderChestRaw.toFieldAccessor();

    public static final FieldAccessor<Object> foodData = PlayerHandle.T.foodDataRaw.toFieldAccessor();

    public static final FieldAccessor<Integer> sleepTicks = PlayerHandle.T.sleepTicks.toFieldAccessor();

    public static final TranslatorFieldAccessor<PlayerAbilities> abilities = PlayerHandle.T.abilities.toFieldAccessor();
    public static final FieldAccessor<Integer> expLevel = PlayerHandle.T.expLevel.toFieldAccessor();
    public static final FieldAccessor<Integer> expTotal = PlayerHandle.T.expTotal.toFieldAccessor();
    public static final FieldAccessor<Float> exp = PlayerHandle.T.exp.toFieldAccessor();

    public static final FieldAccessor<GameProfileHandle> gameProfile = PlayerHandle.T.gameProfile.toFieldAccessor();

    public static boolean canInstaBuild(HumanEntity human) {
        return abilities.get(HandleConversion.toEntityHandle(human)).canInstantlyBuild();
    }
}
