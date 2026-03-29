package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket")
public abstract class ServerboundPlayerAbilitiesPacketHandle extends PacketHandle {
    /** @see ServerboundPlayerAbilitiesPacketClass */
    public static final ServerboundPlayerAbilitiesPacketClass T = Template.Class.create(ServerboundPlayerAbilitiesPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundPlayerAbilitiesPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isFlying();
    public abstract void setIsFlying(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundPlayerAbilitiesPacketClass extends Template.Class<ServerboundPlayerAbilitiesPacketHandle> {
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();

    }

}

