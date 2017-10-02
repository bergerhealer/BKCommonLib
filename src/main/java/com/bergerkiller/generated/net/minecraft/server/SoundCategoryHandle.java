package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.SoundCategory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class SoundCategoryHandle extends Template.Handle {
    /** @See {@link SoundCategoryClass} */
    public static final SoundCategoryClass T = new SoundCategoryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundCategoryHandle.class, "net.minecraft.server.SoundCategory");

    /* ============================================================================== */

    public static SoundCategoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static SoundCategoryHandle byName(String name) {
        return T.byName.invoke(name);
    }

    public String getName() {
        return T.getName.invoke(getRaw());
    }

    /**
     * Stores class members for <b>net.minecraft.server.SoundCategory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundCategoryClass extends Template.Class<SoundCategoryHandle> {
        public final Template.StaticMethod.Converted<SoundCategoryHandle> byName = new Template.StaticMethod.Converted<SoundCategoryHandle>();

        public final Template.Method<String> getName = new Template.Method<String>();

    }

}

