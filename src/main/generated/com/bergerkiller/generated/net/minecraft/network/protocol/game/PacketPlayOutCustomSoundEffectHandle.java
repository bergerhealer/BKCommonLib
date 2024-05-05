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
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffect")
public abstract class PacketPlayOutCustomSoundEffectHandle extends PacketHandle {
    /** @see PacketPlayOutCustomSoundEffectClass */
    public static final PacketPlayOutCustomSoundEffectClass T = Template.Class.create(PacketPlayOutCustomSoundEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutCustomSoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutCustomSoundEffectHandle createNew(ResourceKey<SoundEffect> soundEffect, String category, double x, double y, double z, float volume, float pitch, long randomSeed) {
        return T.createNew.invokeVA(soundEffect, category, x, y, z, volume, pitch, randomSeed);
    }

    public abstract String getCategory();
    public abstract void setCategory(String category);
    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();
    public abstract void setX(double x);
    public abstract void setY(double y);
    public abstract void setZ(double z);
    public abstract void setPitch(float pitch);
    public abstract float getPitch();
    public abstract long getRandomSeed();
    public abstract void setRandomSeed(long seed);

    public static final java.util.Random SOUND_RANDOM_SEED_SOURCE = new java.util.Random();

    public static PacketPlayOutCustomSoundEffectHandle createNew(com.bergerkiller.bukkit.common.resources.ResourceKey<com.bergerkiller.bukkit.common.resources.SoundEffect> soundEffect, String category, org.bukkit.Location location, float volume, float pitch) {
        return createNew(soundEffect, category, location.getX(), location.getY(), location.getZ(), volume, pitch);
    }

    public static PacketPlayOutCustomSoundEffectHandle createNew(com.bergerkiller.bukkit.common.resources.ResourceKey<com.bergerkiller.bukkit.common.resources.SoundEffect> soundEffect, String category, double x, double y, double z, float volume, float pitch) {
        long randomSeed = SOUND_RANDOM_SEED_SOURCE.nextLong();
        return createNew(soundEffect, category, x, y, z, volume, pitch, randomSeed);
    }


    @Deprecated
    public static PacketPlayOutCustomSoundEffectHandle createNew(com.bergerkiller.bukkit.common.resources.ResourceKey<com.bergerkiller.bukkit.common.resources.SoundEffect> soundEffect, String category, org.bukkit.World world, double x, double y, double z, float volume, float pitch) {
        return createNew(soundEffect, category, x, y, z, volume, pitch);
    }
    public abstract ResourceKey<SoundEffect> getSound();
    public abstract void setSound(ResourceKey<SoundEffect> value);
    public abstract float getVolume();
    public abstract void setVolume(float value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCustomSoundEffectClass extends Template.Class<PacketPlayOutCustomSoundEffectHandle> {
        public final Template.Field.Converted<ResourceKey<SoundEffect>> sound = new Template.Field.Converted<ResourceKey<SoundEffect>>();
        public final Template.Field.Float volume = new Template.Field.Float();

        public final Template.StaticMethod.Converted<PacketPlayOutCustomSoundEffectHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutCustomSoundEffectHandle>();

        public final Template.Method<String> getCategory = new Template.Method<String>();
        public final Template.Method<Void> setCategory = new Template.Method<Void>();
        public final Template.Method<Double> getX = new Template.Method<Double>();
        public final Template.Method<Double> getY = new Template.Method<Double>();
        public final Template.Method<Double> getZ = new Template.Method<Double>();
        public final Template.Method<Void> setX = new Template.Method<Void>();
        public final Template.Method<Void> setY = new Template.Method<Void>();
        public final Template.Method<Void> setZ = new Template.Method<Void>();
        public final Template.Method<Void> setPitch = new Template.Method<Void>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Long> getRandomSeed = new Template.Method<Long>();
        public final Template.Method<Void> setRandomSeed = new Template.Method<Void>();

    }

}

