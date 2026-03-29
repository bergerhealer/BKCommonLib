package com.bergerkiller.generated.net.minecraft.sounds;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.sounds.SoundSource</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.sounds.SoundSource")
public abstract class SoundSourceHandle extends Template.Handle {
    /** @see SoundSourceClass */
    public static final SoundSourceClass T = Template.Class.create(SoundSourceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundSourceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static SoundSourceHandle byName(String name) {
        return T.byName.invoke(name);
    }

    public abstract String getName();
    /**
     * Stores class members for <b>net.minecraft.sounds.SoundSource</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundSourceClass extends Template.Class<SoundSourceHandle> {
        public final Template.StaticMethod.Converted<SoundSourceHandle> byName = new Template.StaticMethod.Converted<SoundSourceHandle>();

        public final Template.Method<String> getName = new Template.Method<String>();

    }

}

