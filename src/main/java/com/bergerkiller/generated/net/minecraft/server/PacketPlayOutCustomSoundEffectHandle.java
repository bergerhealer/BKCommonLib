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
public class PacketPlayOutCustomSoundEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutCustomSoundEffectClass} */
    public static final PacketPlayOutCustomSoundEffectClass T = new PacketPlayOutCustomSoundEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutCustomSoundEffectHandle.class, "net.minecraft.server.PacketPlayOutCustomSoundEffect");

    /* ============================================================================== */

    public static PacketPlayOutCustomSoundEffectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutCustomSoundEffectHandle handle = new PacketPlayOutCustomSoundEffectHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public ResourceKey getSound() {
        return T.sound.get(instance);
    }

    public void setSound(ResourceKey value) {
        T.sound.set(instance, value);
    }

    public String getCategory() {
        return T.category.get(instance);
    }

    public void setCategory(String value) {
        T.category.set(instance, value);
    }

    public int getX() {
        return T.x.getInteger(instance);
    }

    public void setX(int value) {
        T.x.setInteger(instance, value);
    }

    public int getY() {
        return T.y.getInteger(instance);
    }

    public void setY(int value) {
        T.y.setInteger(instance, value);
    }

    public int getZ() {
        return T.z.getInteger(instance);
    }

    public void setZ(int value) {
        T.z.setInteger(instance, value);
    }

    public float getVolume() {
        return T.volume.getFloat(instance);
    }

    public void setVolume(float value) {
        T.volume.setFloat(instance, value);
    }

    public float getPitch() {
        return T.pitch.getFloat(instance);
    }

    public void setPitch(float value) {
        T.pitch.setFloat(instance, value);
    }

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

