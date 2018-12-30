package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.LocaleLanguage</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class LocaleLanguageHandle extends Template.Handle {
    /** @See {@link LocaleLanguageClass} */
    public static final LocaleLanguageClass T = new LocaleLanguageClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LocaleLanguageHandle.class, "net.minecraft.server.LocaleLanguage", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    public static final LocaleLanguageHandle INSTANCE = T.INSTANCE.getSafe();
    /* ============================================================================== */

    public static LocaleLanguageHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String get(String s);
    /**
     * Stores class members for <b>net.minecraft.server.LocaleLanguage</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LocaleLanguageClass extends Template.Class<LocaleLanguageHandle> {
        public final Template.StaticField.Converted<LocaleLanguageHandle> INSTANCE = new Template.StaticField.Converted<LocaleLanguageHandle>();

        public final Template.Method<String> get = new Template.Method<String>();

    }

}

