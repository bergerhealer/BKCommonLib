package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.SoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class SoundEffectHandle extends Template.Handle {
    /** @See {@link SoundEffectClass} */
    public static final SoundEffectClass T = new SoundEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundEffectHandle.class, "net.minecraft.server.SoundEffect", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static SoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final SoundEffectHandle createNew(MinecraftKeyHandle minecraftkey) {
        return T.constr_minecraftkey.newInstance(minecraftkey);
    }

    /* ============================================================================== */


    public static SoundEffectHandle byName(String name) {
        if (T.opt_getRegistry.isAvailable()) {
            Object mc_key_raw = MinecraftKeyHandle.T.createNew.raw.invoke(name);
            if (mc_key_raw != null) {
                return createHandle(T.opt_getRegistry.invoke().get(mc_key_raw));
            } else {
                return null;
            }
        } else {
            return createNew(MinecraftKeyHandle.createNew(name));
        }
    }
    public abstract MinecraftKeyHandle getName();
    public abstract void setName(MinecraftKeyHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.SoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectClass extends Template.Class<SoundEffectHandle> {
        public final Template.Constructor.Converted<SoundEffectHandle> constr_minecraftkey = new Template.Constructor.Converted<SoundEffectHandle>();

        public final Template.Field.Converted<MinecraftKeyHandle> name = new Template.Field.Converted<MinecraftKeyHandle>();

        @Template.Optional
        public final Template.StaticMethod.Converted<RegistryMaterialsHandle> opt_getRegistry = new Template.StaticMethod.Converted<RegistryMaterialsHandle>();

    }

}

