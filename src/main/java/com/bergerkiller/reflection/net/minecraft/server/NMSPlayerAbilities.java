package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSPlayerAbilities {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerAbilities");
    
    public static final FieldAccessor<Boolean> isInvulnerable    = T.selectField("public boolean isInvulnerable");
    public static final FieldAccessor<Boolean> isFlying          = T.selectField("public boolean isFlying");
    public static final FieldAccessor<Boolean> canFly            = T.selectField("public boolean canFly");
    public static final FieldAccessor<Boolean> canInstantlyBuild = T.selectField("public boolean canInstantlyBuild");
    public static final FieldAccessor<Boolean> mayBuild          = T.selectField("public boolean mayBuild");
    public static final FieldAccessor<Float> flySpeed            = T.selectField("public float flySpeed");
    public static final FieldAccessor<Float> walkSpeed           = T.selectField("public float walkSpeed");
}
