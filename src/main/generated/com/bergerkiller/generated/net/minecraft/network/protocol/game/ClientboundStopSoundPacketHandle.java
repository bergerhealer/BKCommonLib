package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundStopSoundPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundStopSoundPacket")
public abstract class ClientboundStopSoundPacketHandle extends PacketHandle {
    /** @see ClientboundStopSoundPacketClass */
    public static final ClientboundStopSoundPacketClass T = Template.Class.create(ClientboundStopSoundPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundStopSoundPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundStopSoundPacketHandle createNew(ResourceKey<SoundEffect> soundEffect, String category) {
        return T.createNew.invoke(soundEffect, category);
    }

    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundStopSoundPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundStopSoundPacketClass extends Template.Class<ClientboundStopSoundPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundStopSoundPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundStopSoundPacketHandle>();

    }

}

