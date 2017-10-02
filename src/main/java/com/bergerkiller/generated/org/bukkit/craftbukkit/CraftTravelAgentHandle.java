package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.PortalTravelAgentHandle;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftTravelAgent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftTravelAgentHandle extends PortalTravelAgentHandle {
    /** @See {@link CraftTravelAgentClass} */
    public static final CraftTravelAgentClass T = new CraftTravelAgentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftTravelAgentHandle.class, "org.bukkit.craftbukkit.CraftTravelAgent");

    /* ============================================================================== */

    public static CraftTravelAgentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final CraftTravelAgentHandle createNew(World worldserver) {
        return T.constr_worldserver.newInstance(worldserver);
    }

    /* ============================================================================== */

    public void setCanCreatePortal(boolean create) {
        T.setCanCreatePortal.invoke(getRaw(), create);
    }

    public Location findOrCreate(Location target) {
        return T.findOrCreate.invoke(getRaw(), target);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftTravelAgent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftTravelAgentClass extends Template.Class<CraftTravelAgentHandle> {
        public final Template.Constructor.Converted<CraftTravelAgentHandle> constr_worldserver = new Template.Constructor.Converted<CraftTravelAgentHandle>();

        public final Template.Method<Void> setCanCreatePortal = new Template.Method<Void>();
        public final Template.Method<Location> findOrCreate = new Template.Method<Location>();

    }

}

