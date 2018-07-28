package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.PlayerAbilitiesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

@Deprecated
public class NMSPlayerAbilities {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerAbilities");

    public static final FieldAccessor<Boolean> isInvulnerable    = PlayerAbilitiesHandle.T.isInvulnerable.toFieldAccessor();
    public static final FieldAccessor<Boolean> isFlying          = PlayerAbilitiesHandle.T.isFlying.toFieldAccessor();
    public static final FieldAccessor<Boolean> canFly            = PlayerAbilitiesHandle.T.canFly.toFieldAccessor();
    public static final FieldAccessor<Boolean> canInstantlyBuild = PlayerAbilitiesHandle.T.canInstantlyBuild.toFieldAccessor();
    public static final FieldAccessor<Boolean> mayBuild          = PlayerAbilitiesHandle.T.mayBuild.toFieldAccessor();
    public static final FieldAccessor<Float> flySpeed            = new SafeDirectField<Float>() {
        @Override
        public Float get(Object instance) {
            return Float.valueOf((float) PlayerAbilitiesHandle.createHandle(instance).getFlySpeed());
        }

        @Override
        public boolean set(Object instance, Float value) {
            PlayerAbilitiesHandle.createHandle(instance).setFlySpeed(value.doubleValue());
            return true;
        }
    };
    public static final FieldAccessor<Float> walkSpeed           = PlayerAbilitiesHandle.T.walkSpeed.toFieldAccessor();
}
