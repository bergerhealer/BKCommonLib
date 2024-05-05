package com.bergerkiller.generated.net.minecraft.world.entity.vehicle;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.EntityBoat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.EntityBoat")
public abstract class EntityBoatHandle extends EntityHandle {
    /** @see EntityBoatClass */
    public static final EntityBoatClass T = Template.Class.create(EntityBoatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityBoatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<com.bergerkiller.bukkit.common.wrappers.BoatWoodType> DATA_WOOD_TYPE = Key.Type.BOAT_WOOD_TYPE.createKey(T.DATA_WOOD_TYPE, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.EntityBoat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityBoatClass extends Template.Class<EntityBoatHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_WOOD_TYPE = new Template.StaticField.Converted<Key<Integer>>();

    }

}

