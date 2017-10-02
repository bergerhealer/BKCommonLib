package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutCustomSoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class PacketPlayOutCustomSoundEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutCustomSoundEffectClass} */
    public static final PacketPlayOutCustomSoundEffectClass T = new PacketPlayOutCustomSoundEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutCustomSoundEffectHandle.class, "net.minecraft.server.PacketPlayOutCustomSoundEffect");

    /* ============================================================================== */

    public static PacketPlayOutCustomSoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ResourceKey getSound();
    public abstract void setSound(ResourceKey value);
    public abstract String getCategory();
    public abstract void setCategory(String value);
    public abstract int getX();
    public abstract void setX(int value);
    public abstract int getY();
    public abstract void setY(int value);
    public abstract int getZ();
    public abstract void setZ(int value);
    public abstract float getVolume();
    public abstract void setVolume(float value);
    public abstract float getPitch();
    public abstract void setPitch(float value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutCustomSoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCustomSoundEffectClass extends Template.Class<PacketPlayOutCustomSoundEffectHandle> {
        public final Template.Field.Converted<ResourceKey> sound = new Template.Field.Converted<ResourceKey>();
        public final Template.Field.Converted<String> category = new Template.Field.Converted<String>();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.Field.Float volume = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();

    }

}

