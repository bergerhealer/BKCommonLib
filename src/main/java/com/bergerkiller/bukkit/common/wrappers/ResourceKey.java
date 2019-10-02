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
     * Returns null if the input minecraft key is null.
     * 
     * @param minecraftKey
     * @return resource key, null if minecraftkey is null
     */
    public static ResourceKey fromMinecraftKey(MinecraftKeyHandle minecraftKey) {
        if (minecraftKey != null) {
            return new ResourceKey(minecraftKey);
        } else {
            return null;
        }
    }

    /**
     * Constructs a new Resource Key taking information from a key String token.<br>
     * <br>
     * The key may only contain the characters: [a-z0-9/._-]<br>
     * No uppercase characters are allowed in the path.
     * If the input key is invalid, null is returned.
     * 
     * @param key
     * @return resource key, null if the key contains invalid characters
     */
    public static ResourceKey fromPath(String key) {
        return fromMinecraftKey(MinecraftKeyHandle.createNew(key));
    }

    /**
     * Constructs a new Resource Key taking information from a key String token,
     * made out of the namespace and name parts.<br>
     * <br>
     * The key may only contain the characters: [a-z0-9/._-]<br>
     * No uppercase characters are allowed in the path.
     * If the input key is invalid, null is returned.
     * 
     * @param namespace
     * @param name
     * @return resource key, null if the key contains invalid characters
     */
    public static ResourceKey fromPath(String namespace, String name) {
        return fromMinecraftKey(MinecraftKeyHandle.createNew(namespace, name));
    }
}
