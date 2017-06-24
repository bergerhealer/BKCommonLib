package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.SoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class SoundEffectHandle extends Template.Handle {
    /** @See {@link SoundEffectClass} */
    public static final SoundEffectClass T = new SoundEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundEffectHandle.class, "net.minecraft.server.SoundEffect");

    public static final RegistryMaterialsHandle REGISTRY = T.REGISTRY.getSafe();
    /* ============================================================================== */

    public static SoundEffectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SoundEffectHandle handle = new SoundEffectHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final SoundEffectHandle createNew(MinecraftKeyHandle minecraftkey) {
        return T.constr_minecraftkey.newInstance(minecraftkey);
    }

    /* ============================================================================== */

    public MinecraftKeyHandle getName() {
        return T.name.get(instance);
    }

    public void setName(MinecraftKeyHandle value) {
        T.name.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.SoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectClass extends Template.Class<SoundEffectHandle> {
        public final Template.Constructor.Converted<SoundEffectHandle> constr_minecraftkey = new Template.Constructor.Converted<SoundEffectHandle>();

        public final Template.StaticField.Converted<RegistryMaterialsHandle> REGISTRY = new Template.StaticField.Converted<RegistryMaterialsHandle>();

        public final Template.Field.Converted<MinecraftKeyHandle> name = new Template.Field.Converted<MinecraftKeyHandle>();

    }

}

