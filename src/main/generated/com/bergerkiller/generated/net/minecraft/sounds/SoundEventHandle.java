package com.bergerkiller.generated.net.minecraft.sounds;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.sounds.SoundEvent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.sounds.SoundEvent")
public abstract class SoundEventHandle extends Template.Handle {
    /** @see SoundEventClass */
    public static final SoundEventClass T = Template.Class.create(SoundEventClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundEventHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static SoundEventHandle createVariableRangeEvent(IdentifierHandle minecraftkey) {
        return T.createVariableRangeEvent.invoke(minecraftkey);
    }

    public static SoundEventHandle byName(String name) {
        return T.byName.invoke(name);
    }

    public static SoundEventHandle byKey(IdentifierHandle key) {
        return T.byKey.invoke(key);
    }

    public static Collection<IdentifierHandle> getSoundNames() {
        return T.getSoundNames.invoke();
    }

    @Deprecated
    public static SoundEventHandle createNew(IdentifierHandle name) {
        return createVariableRangeEvent(name);
    }
    public abstract IdentifierHandle getName();
    public abstract void setName(IdentifierHandle value);
    /**
     * Stores class members for <b>net.minecraft.sounds.SoundEvent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEventClass extends Template.Class<SoundEventHandle> {
        public final Template.Field.Converted<IdentifierHandle> name = new Template.Field.Converted<IdentifierHandle>();

        public final Template.StaticMethod.Converted<SoundEventHandle> createVariableRangeEvent = new Template.StaticMethod.Converted<SoundEventHandle>();
        public final Template.StaticMethod.Converted<SoundEventHandle> byName = new Template.StaticMethod.Converted<SoundEventHandle>();
        public final Template.StaticMethod.Converted<SoundEventHandle> byKey = new Template.StaticMethod.Converted<SoundEventHandle>();
        @Template.Optional
        public final Template.StaticMethod<Object> rawSoundEffectResourceKeyToHolder = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<Collection<IdentifierHandle>> getSoundNames = new Template.StaticMethod.Converted<Collection<IdentifierHandle>>();

    }

}

