package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class MinecraftServerHandle extends Template.Handle {
    public static final MinecraftServerClass T = new MinecraftServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MinecraftServerHandle.class, "net.minecraft.server.MinecraftServer");


    /* ============================================================================== */

    public static MinecraftServerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MinecraftServerHandle handle = new MinecraftServerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static MinecraftServerHandle instance() {
        return com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle.instance().getServer();
    }

    public List<WorldServerHandle> getWorlds() {
        return T.worlds.get(instance);
    }

    public void setWorlds(List<WorldServerHandle> value) {
        T.worlds.set(instance, value);
    }

    public static final class MinecraftServerClass extends Template.Class<MinecraftServerHandle> {
        public final Template.Field.Converted<List<WorldServerHandle>> worlds = new Template.Field.Converted<List<WorldServerHandle>>();

    }
}
