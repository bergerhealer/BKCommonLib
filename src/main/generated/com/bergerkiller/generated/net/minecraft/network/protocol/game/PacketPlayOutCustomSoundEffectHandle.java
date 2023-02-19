package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffect")
public abstract class PacketPlayOutCustomSoundEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutCustomSoundEffectClass} */
    public static final PacketPlayOutCustomSoundEffectClass T = Template.Class.create(PacketPlayOutCustomSoundEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutCustomSoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public void setPitch(float pitch) {
        if (T.opt_pitch_float.isAvailable()) {
            T.opt_pitch_float.setFloat(getRaw(), pitch);
        } else {
            T.opt_pitch_integer.setInteger(getRaw(), (int) (pitch * 63.0f));
        }
    }

    public float getPitch() {
        if (T.opt_pitch_float.isAvailable()) {
            return T.opt_pitch_float.getFloat(getRaw());
        } else {
            return (float) T.opt_pitch_integer.getInteger(getRaw()) / 63.0f;
        }
    }
    public abstract ResourceKey<SoundEffect> getSound();
    public abstract void setSound(ResourceKey<SoundEffect> value);
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
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCustomSoundEffectClass extends Template.Class<PacketPlayOutCustomSoundEffectHandle> {
        public final Template.Field.Converted<ResourceKey<SoundEffect>> sound = new Template.Field.Converted<ResourceKey<SoundEffect>>();
        public final Template.Field.Converted<String> category = new Template.Field.Converted<String>();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.Field.Float volume = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Float opt_pitch_float = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Integer opt_pitch_integer = new Template.Field.Integer();

    }

}

