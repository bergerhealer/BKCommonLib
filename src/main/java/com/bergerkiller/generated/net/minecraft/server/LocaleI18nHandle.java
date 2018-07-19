package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.LocaleI18n</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class LocaleI18nHandle extends Template.Handle {
    /** @See {@link LocaleI18nClass} */
    public static final LocaleI18nClass T = new LocaleI18nClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LocaleI18nHandle.class, "net.minecraft.server.LocaleI18n");

    /* ============================================================================== */

    public static LocaleI18nHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static String get(String key) {
        return T.get.invoke(key);
    }

    /**
     * Stores class members for <b>net.minecraft.server.LocaleI18n</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LocaleI18nClass extends Template.Class<LocaleI18nHandle> {
        public final Template.StaticMethod<String> get = new Template.StaticMethod<String>();

    }

}

