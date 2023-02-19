package com.bergerkiller.generated.net.minecraft.sounds;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.core.RegistryMaterialsHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.sounds.SoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.sounds.SoundEffect")
public abstract class SoundEffectHandle extends Template.Handle {
    /** @See {@link SoundEffectClass} */
    public static final SoundEffectClass T = Template.Class.create(SoundEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static SoundEffectHandle createVariableRangeEvent(MinecraftKeyHandle minecraftkey) {
        return T.createVariableRangeEvent.invoke(minecraftkey);
    }


    @Deprecated
    public static SoundEffectHandle createNew(MinecraftKeyHandle name) {
        return createVariableRangeEvent(name);
    }

    public static SoundEffectHandle byName(String name) {
        if (T.opt_getRegistry.isAvailable()) {
            Object mc_key_raw = com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle.T.createNew.raw.invoke(name);
            if (mc_key_raw != null) {
                return createHandle(T.opt_getRegistry.invoke().get(mc_key_raw));
            } else {
                return null;
            }
        } else {
            return createVariableRangeEvent(com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle.createNew(name));
        }
    }
    public abstract MinecraftKeyHandle getName();
    public abstract void setName(MinecraftKeyHandle value);
    /**
     * Stores class members for <b>net.minecraft.sounds.SoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectClass extends Template.Class<SoundEffectHandle> {
        public final Template.Field.Converted<MinecraftKeyHandle> name = new Template.Field.Converted<MinecraftKeyHandle>();

        @Template.Optional
        public final Template.StaticMethod.Converted<RegistryMaterialsHandle> opt_getRegistry = new Template.StaticMethod.Converted<RegistryMaterialsHandle>();
        public final Template.StaticMethod.Converted<SoundEffectHandle> createVariableRangeEvent = new Template.StaticMethod.Converted<SoundEffectHandle>();

    }

}

