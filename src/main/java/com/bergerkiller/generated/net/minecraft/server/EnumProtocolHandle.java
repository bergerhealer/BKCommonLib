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
public class EnumProtocolHandle extends Template.Handle {
    /** @See {@link EnumProtocolClass} */
    public static final EnumProtocolClass T = new EnumProtocolClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumProtocolHandle.class, "net.minecraft.server.EnumProtocol");

    public static final EnumProtocolHandle PLAY = T.PLAY.getSafe();
    /* ============================================================================== */

    public static EnumProtocolHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumProtocolHandle handle = new EnumProtocolHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    @SuppressWarnings("rawtypes")
    public Map<EnumProtocolDirectionHandle, BiMap> getPacketMap() {
        return T.packetMap.get(instance);
    }

    @SuppressWarnings("rawtypes")
    public void setPacketMap(Map<EnumProtocolDirectionHandle, BiMap> value) {
        T.packetMap.set(instance, value);
    }

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

