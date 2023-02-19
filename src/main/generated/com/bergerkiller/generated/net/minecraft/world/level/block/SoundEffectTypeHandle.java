package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEffectHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.SoundEffectType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.SoundEffectType")
public abstract class SoundEffectTypeHandle extends Template.Handle {
    /** @See {@link SoundEffectTypeClass} */
    public static final SoundEffectTypeClass T = Template.Class.create(SoundEffectTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundEffectTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract SoundEffectHandle getStepSound();
    public abstract SoundEffectHandle getPlaceSound();
    public abstract SoundEffectHandle getBreakSound();
    public abstract SoundEffectHandle getFallSound();
    /**
     * Stores class members for <b>net.minecraft.world.level.block.SoundEffectType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectTypeClass extends Template.Class<SoundEffectTypeHandle> {
        public final Template.Method.Converted<SoundEffectHandle> getStepSound = new Template.Method.Converted<SoundEffectHandle>();
        public final Template.Method.Converted<SoundEffectHandle> getPlaceSound = new Template.Method.Converted<SoundEffectHandle>();
        public final Template.Method.Converted<SoundEffectHandle> getBreakSound = new Template.Method.Converted<SoundEffectHandle>();
        public final Template.Method.Converted<SoundEffectHandle> getFallSound = new Template.Method.Converted<SoundEffectHandle>();

    }

}

