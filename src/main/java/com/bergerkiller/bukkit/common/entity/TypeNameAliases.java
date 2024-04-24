package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks enum name aliases for entity types for cross-version compatibility
 */
class TypeNameAliases {
    private static final Map<String, List<String>> aliases = new HashMap<>();
    static {
        register("CHEST_MINECART", "MINECART_CHEST");
        register("FURNACE_MINECART", "MINECART_FURNACE");
        register("TNT_MINECART", "MINECART_TNT");
        register("HOPPER_MINECART", "MINECART_HOPPER");
        register("COMMAND_BLOCK_MINECART", "MINECART_COMMAND");
        register("SPAWNER_MINECART", "MINECART_MOB_SPAWNER");
    }

    public static List<String> getNames(EntityType entityType) {
        return aliases.getOrDefault(entityType.name(), Collections.singletonList(entityType.name()));
    }

    private static void register(String... names) {
        List<String> namesList = Collections.unmodifiableList(Arrays.asList(names));
        for (String name : namesList) {
            aliases.put(name, namesList);
        }
    }
}
