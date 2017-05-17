package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.UUID;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public class WorldServerHandle extends Template.Handle {
    public static final WorldServerClass T = new WorldServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldServerHandle.class, "net.minecraft.server.WorldServer");


    /* ============================================================================== */

    public static WorldServerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldServerHandle handle = new WorldServerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Map<UUID, EntityHandle> getEntitiesByUUID() {
        return T.entitiesByUUID.get(instance);
    }

    public void setEntitiesByUUID(Map<UUID, EntityHandle> value) {
        T.entitiesByUUID.set(instance, value);
    }

    public static final class WorldServerClass extends Template.Class<WorldServerHandle> {
        public final Template.Field.Converted<Map<UUID, EntityHandle>> entitiesByUUID = new Template.Field.Converted<Map<UUID, EntityHandle>>();

    }
}
