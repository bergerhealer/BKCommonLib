package com.bergerkiller.reflection.net.minecraft.server;

import org.bukkit.potion.PotionEffectType;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
/**
 * <b>Deprecated: </b>Please use {@link MobEffectHandle} and {@link MobEffectListHandle} instead.
 */
@Deprecated
public class NMSMobEffect {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobEffect");
    public static final TranslatorFieldAccessor<PotionEffectType> effectType = MobEffectHandle.T.effectList.toFieldAccessor().translate(DuplexConversion.potionEffectType);
    public static final FieldAccessor<Integer> duration = MobEffectHandle.T.duration.toFieldAccessor();
    public static final FieldAccessor<Integer> amplification = MobEffectHandle.T.amplification.toFieldAccessor();
    public static final FieldAccessor<Boolean> splash = MobEffectHandle.T.splash.toFieldAccessor();
    public static final FieldAccessor<Boolean> ambient = MobEffectHandle.T.ambient.toFieldAccessor();
    public static final FieldAccessor<Boolean> particles = MobEffectHandle.T.particles.toFieldAccessor();

    @Deprecated
    public static class List {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobEffectList");
        public static final MethodAccessor<Integer> getId = MobEffectListHandle.T.getId.raw.toMethodAccessor();
        public static final MethodAccessor<Object> fromId = MobEffectListHandle.T.fromId.raw.toMethodAccessor();
    }

}
