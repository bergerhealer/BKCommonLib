package com.bergerkiller.generated.net.minecraft.world.entity.vehicle;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.VehicleEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.vehicle.VehicleEntity")
public abstract class VehicleEntityHandle extends EntityHandle {
    /** @see VehicleEntityClass */
    public static final VehicleEntityClass T = Template.Class.create(VehicleEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static VehicleEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.VehicleEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class VehicleEntityClass extends Template.Class<VehicleEntityHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_SHAKING_FACTOR = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_SHAKING_DIRECTION = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_SHAKING_DAMAGE = new Template.StaticField.Converted<Key<Float>>();

    }

}

