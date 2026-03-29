package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.Registry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.core.Registry")
public abstract class RegistryHandle extends Template.Handle {
    /** @see RegistryClass */
    public static final RegistryClass T = Template.Class.create(RegistryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RegistryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getWindowTypeByName(String name) {
        return T.getWindowTypeByName.invoker.invoke(null,name);
    }

    public static int getWindowTypeId(Object windowTypeRaw) {
        return T.getWindowTypeId.invoker.invoke(null,windowTypeRaw);
    }

    /**
     * Stores class members for <b>net.minecraft.core.Registry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegistryClass extends Template.Class<RegistryHandle> {
        public final Template.StaticMethod<Object> getWindowTypeByName = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Integer> getWindowTypeId = new Template.StaticMethod<Integer>();

    }

}

