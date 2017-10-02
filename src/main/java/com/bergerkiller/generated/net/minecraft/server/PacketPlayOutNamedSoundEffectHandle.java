package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutNamedSoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutNamedSoundEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutNamedSoundEffectClass} */
    public static final PacketPlayOutNamedSoundEffectClass T = new PacketPlayOutNamedSoundEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutNamedSoundEffectHandle.class, "net.minecraft.server.PacketPlayOutNamedSoundEffect");

    /* ============================================================================== */

    public static PacketPlayOutNamedSoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public float getPitch() {
        if (T.pitch_1_10_2.isAvailable()) {
            return T.pitch_1_10_2.getFloat(getRaw());
        } else {
            return (float) T.pitch_1_8_8.getInteger(getRaw()) / 63.0f;
        }
    }

    public void setPitch(float pitch) {
        if (T.pitch_1_10_2.isAvailable()) {
            T.pitch_1_10_2.setFloat(getRaw(), pitch);
        } else {
            T.pitch_1_8_8.setInteger(getRaw(), (int) (pitch * 63.0f));
        }
    }


    public String getCategory() {
        if (T.category_1_10_2.isAvailable()) {
            return T.category_1_10_2.get(getRaw());
        } else {
            return "master";
        }
    }

    public void setCategory(String categoryName) {
        if (T.category_1_10_2.isAvailable()) {
            T.category_1_10_2.set(getRaw(), categoryName);
        } else {
        }
    }
    public ResourceKey getSound() {
        return T.sound.get(getRaw());
    }

    public void setSound(ResourceKey value) {
        T.sound.set(getRaw(), value);
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

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutNamedSoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutNamedSoundEffectClass extends Template.Class<PacketPlayOutNamedSoundEffectHandle> {
        public final Template.Field.Converted<ResourceKey> sound = new Template.Field.Converted<ResourceKey>();
        @Template.Optional
        public final Template.Field.Converted<String> category_1_10_2 = new Template.Field.Converted<String>();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.Field.Float volume = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Integer pitch_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Float pitch_1_10_2 = new Template.Field.Float();

    }

}

