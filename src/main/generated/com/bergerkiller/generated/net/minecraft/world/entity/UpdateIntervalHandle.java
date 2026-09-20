package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.UpdateInterval</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.UpdateInterval")
public abstract class UpdateIntervalHandle extends Template.Handle {
    /** @see UpdateIntervalClass */
    public static final UpdateIntervalClass T = Template.Class.create(UpdateIntervalClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static UpdateIntervalHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object fromPeriodToInterval(int period) {
        return T.fromPeriodToInterval.invoker.invoke(null,period);
    }

    public static int fromIntervalToPeriod(Object updateInterval) {
        return T.fromIntervalToPeriod.invoker.invoke(null,updateInterval);
    }

    /**
     * Stores class members for <b>net.minecraft.world.entity.UpdateInterval</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class UpdateIntervalClass extends Template.Class<UpdateIntervalHandle> {
        public final Template.StaticMethod<Object> fromPeriodToInterval = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Integer> fromIntervalToPeriod = new Template.StaticMethod<Integer>();

    }

}

