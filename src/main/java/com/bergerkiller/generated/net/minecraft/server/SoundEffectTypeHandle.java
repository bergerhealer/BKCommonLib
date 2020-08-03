package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.SoundEffectType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.SoundEffectType")
public abstract class SoundEffectTypeHandle extends Template.Handle {
    /** @See {@link SoundEffectTypeClass} */
    public static final SoundEffectTypeClass T = Template.Class.create(SoundEffectTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundEffectTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract SoundEffectHandle getBreakSound();
    public abstract SoundEffectHandle getStepSound();
    public abstract SoundEffectHandle getPlaceSound();
    /**
     * Stores class members for <b>net.minecraft.server.SoundEffectType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectTypeClass extends Template.Class<SoundEffectTypeHandle> {
        public final Template.Method.Converted<SoundEffectHandle> getBreakSound = new Template.Method.Converted<SoundEffectHandle>();
        public final Template.Method.Converted<SoundEffectHandle> getStepSound = new Template.Method.Converted<SoundEffectHandle>();
        public final Template.Method.Converted<SoundEffectHandle> getPlaceSound = new Template.Method.Converted<SoundEffectHandle>();

    }

}

