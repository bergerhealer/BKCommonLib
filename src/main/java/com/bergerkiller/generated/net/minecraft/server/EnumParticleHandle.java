package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumParticle</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EnumParticleHandle extends Template.Handle {
    /** @See {@link EnumParticleClass} */
    public static final EnumParticleClass T = new EnumParticleClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumParticleHandle.class, "net.minecraft.server.EnumParticle");

    /* ============================================================================== */

    public static EnumParticleHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumParticleHandle handle = new EnumParticleHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public String getName() {
        return T.getName.invoke(instance);
    }

    public int getId() {
        return T.getId.invoke(instance);
    }


    public static EnumParticleHandle getByName(String name) {
        if (T.byName.isAvailable()) {
            return T.byName.invokeVA(name);
        } else {
            for (Object enumValue : T.getType().getEnumConstants()) {
                if (T.getName.invoke(enumValue).equals(name)) {
                    return createHandle(enumValue);
                }
            }
            return null;
        }
    }
    /**
     * Stores class members for <b>net.minecraft.server.EnumParticle</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumParticleClass extends Template.Class<EnumParticleHandle> {
        @Template.Optional
        public final Template.StaticMethod.Converted<EnumParticleHandle> byName = new Template.StaticMethod.Converted<EnumParticleHandle>();

        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

