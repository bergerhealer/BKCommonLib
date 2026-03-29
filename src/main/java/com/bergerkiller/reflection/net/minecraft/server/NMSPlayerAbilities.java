package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.entity.player.AbilitiesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

@Deprecated
public class NMSPlayerAbilities {
    public static final ClassTemplate<?> T = ClassTemplate.create(AbilitiesHandle.T.getType());

    public static final FieldAccessor<Boolean> isInvulnerable    = AbilitiesHandle.T.isInvulnerable.toFieldAccessor();
    public static final FieldAccessor<Boolean> isFlying          = AbilitiesHandle.T.isFlying.toFieldAccessor();
    public static final FieldAccessor<Boolean> canFly            = AbilitiesHandle.T.canFly.toFieldAccessor();
    public static final FieldAccessor<Boolean> canInstantlyBuild = AbilitiesHandle.T.canInstantlyBuild.toFieldAccessor();
    public static final FieldAccessor<Boolean> mayBuild          = AbilitiesHandle.T.mayBuild.toFieldAccessor();
    public static final FieldAccessor<Float> flySpeed            = new SafeDirectField<Float>() {
        @Override
        public Float get(Object instance) {
            return Float.valueOf((float) AbilitiesHandle.createHandle(instance).getFlySpeed());
        }

        @Override
        public boolean set(Object instance, Float value) {
            AbilitiesHandle.createHandle(instance).setFlySpeed(value.doubleValue());
            return true;
        }
    };
    public static final FieldAccessor<Float> walkSpeed           = AbilitiesHandle.T.walkSpeed.toFieldAccessor();
}
