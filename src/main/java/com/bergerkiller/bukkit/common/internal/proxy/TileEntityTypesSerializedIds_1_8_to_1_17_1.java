package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Stores legacy type id's used in PacketPlayOutTileEntityData.
 * No longer used as of Minecraft 1.18.
 */
public class TileEntityTypesSerializedIds_1_8_to_1_17_1 {
    private static final BiMap<Integer, Object> idsToMinecraftKey = HashBiMap.create(16);
    private static final BiMap<Object, Integer> minecraftKeyToId = idsToMinecraftKey.inverse();
    private static final BiMap<MinecraftKeyHandle, String> minecraftKeyToLegacyName = HashBiMap.create(32);
    private static final BiMap<String, MinecraftKeyHandle> legacyNameToMinecraftKey = minecraftKeyToLegacyName.inverse();

    static {
        register(1, "mob_spawner", "MobSpawner");
        register(2, "command_block", "Control");
        register(3, "beacon", "Beacon");
        register(4, "skull", "Skull");
        register(5, "flower_pot", "FlowerPot");
        register(6, "banner", "Banner");
        register(7, "structure_block", "Structure");
        register(8, "end_gateway", "EndGateway");
        register(9, "sign", "Sign");
        register(10, "shulker_box", "ShulkerBox");
        register(11, "bed", "Bed");

        // Legacy name translation, just in case it's needed
        register(-1, "furnace", "Furnace");
        register(-1, "chest", "Chest");
        register(-1, "ender_chest", "EnderChest");
        register(-1, "jukebox", "RecordPlayer");
        register(-1, "dispenser", "Trap");
        register(-1, "dropper", "Dropper");
        register(-1, "noteblock", "Music");
        register(-1, "piston", "Piston");
        register(-1, "brewing_stand", "Cauldron");
        register(-1, "enchanting_table", "EnchantTable");
        register(-1, "end_portal", "Airportal");
        register(-1, "daylight_detector", "DLDetector");
        register(-1, "hopper", "Hopper");
        register(-1, "comparator", "Comparator");
    }

    private static void register(int id, String name, String pre_1_10_2_Name) {
        MinecraftKeyHandle keyHandle = MinecraftKeyHandle.createNew(name);
        Object minecraftKey = keyHandle.getRaw();
        if (id != -1) {
            idsToMinecraftKey.put(id, minecraftKey);
        }
        minecraftKeyToLegacyName.put(keyHandle, pre_1_10_2_Name);
    }

    public static Set<Map.Entry<Integer, Object>> allEntries() {
        return idsToMinecraftKey.entrySet();
    }

    public static Object toMinecraftKey(int id) {
        return idsToMinecraftKey.get(Integer.valueOf(id));
    }

    public static int getId(Object nmsMinecraftKeyHandle) {
        return minecraftKeyToId.getOrDefault(nmsMinecraftKeyHandle, -1);
    }

    // Only for MC 1.10.2 and before
    public static String getLegacyName(MinecraftKeyHandle minecraftKeyHandle) {
        String name = minecraftKeyToLegacyName.get(minecraftKeyHandle);
        if (name == null && minecraftKeyHandle != null) {
            // Little workaround that just generates some 'probably' names
            // Not really going to be used...
            String name_key = minecraftKeyHandle.getName();
            // Split by '_' character, make first character of each part uppercase
            // Result: ender_chest -> EnderChest
            StringBuilder str = new StringBuilder();
            for (String part : name_key.split("_")) {
                if (!part.isEmpty()) {
                    str.append(Character.toUpperCase(part.charAt(0)));
                    str.append(part.substring(1).toLowerCase(Locale.ENGLISH));
                }
            }
            name = str.toString();
        }
        return name;
    }

    // Only for MC 1.10.2 and before
    public static MinecraftKeyHandle toMinecraftKeyFromLegacyName(String legacyName) {
        MinecraftKeyHandle minecraftKey = legacyNameToMinecraftKey.get(legacyName);
        if (minecraftKey == null && legacyName != null) {
            // Find all uppercase characters, put a _ in front except for first char
            // EnderChest -> ender_chest
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < legacyName.length(); i++) {
                char c = legacyName.charAt(i);
                if (i > 0 && Character.isUpperCase(c)) {
                    str.append('_');
                }
                str.append(Character.toLowerCase(c));
            }
            // Convert to a key
            minecraftKey = MinecraftKeyHandle.createNew(str.toString());
        }
        return minecraftKey;
    }
}
