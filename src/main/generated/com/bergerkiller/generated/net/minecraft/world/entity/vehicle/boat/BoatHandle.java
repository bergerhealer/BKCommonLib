package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.boat;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.boat.Boat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.boat.Boat")
public abstract class BoatHandle extends EntityHandle {
    /** @see BoatClass */
    public static final BoatClass T = Template.Class.create(BoatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BoatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<com.bergerkiller.bukkit.common.wrappers.BoatWoodType> DATA_WOOD_TYPE = Key.Type.BOAT_WOOD_TYPE.createKey(T.DATA_WOOD_TYPE, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.boat.Boat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BoatClass extends Template.Class<BoatHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_WOOD_TYPE = new Template.StaticField.Converted<Key<Integer>>();

    }

}

