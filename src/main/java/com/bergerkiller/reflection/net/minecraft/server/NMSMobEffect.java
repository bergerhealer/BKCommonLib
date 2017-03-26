package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSMobEffect {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobEffect");
    public static final FieldAccessor<Object> effectList = T.nextField("private final MobEffectList b");
    public static final FieldAccessor<Integer> duration = T.nextField("private int duration");
    public static final FieldAccessor<Integer> amplification = T.nextField("private int amplification");
    public static final FieldAccessor<Boolean> splash = T.nextField("private boolean splash");
    public static final FieldAccessor<Boolean> ambient = T.nextField("private boolean ambient");
    public static final FieldAccessor<Boolean> particles = T.nextFieldSignature("private boolean h");
    
    public static class List {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobEffectList");
        public static final MethodAccessor<Integer> getId = T.selectMethod("public static int getId(MobEffectList mobeffectlist)");
    }
    
    public static int getEffectId(Object mobEffect) {
    	return List.getId.invoke(null, effectList.get(mobEffect));
    }
}
