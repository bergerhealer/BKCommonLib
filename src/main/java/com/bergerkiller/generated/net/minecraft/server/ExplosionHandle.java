package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Explosion</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ExplosionHandle extends Template.Handle {
    /** @See {@link ExplosionClass} */
    public static final ExplosionClass T = new ExplosionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ExplosionHandle.class, "net.minecraft.server.Explosion", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static ExplosionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ExplosionHandle createNew(World world, Entity entity, double x, double y, double z, float yield, boolean fire, boolean destroyBlocks) {
        return T.createNew.invokeVA(world, entity, x, y, z, yield, fire, destroyBlocks);
    }

    /**
     * Stores class members for <b>net.minecraft.server.Explosion</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ExplosionClass extends Template.Class<ExplosionHandle> {
        public final Template.StaticMethod.Converted<ExplosionHandle> createNew = new Template.StaticMethod.Converted<ExplosionHandle>();

    }

}

