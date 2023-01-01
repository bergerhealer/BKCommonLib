package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

/**
 * Magma spigot + forge server.
 * Used for 1.18 versions and later which use the same Arclight remapper.
 * Since this build, Magma exposes a remapped version of NMS bytecode.
 * This allows our normal reflection and code generation to function as expected.<br>
 * <br>
 * https://magmafoundation.org/
 */
public class MagmaServer extends SpigotServer {

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Magma server, we expect this Class to exist
        try {
            Class.forName("org.magmafoundation.magma.Magma");
        } catch (Throwable t) {
            return false;
        }

        // If this class exists, this is a modern version that remaps the nms byte[] source
        try {
            Class.forName("org.magmafoundation.magma.remapping.handlers.RemapSourceHandler");
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
        return "Magma";
    }

    @Override
    public boolean isForgeServer() {
        return true;
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
        if (classPath.startsWith("net.minecraft.")) {
            return false;
        }

        return true;
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
        variables.put("forge", "magma");
    }
}
