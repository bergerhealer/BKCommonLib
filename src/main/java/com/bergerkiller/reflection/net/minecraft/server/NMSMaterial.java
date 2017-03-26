package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSMaterial {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Material");
    public static final MethodAccessor<Boolean> materialBuildable = T.selectMethod("public boolean isBuildable()");
}
