package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.SoundEffects</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class SoundEffectsHandle extends Template.Handle {
    /** @See {@link SoundEffectsClass} */
    public static final SoundEffectsClass T = new SoundEffectsClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundEffectsHandle.class, "net.minecraft.server.SoundEffects");

    public static final ResourceKey EXTINGUISH_FIRE = T.EXTINGUISH_FIRE.getSafe();
    /* ============================================================================== */

    public static SoundEffectsHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SoundEffectsHandle handle = new SoundEffectsHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.SoundEffects</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectsClass extends Template.Class<SoundEffectsHandle> {
        public final Template.StaticField.Converted<ResourceKey> EXTINGUISH_FIRE = new Template.StaticField.Converted<ResourceKey>();

    }

}

