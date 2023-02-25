package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.IBlockAccess</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.IBlockAccess")
public abstract class IBlockAccessHandle extends Template.Handle {
    /** @See {@link IBlockAccessClass} */
    public static final IBlockAccessClass T = Template.Class.create(IBlockAccessClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IBlockAccessHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.IBlockAccess</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IBlockAccessClass extends Template.Class<IBlockAccessHandle> {
    }

}

