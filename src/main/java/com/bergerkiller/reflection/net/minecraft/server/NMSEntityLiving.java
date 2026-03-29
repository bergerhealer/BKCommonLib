package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use LivingEntityHandle instead
 */
@Deprecated
public class NMSEntityLiving extends NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.create(LivingEntityHandle.T.getType());

    public static final DataWatcher.Key<Byte> DATA_LIVING_FLAGS = LivingEntityHandle.DATA_LIVING_FLAGS;
    public static final DataWatcher.Key<Float> DATA_HEALTH = LivingEntityHandle.DATA_HEALTH;
    public static final DataWatcher.Key<Boolean> DATA_PARTICLES_HIDDEN = LivingEntityHandle.DATA_PARTICLES_HIDDEN;
    public static final DataWatcher.Key<Integer> DATA_UNKNOWN1 = LivingEntityHandle.DATA_UNKNOWN1;

    public static final FieldAccessor<Boolean> updateEffects = LivingEntityHandle.T.updateEffects.toFieldAccessor();

    public static final FieldAccessor<Float> lastDamage = LivingEntityHandle.T.lastDamage.toFieldAccessor();

    public static final MethodAccessor<Void> resetAttributes = LivingEntityHandle.T.resetAttributes.toMethodAccessor();
    public static final MethodAccessor<Object> getAttributesMap = LivingEntityHandle.T.getAttributeMap.raw.toMethodAccessor();
    public static final MethodAccessor<Void> initAttributes = LivingEntityHandle.T.resetAttributes.toMethodAccessor();
    /*
	 * public static final TranslatorFieldAccessor<PlayerAbilities> abilities = TEMPLATE.getField("abilities").translate(ConversionPairs.playerAbilities);
     */
}
