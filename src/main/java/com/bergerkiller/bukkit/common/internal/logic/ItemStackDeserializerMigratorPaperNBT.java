package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.config.SNBTDeserializer;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Converts Paper's NBT encoding of ItemStacks, like:
 * <pre>
 * ==: org.bukkit.inventory.ItemStack
 * DataVersion: 4325
 * id: minecraft:oak_planks
 * count: 1
 * schema_version: 1
 * </pre>
 * Back into a Spigot (or legacy) supported format, like so:
 * <pre>
 * ==: org.bukkit.inventory.ItemStack
 * v: 4325
 * type: OAK_PLANKS
 * </pre>
 * This is only used for data versions beyond 4325 (1.21.5), as this mechanism
 * did not exist on prior versions.
 */
public class ItemStackDeserializerMigratorPaperNBT extends ItemStackDeserializerMigrator implements Function<Map<String, Object>, ItemStack> {
    private final ItemStackDeserializerIdToMaterialMapper idMapper;
    private final Map<String, ComponentMapper> componentMappers = new HashMap<>();

    ItemStackDeserializerMigratorPaperNBT() {
        this.idMapper = new ItemStackDeserializerIdToMaterialMapper();
        this.idMapper.loadMappings();
        this.componentMappers.put("minecraft:custom_model_data", (result, nbtData) -> {
            Map<String, Object> cmd = new LinkedHashMap<>();
            cmd.put("==", "CustomModelData");
            cmd.put("floats", parseList(nbtData, "floats", o -> o instanceof Number ? ((Number) o).doubleValue() : 0.0f));
            cmd.put("flags", parseList(nbtData, "flags", o -> o instanceof Number && ((Number) o).intValue() != 0));
            cmd.put("strings", parseList(nbtData, "strings", o -> (o != null) ? o.toString() : ""));
            cmd.put("colors", parseList(nbtData, "colors", o -> {
                Color color = o instanceof Number ? Color.fromARGB(((Number) o).intValue()) : Color.BLACK;
                Map<String, Object> s = new LinkedHashMap<>(5);
                s.put("==", "Color");
                s.put("ALPHA", color.getAlpha());
                s.put("RED", color.getRed());
                s.put("BLUE", color.getBlue());
                s.put("GREEN", color.getGreen());
                return s;
            }));
            prepareMetadata(result).put("custom-model-data", cmd);
        });
        this.componentMappers.put("minecraft:unbreakable", (result, nbtData) -> {
            prepareMetadata(result).put("Unbreakable", true);
        });
        this.componentMappers.put("minecraft:damage", (result, nbtData) -> {
            if (nbtData instanceof Number) {
                prepareMetadata(result).put("Damage", ((Number) nbtData).intValue());
            }
        });

        // Maximum supported data version
        this.setMaximumDataVersion(4325); // MC 1.21.5
    }

    public Map<String, Object> toBukkitEncoding(Map<String, Object> args) {
        int dataVersion = readInteger(args.get("DataVersion")).orElse(getCurrentDataVersion());
        String id = readString(args.get("id")).orElse(null);

        // Validate
        if (id == null) {
            ItemStackDeserializerMigratorBukkit.logFailDeserialize(args);
            throw new IllegalArgumentException("ItemStack has no id field set");
        }
        if (id.indexOf(':') == -1) {
            id = "minecraft:" + id;
        }

        // Attempt to convert the "id" field into a valid Material name using the id mapper
        Map<String, String> idMappings = this.idMapper.getOrOlder(Integer.toString(dataVersion))
                .orElse(null);
        if (idMappings == null) {
            ItemStackDeserializerMigratorBukkit.logFailDeserialize(args);
            throw new IllegalArgumentException("Unsupported data version: " + dataVersion);
        }

        // Decode id into material type.
        String type = idMappings.get(id);
        if (type == null) {
            type = "UNKNOWN_MATERIAL_TYPE"; // Triggers fallback deserialization
        }

        // Decode count
        int amount = readInteger(args.get("count")).orElse(1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("==", "org.bukkit.inventory.ItemStack");
        result.put("v", dataVersion);
        result.put("type", type);
        if (amount != 1) {
            result.put("amount", amount);
        }

        // Decode a select few components into ItemMeta that Spigot/old paper supports
        Optional<Map<String, Object>> components = readMap(args.get("components"));
        if (components.isPresent()) {
            for (Map.Entry<String, Object> component : components.get().entrySet()) {
                String key = component.getKey();
                if (key.indexOf(':') == -1) {
                    key = "minecraft:" + key;
                }
                ComponentMapper mapper = componentMappers.get(key);
                if (mapper != null) {
                    Object nbtData = component.getValue();
                    if (nbtData != null) {
                        nbtData = SNBTDeserializer.parse(nbtData.toString());
                    }
                    mapper.apply(result, nbtData);
                }
            }
        }
        return result;
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        // NBT format has no garbage ItemMeta or anything. We can just feed it directly to deserialize()
        // We can deserialize from GSON, so do ensure number data types are correct
        convertNumberToIntegerInMap(args, "count");

        this.migrate(args, "DataVersion");

        try {
            return ItemStack.deserialize(args);
        } catch (RuntimeException ex) {
            logFailDeserialize(args);
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> prepareMetadata(Map<String, Object> result) {
        return (Map<String, Object>) result.computeIfAbsent("meta", m -> {
            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("==", "ItemMeta");
            meta.put("meta-type", "UNSPECIFIC");
            return meta;
        });
    }

    @FunctionalInterface
    private interface ComponentMapper {
        void apply(Map<String, Object> result, Object nbtData);
    }
}
