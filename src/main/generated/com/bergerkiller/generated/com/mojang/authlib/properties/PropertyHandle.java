package com.bergerkiller.generated.com.mojang.authlib.properties;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>com.mojang.authlib.properties.Property</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("com.mojang.authlib.properties.Property")
public abstract class PropertyHandle extends Template.Handle {
    /** @See {@link PropertyClass} */
    public static final PropertyClass T = Template.Class.create(PropertyClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PropertyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PropertyHandle createNew(String name, String value) {
        return T.constr_name_value.newInstance(name, value);
    }

    public static final PropertyHandle createNew(String name, String value, String signature) {
        return T.constr_name_value_signature.newInstance(name, value, signature);
    }

    /* ============================================================================== */

    public abstract String getName();
    public abstract String getValue();
    public abstract String getSignature();
    /**
     * Stores class members for <b>com.mojang.authlib.properties.Property</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PropertyClass extends Template.Class<PropertyHandle> {
        public final Template.Constructor.Converted<PropertyHandle> constr_name_value = new Template.Constructor.Converted<PropertyHandle>();
        public final Template.Constructor.Converted<PropertyHandle> constr_name_value_signature = new Template.Constructor.Converted<PropertyHandle>();

        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method<String> getValue = new Template.Method<String>();
        public final Template.Method<String> getSignature = new Template.Method<String>();

    }

}

