package com.bergerkiller.reflection.net.minecraft.server;

import java.util.Map;

import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSEntityLiving extends NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityLiving");

    public static final DataWatcher.Key<Byte> DATA_LIVING_FLAGS = DataWatcher.Key.fromStaticField(T, "at");
    public static final DataWatcher.Key<Float> DATA_HEALTH = DataWatcher.Key.fromStaticField(T, "HEALTH");
    public static final DataWatcher.Key<Integer> DATA_PARTICLES_TIMER = DataWatcher.Key.fromStaticField(T, "g");
    public static final DataWatcher.Key<Boolean> DATA_PARTICLES_HIDDEN = DataWatcher.Key.fromStaticField(T, "h");
    public static final DataWatcher.Key<Integer> DATA_UNKNOWN1 = DataWatcher.Key.fromStaticField(T, "bq");

    public static final FieldAccessor<Object> attributeMap = T.nextField("private AttributeMapBase attributeMap");
    public static final FieldAccessor<Map<Object, Object>> mobEffects = T.nextField("public final Map<MobEffectList, MobEffect> effects");
    public static final FieldAccessor<Boolean> updateEffects = T.nextField("public boolean updateEffects");

    public static final FieldAccessor<Float> lastDamage = T.nextField("public float lastDamage");

    static {
    	T.skipFieldSignature("protected boolean bd");
    	T.skipFieldSignature("public float be");
    }

    public static final FieldAccessor<Float> forwardMovement = T.nextFieldSignature("public float bf");

    static {
    	T.skipFieldSignature("public float bg");
    }

    public static final MethodAccessor<Void> resetAttributes = T.selectMethod("protected void initAttributes()");
    public static final MethodAccessor<Object> getAttributesMap = T.selectMethod("public AttributeMapBase getAttributeMap()");
    public static final MethodAccessor<Void> initAttributes = T.selectMethod("protected void initAttributes()");
    /*
	 * public static final TranslatorFieldAccessor<PlayerAbilities> abilities = TEMPLATE.getField("abilities").translate(ConversionPairs.playerAbilities);
     */
}
