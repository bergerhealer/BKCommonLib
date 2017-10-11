package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;

/**
 * Stores information for accessing a resource, such as a sound, texture, entity, block, etc.
 */
public class ResourceKey extends BasicWrapper<MinecraftKeyHandle> {

    private ResourceKey(MinecraftKeyHandle keyHandle) {
        this.setHandle(keyHandle);
    }

    /**
     * Gets the resource key path
     * 
     * @return path
     */
    public String getPath() {
        return handle.toString();
    }

    /**
     * Gets the backing Minecraft Key for this resource.
     * 
     * @return Minecraft key
     */
    public MinecraftKeyHandle toMinecraftKey() {
        return handle;
    }

    /**
     * Constructs a new Resource Key taking information from a backing minecraft key token.
     * 
     * @param minecraftKey
     * @return resource key
     */
    public static ResourceKey fromMinecraftKey(MinecraftKeyHandle minecraftKey) {
        return new ResourceKey(minecraftKey);
    }

    /**
     * Constructs a new Resource Key taking information from a key String token.
     * On MC 1.8.8, this key is stored as is. On MC 1.10.2 and onwards, the key is transformed
     * into a MinecraftKey before use.
     * 
     * @param key
     * @return resource key
     */
    public static ResourceKey fromPath(String key) {
        return new ResourceKey(MinecraftKeyHandle.createNew(key));
    }

}
