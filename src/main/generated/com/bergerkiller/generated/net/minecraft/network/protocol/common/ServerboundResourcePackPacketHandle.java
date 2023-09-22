package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ServerboundResourcePackPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ServerboundResourcePackPacket")
public abstract class ServerboundResourcePackPacketHandle extends PacketHandle {
    /** @see ServerboundResourcePackPacketClass */
    public static final ServerboundResourcePackPacketClass T = Template.Class.create(ServerboundResourcePackPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundResourcePackPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getStatus();
    public abstract void setStatus(Object value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ServerboundResourcePackPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundResourcePackPacketClass extends Template.Class<ServerboundResourcePackPacketHandle> {
        @Template.Optional
        public final Template.Field<String> message = new Template.Field<String>();
        public final Template.Field.Converted<Object> status = new Template.Field.Converted<Object>();

    }

}

