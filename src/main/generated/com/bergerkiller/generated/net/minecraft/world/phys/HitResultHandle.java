package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.HitResult</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.HitResult")
public abstract class HitResultHandle extends Template.Handle {
    /** @see HitResultClass */
    public static final HitResultClass T = Template.Class.create(HitResultClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    /* ============================================================================== */

    public static HitResultHandle createHandle(Object hitResult) {
        return T.createHandle.invoke(hitResult);
    }

    public abstract Vector getLocation();
    /**
     * Stores class members for <b>net.minecraft.world.phys.HitResult</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HitResultClass extends Template.Class<HitResultHandle> {
        public final Template.StaticMethod.Converted<HitResultHandle> createHandle = new Template.StaticMethod.Converted<HitResultHandle>();

        public final Template.Method.Converted<Vector> getLocation = new Template.Method.Converted<Vector>();

    }

}

