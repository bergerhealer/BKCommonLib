package com.bergerkiller.generated.net.minecraft.locale;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.locale.LocaleLanguage</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.locale.LocaleLanguage")
public abstract class LocaleLanguageHandle extends Template.Handle {
    /** @See {@link LocaleLanguageClass} */
    public static final LocaleLanguageClass T = Template.Class.create(LocaleLanguageClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static LocaleLanguageHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static LocaleLanguageHandle INSTANCE() {
        return T.INSTANCE.get();
    }

    public static void INSTANCE_set(LocaleLanguageHandle value) {
        T.INSTANCE.set(value);
    }

    public abstract String get(String s);
    /**
     * Stores class members for <b>net.minecraft.locale.LocaleLanguage</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LocaleLanguageClass extends Template.Class<LocaleLanguageHandle> {
        public final Template.StaticField.Converted<LocaleLanguageHandle> INSTANCE = new Template.StaticField.Converted<LocaleLanguageHandle>();

        public final Template.Method<String> get = new Template.Method<String>();

    }

}

