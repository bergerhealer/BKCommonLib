package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;

/**
 * Used as net.minecraft.server.ResourceKey on 1.15.2 and earlier, before this class was introduced.
 */
public final class ResourceKey_1_15_2<T> {
    @SuppressWarnings("rawtypes")
    private static final Map<String, ResourceKey_1_15_2> cache = Collections.synchronizedMap(new IdentityHashMap<String, ResourceKey_1_15_2>());
    private static final Object root_category = MinecraftKeyHandle.createNew("root").getRaw();
    public final Object category;
    public final Object name;

    // Note: Dimension type! This is not the world dimension.
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_TYPE_OVERWORLD = createNamed("dimension_type", "overworld");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_TYPE_THE_NETHER = createNamed("dimension_type", "the_nether");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_TYPE_THE_END = createNamed("dimension_type", "the_end");

    // Note: World Dimension! This is not the type, and more than these 3 are in use
    // These constants are only for the 3 main worlds
    public static final ResourceKey_1_15_2<Object> CATEGORY_WORLD_DIMENSION = createCategory(MinecraftKeyHandle.createNew("dimension").getRaw());
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_OVERWORLD = createNamed("dimension", "overworld");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_THE_NETHER = createNamed("dimension", "the_nether");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_THE_END = createNamed("dimension", "the_end");

    public static <T> ResourceKey_1_15_2<T> createCategory(Object categoryName) {
        return fromCache(root_category, categoryName);
    }

    public static <T> ResourceKey_1_15_2<T> create(ResourceKey_1_15_2<T> category, Object key) {
        return fromCache(category.name, key);
    }

    // Pre-1.16 use only!
    public static <T> ResourceKey_1_15_2<T> createNamed(String categoryName, String name) {
        return fromCache(MinecraftKeyHandle.createNew(categoryName).getRaw(),
                         MinecraftKeyHandle.createNew(name).getRaw());
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey_1_15_2<T> fromCache(Object category, Object name) {
        String key = (category + ":" + name).intern();
        return cache.computeIfAbsent(key, unused -> new ResourceKey_1_15_2<T>(category, name));
    }

    private ResourceKey_1_15_2(Object category, Object key) {
        this.category = category;
        this.name = key;
    }

    @Override
    public String toString() {
        return "ResourceKey[" + this.category + " / " + this.name + ']';
    }
}
