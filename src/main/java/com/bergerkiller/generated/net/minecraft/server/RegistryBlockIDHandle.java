package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RegistryBlockID</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.RegistryBlockID")
public abstract class RegistryBlockIDHandle extends Template.Handle {
    /** @See {@link RegistryBlockIDClass} */
    public static final RegistryBlockIDClass T = Template.Class.create(RegistryBlockIDClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RegistryBlockIDHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId(Object value);
    /**
     * Stores class members for <b>net.minecraft.server.RegistryBlockID</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegistryBlockIDClass extends Template.Class<RegistryBlockIDHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

