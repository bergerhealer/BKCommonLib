package com.bergerkiller.bukkit.common.resources;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.resources.ResourceKeyHandle;

/**
 * Type of resource represented by a resource key
 * 
 * @param <T> type information representing the category
 */
public final class ResourceCategory<T> {
    @SuppressWarnings("rawtypes")
    private static final Map<Object, ResourceCategory> cache = Collections.synchronizedMap(new IdentityHashMap<Object, ResourceCategory>());
    public static final ResourceCategory<SoundEffect> sound_effect = create("sound_event");
    public static final ResourceCategory<DimensionType> dimension_type = create("dimension_type");
    public static final ResourceCategory<org.bukkit.World> dimension = create("dimension");

    private final ResourceKeyHandle categoryKey;

    private ResourceCategory(Object keyHandle) {
        this.categoryKey = ResourceKeyHandle.createHandle(keyHandle);
    }

    private static <T> ResourceCategory<T> create(String name) {
        return create(MinecraftKeyHandle.createNew(name));
    }

    @SuppressWarnings("unchecked")
    protected static <T> ResourceCategory<T> create(MinecraftKeyHandle name) {
        Object resourceKeyHandle = ResourceKeyHandle.T.createCategory.raw.invoke(name.getRaw());
        return cache.computeIfAbsent(resourceKeyHandle, ResourceCategory::new);
    }

    /**
     * Constructs a new Resource Key taking information from a backing minecraft key token.
     * Returns null if the input minecraft key is null.
     * 
     * @param name Name to turn into a resource key
     * @return resource key, null if minecraftkey is null
     */
    public ResourceKey<T> createKey(MinecraftKeyHandle name) {
        return ResourceKey.fromMinecraftKey(this, name);
    }

    /**
     * Constructs a new Resource Key taking information from a key String token.<br>
     * <br>
     * The key may only contain the characters: [a-z0-9/._-]<br>
     * No uppercase characters are allowed in the path.
     * If the input key is invalid, null is returned.
     *
     * @param name Name to turn into a resource key
     * @return resource key, null if the key contains invalid characters
     */
    public ResourceKey<T> createKey(String name) {
        return ResourceKey.fromPath(this, name);
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
    public ResourceKey<T> createKey(String namespace, String name) {
        return ResourceKey.fromPath(this, namespace, name);
    }

    /**
     * Gets the name of this category
     * 
     * @return category name
     */
    public MinecraftKeyHandle getName() {
        return this.categoryKey.getName();
    }

    /**
     * Gets the key used to refer to the resource category
     * 
     * @return category key
     */
    public ResourceKeyHandle getCategoryKey() {
        return this.categoryKey;
    }

    @Override
    public String toString() {
        return this.categoryKey.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ResourceCategory) {
            return ((ResourceCategory<?>) o).getName().equals(this.getName());
        } else {
            return false;
        }
    }
}
