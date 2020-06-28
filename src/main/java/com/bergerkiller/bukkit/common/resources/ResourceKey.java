package com.bergerkiller.bukkit.common.resources;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.ResourceKeyHandle;

/**
 * Stores information for accessing a resource, such as a sound, texture, entity, block, etc.
 * 
 * @param <T> type information representing the category
 */
public final class ResourceKey<T> extends BasicWrapper<ResourceKeyHandle> {
    @SuppressWarnings("rawtypes")
    private static final Map<Object, ResourceKey> cache = Collections.synchronizedMap(new IdentityHashMap<Object, ResourceKey>());

    private ResourceKey(Object keyHandle) {
        this.setHandle(ResourceKeyHandle.createHandle(keyHandle));
    }

    /**
     * Gets the category information of this resource key
     * 
     * @return category
     */
    public ResourceCategory<T> getCategory() {
        return ResourceCategory.create(handle.getCategory());
    }

    /**
     * Gets the backing Minecraft Key name for this resource.
     * 
     * @return Minecraft key name
     */
    public MinecraftKeyHandle getName() {
        return handle.getName();
    }

    /**
     * Gets the resource key path, which is the name stringified.
     * 
     * @return path
     */
    public String getPath() {
        return handle.getName().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ResourceKey) {
            ResourceKey<?> other = (ResourceKey<?>) o;
            return this.getCategory().equals(other.getCategory()) &&
                   this.getName().equals(other.getName());
        } else {
            return false;
        }
    }

    /**
     * Constructs a resource key for representing the net.minecraft.server.ResourceKey object.
     * 
     * @param nmsResourceKeyHandle
     * @return ResourceKey wrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> ResourceKey<T> fromResourceKeyHandle(Object nmsResourceKeyHandle) {
        if (nmsResourceKeyHandle == null) {
            return null;
        } else {
            return cache.computeIfAbsent(nmsResourceKeyHandle, ResourceKey::new);
        }
    }

    /**
     * Constructs a new Resource Key taking information from a backing minecraft key token.
     * Returns null if the input minecraft key is null.
     * 
     * @param category Resource category
     * @param minecraftKey
     * @return resource key, null if minecraftkey is null
     */
    public static <T> ResourceKey<T> fromMinecraftKey(ResourceCategory<T> category, MinecraftKeyHandle minecraftKey) {
        if (minecraftKey != null) {
            Object resourceKeyHandle = ResourceKeyHandle.T.create.raw.invoke(category, minecraftKey);
            return fromResourceKeyHandle(resourceKeyHandle);
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
     * @param category Resource category
     * @param key
     * @return resource key, null if the key contains invalid characters
     */
    public static <T> ResourceKey<T> fromPath(ResourceCategory<T> category, String key) {
        return fromMinecraftKey(category, MinecraftKeyHandle.createNew(key));
    }

    /**
     * Constructs a new Resource Key taking information from a key String token,
     * made out of the namespace and name parts.<br>
     * <br>
     * The key may only contain the characters: [a-z0-9/._-]<br>
     * No uppercase characters are allowed in the path.
     * If the input key is invalid, null is returned.
     * 
     * @param category Resource category
     * @param namespace
     * @param name
     * @return resource key, null if the key contains invalid characters
     */
    public static <T> ResourceKey<T> fromPath(ResourceCategory<T> category, String namespace, String name) {
        return fromMinecraftKey(category, MinecraftKeyHandle.createNew(namespace, name));
    }
}
