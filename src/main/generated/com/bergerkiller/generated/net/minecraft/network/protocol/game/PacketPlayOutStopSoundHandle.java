package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutStopSound</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutStopSound")
public abstract class PacketPlayOutStopSoundHandle extends PacketHandle {
    /** @see PacketPlayOutStopSoundClass */
    public static final PacketPlayOutStopSoundClass T = Template.Class.create(PacketPlayOutStopSoundClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutStopSoundHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutStopSoundHandle createNew(ResourceKey<SoundEffect> soundEffect, String category) {
        return T.createNew.invoke(soundEffect, category);
    }

    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutStopSound</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutStopSoundClass extends Template.Class<PacketPlayOutStopSoundHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutStopSoundHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutStopSoundHandle>();

    }

}

