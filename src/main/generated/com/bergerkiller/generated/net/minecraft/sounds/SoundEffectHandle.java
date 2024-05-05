package com.bergerkiller.generated.net.minecraft.sounds;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.sounds.SoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.sounds.SoundEffect")
public abstract class SoundEffectHandle extends Template.Handle {
    /** @see SoundEffectClass */
    public static final SoundEffectClass T = Template.Class.create(SoundEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static SoundEffectHandle createVariableRangeEvent(MinecraftKeyHandle minecraftkey) {
        return T.createVariableRangeEvent.invoke(minecraftkey);
    }

    public static SoundEffectHandle byName(String name) {
        return T.byName.invoke(name);
    }

    public static SoundEffectHandle byKey(MinecraftKeyHandle key) {
        return T.byKey.invoke(key);
    }

    public static Collection<MinecraftKeyHandle> getSoundNames() {
        return T.getSoundNames.invoke();
    }

    @Deprecated
    public static SoundEffectHandle createNew(MinecraftKeyHandle name) {
        return createVariableRangeEvent(name);
    }
    public abstract MinecraftKeyHandle getName();
    public abstract void setName(MinecraftKeyHandle value);
    /**
     * Stores class members for <b>net.minecraft.sounds.SoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectClass extends Template.Class<SoundEffectHandle> {
        public final Template.Field.Converted<MinecraftKeyHandle> name = new Template.Field.Converted<MinecraftKeyHandle>();

        public final Template.StaticMethod.Converted<SoundEffectHandle> createVariableRangeEvent = new Template.StaticMethod.Converted<SoundEffectHandle>();
        public final Template.StaticMethod.Converted<SoundEffectHandle> byName = new Template.StaticMethod.Converted<SoundEffectHandle>();
        public final Template.StaticMethod.Converted<SoundEffectHandle> byKey = new Template.StaticMethod.Converted<SoundEffectHandle>();
        @Template.Optional
        public final Template.StaticMethod<Object> rawSoundEffectResourceKeyToHolder = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<Collection<MinecraftKeyHandle>> getSoundNames = new Template.StaticMethod.Converted<Collection<MinecraftKeyHandle>>();

    }

}

