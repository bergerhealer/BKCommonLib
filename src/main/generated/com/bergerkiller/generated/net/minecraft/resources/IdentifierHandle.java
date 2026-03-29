package com.bergerkiller.generated.net.minecraft.resources;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.resources.Identifier</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.resources.Identifier")
public abstract class IdentifierHandle extends Template.Handle {
    /** @see IdentifierClass */
    public static final IdentifierClass T = Template.Class.create(IdentifierClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IdentifierHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static IdentifierHandle createNew(String keyToken) {
        return T.createNew.invoke(keyToken);
    }

    public abstract String getName();
    public abstract String getNamespace();
    public static final String DEFAULT_NAMESPACE = "minecraft";

    public boolean isDefaultNamespace() {
        return DEFAULT_NAMESPACE.equals(getNamespace());
    }

    public String toShortString() {
        return isDefaultNamespace() ? getName() : toString();
    }

    public static boolean isValid(String key) {
        return createNew(key) != null;
    }

    public static boolean isValidNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return true;
        }
        for (int cidx = 0; cidx < namespace.length(); cidx++) {
            char i = namespace.charAt(cidx);
            if (i == 95 || i == 45 || (i >= 97 && i <= 122) || (i >= 48 && i <= 57) || i == 46) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (int cidx = 0; cidx < name.length(); cidx++) {
            char i = name.charAt(cidx);
            if (i == 95 || i == 45 || (i >= 97 && i <= 122) || (i >= 48 && i <= 57) || i == 47 || i == 46) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static IdentifierHandle createNew(String namespace, String name) {
        return T.createNew2.invoke(namespace, name);
    }
    /**
     * Stores class members for <b>net.minecraft.resources.Identifier</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IdentifierClass extends Template.Class<IdentifierHandle> {
        @Template.Optional
        public final Template.Constructor<Object> constr_code_parts = new Template.Constructor<Object>();

        public final Template.StaticMethod.Converted<IdentifierHandle> createNew = new Template.StaticMethod.Converted<IdentifierHandle>();
        @Template.Optional
        public final Template.StaticMethod.Converted<IdentifierHandle> createNew2 = new Template.StaticMethod.Converted<IdentifierHandle>();

        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method<String> getNamespace = new Template.Method<String>();

    }

}

