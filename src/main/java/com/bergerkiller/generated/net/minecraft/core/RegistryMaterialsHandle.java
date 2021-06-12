package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.RegistryMaterials</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.RegistryMaterials")
public abstract class RegistryMaterialsHandle extends Template.Handle {
    /** @See {@link RegistryMaterialsClass} */
    public static final RegistryMaterialsClass T = Template.Class.create(RegistryMaterialsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RegistryMaterialsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object get(Object key);
    public abstract Object getKey(Object value);
    /**
     * Stores class members for <b>net.minecraft.core.RegistryMaterials</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegistryMaterialsClass extends Template.Class<RegistryMaterialsHandle> {
        @Template.Optional
        public final Template.Field<Map<Object, Object>> opt_inverseLookupField = new Template.Field<Map<Object, Object>>();

        public final Template.Method.Converted<Object> get = new Template.Method.Converted<Object>();
        public final Template.Method<Object> getKey = new Template.Method<Object>();

    }

}

