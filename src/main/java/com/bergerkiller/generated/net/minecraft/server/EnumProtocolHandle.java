package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.google.common.collect.BiMap;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumProtocol</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EnumProtocolHandle extends Template.Handle {
    /** @See {@link EnumProtocolClass} */
    public static final EnumProtocolClass T = new EnumProtocolClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumProtocolHandle.class, "net.minecraft.server.EnumProtocol");

    public static final EnumProtocolHandle PLAY = T.PLAY.getSafe();
    /* ============================================================================== */

    public static EnumProtocolHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public Class<?> getPacketClassIn(int id) {
        return (Class<?>) getPacketMap().get(EnumProtocolDirectionHandle.SERVERBOUND).get(id);
    }

    public Class<?> getPacketClassOut(int id) {
        return (Class<?>) getPacketMap().get(EnumProtocolDirectionHandle.CLIENTBOUND).get(id);
    }

    public int getPacketIdIn(Class<?> packetClass) {
        Integer id = (Integer) getPacketMap().get(EnumProtocolDirectionHandle.SERVERBOUND).inverse().get(packetClass);
        return (id == null) ? -1 : id.intValue();
    }

    public int getPacketIdOut(Class<?> packetClass) {
        Integer id = (Integer) getPacketMap().get(EnumProtocolDirectionHandle.CLIENTBOUND).inverse().get(packetClass);
        return (id == null) ? -1 : id.intValue();
    }
    @SuppressWarnings("rawtypes")
    public abstract Map<EnumProtocolDirectionHandle, BiMap> getPacketMap();
    @SuppressWarnings("rawtypes")
    public abstract void setPacketMap(Map<EnumProtocolDirectionHandle, BiMap> value);
    /**
     * Stores class members for <b>net.minecraft.server.EnumProtocol</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumProtocolClass extends Template.Class<EnumProtocolHandle> {
        public final Template.EnumConstant.Converted<EnumProtocolHandle> PLAY = new Template.EnumConstant.Converted<EnumProtocolHandle>();

        @SuppressWarnings("rawtypes")
        public final Template.Field.Converted<Map<EnumProtocolDirectionHandle, BiMap>> packetMap = new Template.Field.Converted<Map<EnumProtocolDirectionHandle, BiMap>>();

    }

}

