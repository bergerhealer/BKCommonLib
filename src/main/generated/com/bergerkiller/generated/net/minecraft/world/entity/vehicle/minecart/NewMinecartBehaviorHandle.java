package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior")
public abstract class NewMinecartBehaviorHandle extends Template.Handle {
    /** @see NewMinecartBehaviorClass */
    public static final NewMinecartBehaviorClass T = Template.Class.create(NewMinecartBehaviorClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NewMinecartBehaviorHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NewMinecartBehaviorClass extends Template.Class<NewMinecartBehaviorHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior.LerpStep</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.Optional
    @Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior.LerpStep")
    public abstract static class LerpStepHandle extends Template.Handle {
        /** @see LerpStepClass */
        public static final LerpStepClass T = Template.Class.create(LerpStepClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static LerpStepHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static LerpStepHandle createNew(Vector position, Vector movement, float yaw, float pitch, float weight) {
            return T.createNew.invoke(position, movement, yaw, pitch, weight);
        }

        public abstract Vector getPosition();
        public abstract Vector getMovement();
        public abstract float getYaw();
        public abstract float getPitch();
        public abstract float getWeight();
        /**
         * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior.LerpStep</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class LerpStepClass extends Template.Class<LerpStepHandle> {
            public final Template.StaticMethod.Converted<LerpStepHandle> createNew = new Template.StaticMethod.Converted<LerpStepHandle>();

            public final Template.Method.Converted<Vector> getPosition = new Template.Method.Converted<Vector>();
            public final Template.Method.Converted<Vector> getMovement = new Template.Method.Converted<Vector>();
            public final Template.Method<Float> getYaw = new Template.Method<Float>();
            public final Template.Method<Float> getPitch = new Template.Method<Float>();
            public final Template.Method<Float> getWeight = new Template.Method<Float>();

        }

    }

}

