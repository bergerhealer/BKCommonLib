package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PortalTravelAgent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PortalTravelAgentHandle extends Template.Handle {
    /** @See {@link PortalTravelAgentClass} */
    public static final PortalTravelAgentClass T = new PortalTravelAgentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PortalTravelAgentHandle.class, "net.minecraft.server.PortalTravelAgent");

    /* ============================================================================== */

    public static PortalTravelAgentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public void adjustExit(Entity entity, Location position, Vector velocity) {
        T.adjustExit.invoke(getRaw(), entity, position, velocity);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PortalTravelAgent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PortalTravelAgentClass extends Template.Class<PortalTravelAgentHandle> {
        public final Template.Method.Converted<Void> adjustExit = new Template.Method.Converted<Void>();

    }

}

