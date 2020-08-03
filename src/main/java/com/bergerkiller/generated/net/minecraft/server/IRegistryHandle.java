package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IRegistry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.IRegistry")
public abstract class IRegistryHandle extends Template.Handle {
    /** @See {@link IRegistryClass} */
    public static final IRegistryClass T = Template.Class.create(IRegistryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IRegistryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int getWindowIdFromName(String name) {
        return T.getWindowIdFromName.invoker.invoke(null,name);
    }

    /**
     * Stores class members for <b>net.minecraft.server.IRegistry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IRegistryClass extends Template.Class<IRegistryHandle> {
        public final Template.StaticMethod<Integer> getWindowIdFromName = new Template.StaticMethod<Integer>();

    }

}

