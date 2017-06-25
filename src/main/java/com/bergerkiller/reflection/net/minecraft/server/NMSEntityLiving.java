package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use EntityLivingHandle instead
 */
@Deprecated
public class NMSEntityLiving extends NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityLiving");

    public static final DataWatcher.Key<Byte> DATA_LIVING_FLAGS = EntityLivingHandle.DATA_LIVING_FLAGS;
    public static final DataWatcher.Key<Float> DATA_HEALTH = EntityLivingHandle.DATA_HEALTH;
    public static final DataWatcher.Key<Integer> DATA_PARTICLES_TIMER = EntityLivingHandle.DATA_PARTICLES_TIMER;
    public static final DataWatcher.Key<Boolean> DATA_PARTICLES_HIDDEN = EntityLivingHandle.DATA_PARTICLES_HIDDEN;
    public static final DataWatcher.Key<Integer> DATA_UNKNOWN1 = EntityLivingHandle.DATA_UNKNOWN1;

    public static final FieldAccessor<Object> attributeMap = EntityLivingHandle.T.attributeMapField.raw.toFieldAccessor();
    public static final FieldAccessor<Boolean> updateEffects = EntityLivingHandle.T.updateEffects.toFieldAccessor();

    public static final FieldAccessor<Float> lastDamage = EntityLivingHandle.T.lastDamage.toFieldAccessor();
    public static final FieldAccessor<Float> forwardMovement = EntityLivingHandle.T.forwardMovement.toFieldAccessor();

    public static final MethodAccessor<Void> resetAttributes = EntityLivingHandle.T.resetAttributes.toMethodAccessor();
    public static final MethodAccessor<Object> getAttributesMap = EntityLivingHandle.T.getAttributeMap.raw.toMethodAccessor();
    public static final MethodAccessor<Void> initAttributes = EntityLivingHandle.T.resetAttributes.toMethodAccessor();
    /*
	 * public static final TranslatorFieldAccessor<PlayerAbilities> abilities = TEMPLATE.getField("abilities").translate(ConversionPairs.playerAbilities);
     */
}
