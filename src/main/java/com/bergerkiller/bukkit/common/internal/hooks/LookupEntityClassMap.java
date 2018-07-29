package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.bergerkiller.generated.net.minecraft.server.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryMaterialsHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;

/**
 * Map proxy for a mapping from Entity Class to entity name.
 * This map is injected into the server so that class lookups to
 * BKCommonLib generated entity classes still succeed.
 */
public class LookupEntityClassMap<K, V> implements Map<K, V> {
    private final Map<K, V> _base;

    @SuppressWarnings("unchecked")
    public LookupEntityClassMap(Map<?, ?> base) {
        this._base = (Map<K, V>) base;
    }

    @Override
    public final int size() {
        return this._base.size();
    }

    @Override
    public final boolean isEmpty() {
        return this._base.isEmpty();
    }

    @Override
    public final boolean containsKey(Object key) {
        return this._base.containsKey(translate(key));
    }

    @Override
    public final boolean containsValue(Object value) {
        return this._base.containsValue(value);
    }

    @Override
    public final V get(Object key) {
        return this._base.get(translate(key));
    }

    @Override
    public final V put(K key, V value) {
        return this._base.put(key, value);
    }

    @Override
    public final V remove(Object key) {
        return this._base.remove(key);
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> m) {
        this._base.putAll(m);
    }

    @Override
    public final void clear() {
        this._base.clear();
    }

    @Override
    public final Set<K> keySet() {
        return this._base.keySet();
    }

    @Override
    public final Collection<V> values() {
        return this._base.values();
    }

    @Override
    public final Set<java.util.Map.Entry<K, V>> entrySet() {
        return this._base.entrySet();
    }

    private static Object translate(Object key) {
        if (key instanceof Class<?>) {
            return ClassInterceptor.findBaseType((Class<?>) key);
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    public static void hook() {
        // <= 1.10.2 had a static Map instance
        if (EntityTypesHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            Map<Class<?>, String> base = (Map<Class<?>, String>) EntityTypesHandle.T.opt_typeNameMap_1_10_2.raw.get();
            Map<Class<?>, String> repl = new LookupEntityClassMap<Class<?>, String>(base);
            EntityTypesHandle.T.opt_typeNameMap_1_10_2.raw.set(repl);
            return;
        }

        // >= 1.11 uses RegistryMaterials BiMap
        RegistryMaterialsHandle reg = EntityTypesHandle.T.opt_registry.get();
        Map<?, ?> base = reg.getInverseLookupField();
        Map<Object, Object> repl = new LookupEntityClassMap<Object, Object>(base);
        reg.setInverseLookupField(repl);
    }

    @SuppressWarnings("unchecked")
    public static void unhook() {
        // <= 1.10.2 had a static Map instance
        if (EntityTypesHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            Object orig = EntityTypesHandle.T.opt_typeNameMap_1_10_2.raw.get();
            if (orig instanceof LookupEntityClassMap) {
                LookupEntityClassMap<Class<?>, String> repl = (LookupEntityClassMap<Class<?>, String>) orig;
                EntityTypesHandle.T.opt_typeNameMap_1_10_2.raw.set(repl._base);
            }
            return;
        }

        // >= 1.11 uses RegistryMaterials BiMap
        RegistryMaterialsHandle reg = EntityTypesHandle.T.opt_registry.get();
        Map<?, ?> orig = reg.getInverseLookupField();
        if (orig instanceof LookupEntityClassMap) {
            LookupEntityClassMap<Object, Object> repl = (LookupEntityClassMap<Object, Object>) orig;
            reg.setInverseLookupField(repl._base);
        }
    }

}
