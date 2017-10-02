package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MinecraftKey</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class MinecraftKeyHandle extends Template.Handle {
    /** @See {@link MinecraftKeyClass} */
    public static final MinecraftKeyClass T = new MinecraftKeyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MinecraftKeyHandle.class, "net.minecraft.server.MinecraftKey");

    /* ============================================================================== */

    public static MinecraftKeyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final MinecraftKeyHandle createNew(String keyToken) {
        return T.constr_keyToken.newInstance(keyToken);
    }

    public static final MinecraftKeyHandle createNew(int code, String[] parts) {
        return T.constr_code_parts.newInstance(code, parts);
    }

    /* ============================================================================== */


    public static MinecraftKeyHandle createNew(String namespace, String name) {
        return createNew(0, new String[] { namespace, name });
    }
    public String getNamespace() {
        return T.namespace.get(getRaw());
    }

    public void setNamespace(String value) {
        T.namespace.set(getRaw(), value);
    }

    public String getName() {
        return T.name.get(getRaw());
    }

    public void setName(String value) {
        T.name.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.MinecraftKey</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecraftKeyClass extends Template.Class<MinecraftKeyHandle> {
        public final Template.Constructor.Converted<MinecraftKeyHandle> constr_keyToken = new Template.Constructor.Converted<MinecraftKeyHandle>();
        public final Template.Constructor.Converted<MinecraftKeyHandle> constr_code_parts = new Template.Constructor.Converted<MinecraftKeyHandle>();

        public final Template.Field<String> namespace = new Template.Field<String>();
        public final Template.Field<String> name = new Template.Field<String>();

    }

}

