package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PortalTravelAgent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PortalTravelAgentHandle extends Template.Handle {
    /** @See {@link PortalTravelAgentClass} */
    public static final PortalTravelAgentClass T = new PortalTravelAgentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PortalTravelAgentHandle.class, "net.minecraft.server.PortalTravelAgent", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PortalTravelAgentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PortalTravelAgentHandle createNew(World world) {
        return T.constr_world.newInstance(world);
    }

    /* ============================================================================== */

    public abstract Location findPortal(Location startLocation);
    public abstract boolean createPortal(double x, double y, double z);
    @Template.Readonly
    public abstract World getWorld();
    /**
     * Stores class members for <b>net.minecraft.server.PortalTravelAgent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PortalTravelAgentClass extends Template.Class<PortalTravelAgentHandle> {
        public final Template.Constructor.Converted<PortalTravelAgentHandle> constr_world = new Template.Constructor.Converted<PortalTravelAgentHandle>();

        @Template.Readonly
        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();

        public final Template.Method<Location> findPortal = new Template.Method<Location>();
        public final Template.Method<Boolean> createPortal = new Template.Method<Boolean>();

    }

}

