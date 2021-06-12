package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSSoundEffect {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("SoundEffect");
    public static final FieldAccessor<Object> key = T.nextField("private final net.minecraft.resources.MinecraftKey b");
}
