package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

/**
 * ArcLight spigot + forge server.
 * Used for 1.16 build #156 and later.
 * Since this build, Arclight exposes a remapped version of NMS bytecode.
 * This allows our normal reflection and code generation to function as expected.<br>
 * <br>
 * https://github.com/IzzelAliz/Arclight
 */
public class ArclightServer extends SpigotServer {

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Arclight server, we expect this Class to exist
        try {
            Class.forName("io.izzel.arclight.common.ArclightMain");
        } catch (Throwable t) {
            return false;
        }

        // If this class exists, this is a modern version that remaps the nms byte[] source
        try {
            Class.forName("io.izzel.arclight.common.mod.util.remapper.resource.RemapSourceHandler");
        } catch (Throwable t) {
            return false;
        }

        return true;
    }

    @Override
    public void postInit(PostInitEvent event) {
        Resolver.setClassLoaderRemappingEnabled(true);
        super.postInit(event);
    }

    @Override
    public String getServerName() {
        return "Arclight";
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        // The .class data at this path contains obfuscated type information
        // These obfuscated names are deobufscated at runtime
        // This difference causes compiler errors at runtime, so instead of
        // loading the .class files, inspect the signatures using reflection.
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }

        // NMS World class 'entitiesById' has different field modifiers in bytecode than loaded class
        if (classPath.startsWith("net.minecraft.server.")) {
            return false;
        }

        return false;
    }

    @Override
    public Collection<String> getLoadableWorlds() {
        return ForgeSupport.bukkit().getLoadableWorlds();
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return ForgeSupport.bukkit().isLoadableWorld(worldName);
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldRegionFolder(worldName);
    }

    @Override
    public File getWorldFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldFolder(worldName);
    }

    @Override
    public File getWorldLevelFile(String worldName) {
        return ForgeSupport.bukkit().getWorldLevelFile(worldName);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "arclight");
    }
}
