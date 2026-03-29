package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.MinecartSpawner</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.MinecartSpawner")
public abstract class MinecartSpawnerHandle extends AbstractMinecartHandle {
    /** @see MinecartSpawnerClass */
    public static final MinecartSpawnerClass T = Template.Class.create(MinecartSpawnerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MinecartSpawnerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract MobSpawner getMobSpawner();
    public abstract void setMobSpawner(MobSpawner value);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.MinecartSpawner</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecartSpawnerClass extends Template.Class<MinecartSpawnerHandle> {
        public final Template.Field.Converted<MobSpawner> mobSpawner = new Template.Field.Converted<MobSpawner>();

    }

}

