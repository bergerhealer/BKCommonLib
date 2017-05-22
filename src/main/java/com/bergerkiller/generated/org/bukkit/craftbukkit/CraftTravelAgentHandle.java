package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.World;
import com.bergerkiller.generated.net.minecraft.server.PortalTravelAgentHandle;

public class CraftTravelAgentHandle extends PortalTravelAgentHandle {
    public static final CraftTravelAgentClass T = new CraftTravelAgentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftTravelAgentHandle.class, "org.bukkit.craftbukkit.CraftTravelAgent");


    /* ============================================================================== */

    public static CraftTravelAgentHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftTravelAgentHandle handle = new CraftTravelAgentHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final CraftTravelAgentHandle createNew(World worldserver) {
        return T.constr_worldserver.newInstance(worldserver);
    }

    /* ============================================================================== */

    public void setCanCreatePortal(boolean create) {
        T.setCanCreatePortal.invoke(instance, create);
    }

    public Location findOrCreate(Location target) {
        return T.findOrCreate.invoke(instance, target);
    }

    public static final class CraftTravelAgentClass extends Template.Class<CraftTravelAgentHandle> {
        public final Template.Constructor.Converted<CraftTravelAgentHandle> constr_worldserver = new Template.Constructor.Converted<CraftTravelAgentHandle>();

        public final Template.Method<Void> setCanCreatePortal = new Template.Method<Void>();
        public final Template.Method<Location> findOrCreate = new Template.Method<Location>();

    }
}
