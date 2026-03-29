package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import org.bukkit.potion.PotionEffectType;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
/**
 * <b>Deprecated: </b>Please use {@link MobEffectInstanceHandle} and {@link MobEffectHandle} instead.
 */
@Deprecated
public class NMSMobEffect {
    public static final ClassTemplate<?> T = ClassTemplate.create(MobEffectInstanceHandle.T.getType());
    public static final TranslatorFieldAccessor<PotionEffectType> effectType = MobEffectInstanceHandle.T.effectList.toFieldAccessor().translate(DuplexConversion.potionEffectType);
    public static final FieldAccessor<Integer> duration = MobEffectInstanceHandle.T.duration.toFieldAccessor();
    public static final FieldAccessor<Integer> amplification = MobEffectInstanceHandle.T.amplifier.toFieldAccessor();
    public static final FieldAccessor<Boolean> ambient = MobEffectInstanceHandle.T.ambient.toFieldAccessor();
    public static final FieldAccessor<Boolean> particles = MobEffectInstanceHandle.T.particles.toFieldAccessor();

    @Deprecated
    public static class List {
        public static final ClassTemplate<?> T = ClassTemplate.create(MobEffectHandle.T.getType());
        public static final MethodAccessor<Integer> getId = MobEffectHandle.T.getId.raw.toMethodAccessor();
        public static final MethodAccessor<Object> fromId = MobEffectHandle.T.fromId.raw.toMethodAccessor();
    }

}
