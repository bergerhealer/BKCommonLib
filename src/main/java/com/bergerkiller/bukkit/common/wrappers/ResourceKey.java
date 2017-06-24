package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;

/**
 * Stores information for accessing a resource, such as a sound, texture, entity, block, etc.
 * For MC 1.10.2 and onwards the MinecraftKey is used for this, for MC 1.8.8 String is used.
 */
public class ResourceKey {
    private final Object keyHandle;

    private ResourceKey(Object keyHandle) {
        this.keyHandle = keyHandle;
    }

    /**
     * Gets the resource key path
     * 
     * @return path
     */
    public String getPath() {
        return keyHandle.toString();
    }

    /**
     * Gets the backing Minecraft Key for this resource. Unsupported on MC 1.8.8.
     * 
     * @return Minecraft key
     */
    public Object toMinecraftKey() {
        if (!MinecraftKeyHandle.T.isAvailable()) {
            throw new UnsupportedOperationException("Minecraft Keys are not used on this version of Minecraft");
        }
        return this.keyHandle;
    }

    /**
     * Constructs a new Resource Key taking information from a backing minecraft key token.
     * Not valid on MC 1.8.8.
     * 
     * @param minecraftKey
     * @return resource key
     */
    public static ResourceKey fromMinecraftKey(Object minecraftKey) {
        if (!MinecraftKeyHandle.T.isAssignableFrom(minecraftKey)) {
            throw new IllegalArgumentException("Input is not a MinecraftKey");
        }
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
        if (MinecraftKeyHandle.T.isAvailable()) {
            return new ResourceKey(MinecraftKeyHandle.T.constr_keyToken.raw.newInstance(key));
        } else {
            return new ResourceKey(key);
        }
    }
}
