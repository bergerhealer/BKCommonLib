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

    public static <T> ResourceKey_1_15_2<T> createCategory(String categoryName) {
        return fromCache(root_category, MinecraftKeyHandle.createNew(categoryName).getRaw());
    }

    public static <T> ResourceKey_1_15_2<T> create(ResourceKey_1_15_2<T> category, Object key) {
        return fromCache(category.name, key);
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
