package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Handles conversion of String enum names to {@link CommonEntityType} and
 * Bukkit {@link EntityType}. Supports future and backwards compatible naming conventions.
 */
class TypeNameLookup {
    private static final NamePair[] values;
    private static final Object byNameLock = new Object();
    private static Map<String, NamePair> byNameCache;
    static {
        List<NamePair> namePairValues = new ArrayList<>();
        Map<String, NamePair> namePairByNameCache = new HashMap<>();
        for (EntityType entityType : EntityType.values()) {
            CommonEntityType commonEntityType = CommonEntityType.byEntityType(entityType);
            if (commonEntityType == CommonEntityType.UNKNOWN) {
                namePairValues.add(new NamePair(entityType.name(), entityType, commonEntityType));
            } else {
                for (String name : commonEntityType.entityTypeNames) {
                    namePairValues.add(new NamePair(name, entityType, commonEntityType));
                }
            }
        }
        for (NamePair pair : namePairValues) {
            namePairByNameCache.put(pair.name, pair);
        }
        for (NamePair pair : namePairValues) {
            namePairByNameCache.putIfAbsent(pair.name.toLowerCase(Locale.ENGLISH), pair);
            namePairByNameCache.putIfAbsent(pair.name.toUpperCase(Locale.ENGLISH), pair);
        }
        values = namePairValues.toArray(new NamePair[0]);
        byNameCache = namePairByNameCache;
    }

    public static NamePair lookupByName(String name) {
        return LogicUtil.synchronizeCopyOnWrite(byNameLock, () -> byNameCache, name, Map::get, (currByNameCache, theName) -> {
            NamePair computed = MountiplexUtil.parseArray(values, theName, null);

            // If an alias is found, cache it
            if (computed != null) {
                Map<String, NamePair> updatedCache = new HashMap<>(currByNameCache);
                updatedCache.put(theName, computed);
                byNameCache = updatedCache;
            }

            return computed;
        });
    }

    public static class NamePair {
        public final String name;
        public final EntityType entityType;
        public final CommonEntityType commonEntityType;

        public NamePair(String name, EntityType entityType, CommonEntityType commonEntityType) {
            this.name = name;
            this.entityType = entityType;
            this.commonEntityType = commonEntityType;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
