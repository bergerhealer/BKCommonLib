package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInBlockPlace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayInBlockPlaceHandle extends PacketHandle {
    /** @See {@link PacketPlayInBlockPlaceClass} */
    public static final PacketPlayInBlockPlaceClass T = new PacketPlayInBlockPlaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInBlockPlaceHandle.class, "net.minecraft.server.PacketPlayInBlockPlace");

    /* ============================================================================== */

    public static PacketPlayInBlockPlaceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.IN_BLOCK_PLACE;
    }

    public void setTimestamp(long timestamp) {
        if (T.timestamp.isAvailable()) {
            T.timestamp.setLong(getRaw(), timestamp);
        }
    }

    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return internalGetHand(T.enumHand, humanEntity);
    }

    public void setHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        internalSetHand(T.enumHand, humanEntity, hand);
    }
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInBlockPlace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInBlockPlaceClass extends Template.Class<PacketPlayInBlockPlaceHandle> {
        @Template.Optional
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();

    }

}

