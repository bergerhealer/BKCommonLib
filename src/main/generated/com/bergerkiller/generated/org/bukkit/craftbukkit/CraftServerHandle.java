package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.dedicated.DedicatedPlayerListHandle;
import org.bukkit.command.SimpleCommandMap;
import java.io.File;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.CraftServer")
public abstract class CraftServerHandle extends Template.Handle {
    /** @see CraftServerClass */
    public static final CraftServerClass T = Template.Class.create(CraftServerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract SimpleCommandMap getCommandMap();
    public abstract DedicatedPlayerListHandle getPlayerList();
    public abstract MinecraftServerHandle getServer();
    public abstract File getPluginsDirectory();
    private static CraftServerHandle _instance = null;
    public static CraftServerHandle instance() {
        if (_instance == null) {
            _instance = createHandle(org.bukkit.Bukkit.getServer());
        }
        return _instance;
    }
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftServerClass extends Template.Class<CraftServerHandle> {
        public final Template.Method<SimpleCommandMap> getCommandMap = new Template.Method<SimpleCommandMap>();
        public final Template.Method.Converted<DedicatedPlayerListHandle> getPlayerList = new Template.Method.Converted<DedicatedPlayerListHandle>();
        public final Template.Method.Converted<MinecraftServerHandle> getServer = new Template.Method.Converted<MinecraftServerHandle>();
        public final Template.Method<File> getPluginsDirectory = new Template.Method<File>();

    }

}

