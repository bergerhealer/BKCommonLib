package com.bergerkiller.generated.net.minecraft.resources;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.resources.MinecraftKey</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.resources.MinecraftKey")
public abstract class MinecraftKeyHandle extends Template.Handle {
    /** @see MinecraftKeyClass */
    public static final MinecraftKeyClass T = Template.Class.create(MinecraftKeyClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MinecraftKeyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MinecraftKeyHandle createNew(String keyToken) {
        return T.createNew.invoke(keyToken);
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

    public static MinecraftKeyHandle createNew(String namespace, String name) {
        return T.createNew2.invoke(namespace, name);
    }
    public abstract String getNamespace();
    public abstract void setNamespace(String value);
    public abstract String getName();
    public abstract void setName(String value);
    /**
     * Stores class members for <b>net.minecraft.resources.MinecraftKey</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecraftKeyClass extends Template.Class<MinecraftKeyHandle> {
        @Template.Optional
        public final Template.Constructor<Object> constr_code_parts = new Template.Constructor<Object>();

        public final Template.Field<String> namespace = new Template.Field<String>();
        public final Template.Field<String> name = new Template.Field<String>();

        public final Template.StaticMethod.Converted<MinecraftKeyHandle> createNew = new Template.StaticMethod.Converted<MinecraftKeyHandle>();
        @Template.Optional
        public final Template.StaticMethod.Converted<MinecraftKeyHandle> createNew2 = new Template.StaticMethod.Converted<MinecraftKeyHandle>();

    }

}

