package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class NMSSoundEffect {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("SoundEffect");
    public static final FieldAccessor<Object> key = T.nextField("private final MinecraftKey b");
}
