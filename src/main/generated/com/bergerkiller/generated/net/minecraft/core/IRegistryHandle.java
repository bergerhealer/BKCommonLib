package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.IRegistry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.core.IRegistry")
public abstract class IRegistryHandle extends Template.Handle {
    /** @see IRegistryClass */
    public static final IRegistryClass T = Template.Class.create(IRegistryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IRegistryHandle createHandle(Object handleInstance) {
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
     * Stores class members for <b>net.minecraft.core.IRegistry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IRegistryClass extends Template.Class<IRegistryHandle> {
        public final Template.StaticMethod<Object> getWindowTypeByName = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Integer> getWindowTypeId = new Template.StaticMethod<Integer>();

    }

}

