package com.bergerkiller.reflection.net.minecraft.server;

import org.bukkit.potion.PotionEffectType;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

public class NMSMobEffect {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobEffect");
    public static final TranslatorFieldAccessor<PotionEffectType> effectType = T.nextField("private final MobEffectList b").translate(ConversionPairs.potionEffectType);
    public static final FieldAccessor<Integer> duration = T.nextField("private int duration");
    public static final FieldAccessor<Integer> amplification = T.nextField("private int amplification");
    public static final FieldAccessor<Boolean> splash = T.nextField("private boolean splash");
    public static final FieldAccessor<Boolean> ambient = T.nextField("private boolean ambient");
    public static final FieldAccessor<Boolean> particles = T.nextFieldSignature("private boolean h");

    public static class List {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobEffectList");
        public static final MethodAccessor<Integer> getId = T.selectMethod("public static int getId(MobEffectList mobeffectlist)");
        public static final MethodAccessor<Object> fromId = T.selectMethod("public static MobEffectList fromId(int id)");
    }

}
