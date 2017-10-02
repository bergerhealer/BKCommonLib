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
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public ResourceKey getSound() {
        return T.sound.get(getRaw());
    }

    public void setSound(ResourceKey value) {
        T.sound.set(getRaw(), value);
    }

    public String getCategory() {
        return T.category.get(getRaw());
    }

    public void setCategory(String value) {
        T.category.set(getRaw(), value);
    }

    public int getX() {
        return T.x.getInteger(getRaw());
    }

    public void setX(int value) {
        T.x.setInteger(getRaw(), value);
    }

    public int getY() {
        return T.y.getInteger(getRaw());
    }

    public void setY(int value) {
        T.y.setInteger(getRaw(), value);
    }

    public int getZ() {
        return T.z.getInteger(getRaw());
    }

    public void setZ(int value) {
        T.z.setInteger(getRaw(), value);
    }

    public float getVolume() {
        return T.volume.getFloat(getRaw());
    }

    public void setVolume(float value) {
        T.volume.setFloat(getRaw(), value);
    }

    public float getPitch() {
        return T.pitch.getFloat(getRaw());
    }

    public void setPitch(float value) {
        T.pitch.setFloat(getRaw(), value);
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

