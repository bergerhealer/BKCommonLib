package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket")
public abstract class ClientboundPlayerRotationPacketHandle extends PacketHandle {
    /** @see ClientboundPlayerRotationPacketClass */
    public static final ClientboundPlayerRotationPacketClass T = Template.Class.create(ClientboundPlayerRotationPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundPlayerRotationPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static boolean isRelativeSupported() {
        return T.isRelativeSupported.invoker.invoke(null);
    }

    public static ClientboundPlayerRotationPacketHandle createNew(float yaw, boolean isYawRelative, float pitch, boolean isPitchRelative) {
        return T.createNew.invoke(yaw, isYawRelative, pitch, isPitchRelative);
    }

    public abstract float getYaw();
    public abstract boolean isYawRelative();
    public abstract float getPitch();
    public abstract boolean isPitchRelative();
    public static ClientboundPlayerRotationPacketHandle createAbsolute(float yaw, float pitch) {
        return createNew(yaw, false, pitch, false);
    }

    public static ClientboundPlayerRotationPacketHandle createRelative(float yaw, float pitch) {
        return createNew(yaw, true, pitch, true);
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundPlayerRotationPacketClass extends Template.Class<ClientboundPlayerRotationPacketHandle> {
        public final Template.StaticMethod<Boolean> isRelativeSupported = new Template.StaticMethod<Boolean>();
        public final Template.StaticMethod.Converted<ClientboundPlayerRotationPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundPlayerRotationPacketHandle>();

        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Boolean> isYawRelative = new Template.Method<Boolean>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Boolean> isPitchRelative = new Template.Method<Boolean>();

    }

}

