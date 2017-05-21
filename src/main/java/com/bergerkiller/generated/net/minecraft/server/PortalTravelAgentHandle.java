package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;

public class PortalTravelAgentHandle extends Template.Handle {
    public static final PortalTravelAgentClass T = new PortalTravelAgentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PortalTravelAgentHandle.class, "net.minecraft.server.PortalTravelAgent");


    /* ============================================================================== */

    public static PortalTravelAgentHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PortalTravelAgentHandle handle = new PortalTravelAgentHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void adjustExit(Entity entity, Location position, Vector velocity) {
        T.adjustExit.invoke(instance, entity, position, velocity);
    }

    public static final class PortalTravelAgentClass extends Template.Class<PortalTravelAgentHandle> {
        public final Template.Method.Converted<Void> adjustExit = new Template.Method.Converted<Void>();

    }
}
