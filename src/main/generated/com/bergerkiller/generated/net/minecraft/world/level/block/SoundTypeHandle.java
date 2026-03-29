package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.SoundType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.SoundType")
public abstract class SoundTypeHandle extends Template.Handle {
    /** @see SoundTypeClass */
    public static final SoundTypeClass T = Template.Class.create(SoundTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract SoundEventHandle getStepSound();
    public abstract SoundEventHandle getPlaceSound();
    public abstract SoundEventHandle getBreakSound();
    public abstract SoundEventHandle getFallSound();
    /**
     * Stores class members for <b>net.minecraft.world.level.block.SoundType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundTypeClass extends Template.Class<SoundTypeHandle> {
        public final Template.Method.Converted<SoundEventHandle> getStepSound = new Template.Method.Converted<SoundEventHandle>();
        public final Template.Method.Converted<SoundEventHandle> getPlaceSound = new Template.Method.Converted<SoundEventHandle>();
        public final Template.Method.Converted<SoundEventHandle> getBreakSound = new Template.Method.Converted<SoundEventHandle>();
        public final Template.Method.Converted<SoundEventHandle> getFallSound = new Template.Method.Converted<SoundEventHandle>();

    }

}

