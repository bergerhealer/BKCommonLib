package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.MappedRegistry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.MappedRegistry")
public abstract class MappedRegistryHandle extends Template.Handle {
    /** @see MappedRegistryClass */
    public static final MappedRegistryClass T = Template.Class.create(MappedRegistryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MappedRegistryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object get(Object key);
    public abstract Object getKey(Object value);
    /**
     * Stores class members for <b>net.minecraft.core.MappedRegistry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MappedRegistryClass extends Template.Class<MappedRegistryHandle> {
        @Template.Optional
        public final Template.Field<Map<Object, Object>> opt_inverseLookupField = new Template.Field<Map<Object, Object>>();

        public final Template.Method.Converted<Object> get = new Template.Method.Converted<Object>();
        public final Template.Method<Object> getKey = new Template.Method<Object>();

    }

}

