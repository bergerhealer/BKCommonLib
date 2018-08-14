package com.bergerkiller.bukkit.common.io;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Rewrites Java Class byte data before a class is loaded, allowing changes to be made to the bytecode
 * at runtime.
 */
public interface ClassRewriter {

    /**
     * Called when a class needs to be rewritten, right before loading it.
     * 
     * @param plugin from which the class was loaded
     * @param name of the class being loaded
     * @param classBytes of the class, previously read from the plugin
     * @return bytes result to load into the Class Loader
     */
    byte[] rewrite(JavaPlugin plugin, String name, byte[] classBytes);
}
