package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.mountiplex.logic.TextValueSequence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores some important information cached for performance reasons
 */
class PlayerGameInfoCache {
    private static final Map<String, TextValueSequence> valueCache = new ConcurrentHashMap<>(); // Performance

    public static TextValueSequence parseVersion(String version) {
        return valueCache.computeIfAbsent(version, TextValueSequence::parse);
    }
}
