package com.bergerkiller.generated.net.minecraft.server;

import com.google.common.collect.BiMap;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.Map;

public class EnumProtocolHandle extends Template.Handle {
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

    public Map<EnumProtocolDirectionHandle, BiMap> getPacketMap() {
        return T.packetMap.get(instance);
    }

    public void setPacketMap(Map<EnumProtocolDirectionHandle, BiMap> value) {
        T.packetMap.set(instance, value);
    }

    public static final class EnumProtocolClass extends Template.Class<EnumProtocolHandle> {
        public final Template.EnumConstant.Converted<EnumProtocolHandle> PLAY = new Template.EnumConstant.Converted<EnumProtocolHandle>();

        public final Template.Field.Converted<Map<EnumProtocolDirectionHandle, BiMap>> packetMap = new Template.Field.Converted<Map<EnumProtocolDirectionHandle, BiMap>>();

    }

}

