package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.config.SNBTDeserializer;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Parses Paper's NBT encoding of ItemStacks, like:
 * <pre>
 * ==: org.bukkit.inventory.ItemStack
 * DataVersion: 4325
 * id: minecraft:oak_planks
 * count: 1
 * schema_version: 1
 * </pre>
 * Into ItemStacks. If parsing isn't supported, can migrate it back into a Spigot
 * (or legacy) supported format, like so:
 * <pre>
 * ==: org.bukkit.inventory.ItemStack
 * v: 4325
 * type: OAK_PLANKS
 * </pre>
 * This is only used for data versions beyond 4325 (1.21.5), as this mechanism
 * did not exist on prior versions.
 */
public class ItemStackDeserializerMigratorNBT extends ItemStackDeserializerMigrator implements Function<Map<String, Object>, ItemStack> {
    private final NBTToBukkit nbtToBukkit = new NBTToBukkit();
    private final boolean isPaperServer;

    ItemStackDeserializerMigratorNBT() {
        isPaperServer = CommonBootstrap.isPaperServer();

        // 1.21.6 -> 1.21.5
        this.register(4325, ConverterFunction.NO_CONVERSION);

        // 1.21.7 -> 1.21.6
        this.register(4435, ConverterFunction.NO_CONVERSION);

        // 1.21.8 -> 1.21.7
        this.register(4438, ConverterFunction.NO_CONVERSION);

        // 1.21.9 -> 1.21.8
        this.register(4440, ConverterFunction.NO_CONVERSION);

        // 1.21.9 -> 1.21.10
        this.register(4554, ConverterFunction.NO_CONVERSION);

        // Maximum supported data version
        this.setMaximumDataVersion(4556); // MC 1.21.10
    }

    /**
     * Migrates the Paper NBT format to the Bukkit ItemMeta format. Only supports data saved
     * on 1.21.5 or later, with newer NBT data being back-migrated to the 1.21.5 format.
     *
     * @param args Input data
     * @return Migrated data in Bukkit's ItemMeta format
     */
    public Map<String, Object> toBukkitEncoding(Map<String, Object> args) {
        this.migrate(args, "DataVersion");
        return nbtToBukkit.apply(args);
    }

    @Override
    protected void baseMigrate(Map<String, Object> args) {
        // NBT format has no garbage ItemMeta or anything. We can just feed it directly to deserialize()
        // We can deserialize from GSON, so do ensure number data types are correct
        convertNumberToIntegerInMap(args, "count");
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        this.migrate(args, "DataVersion");

        try {
            // On Paper we can just call deserialize(), which handles NBT properly
            if (isPaperServer) {
                return ItemStack.deserialize(args);
            }

            // On Spigot, we have to turn the args into NBT, and then deserialize that.
            // See also Paper's implementation of this (CraftMagicNumbers deserializeStack(args)
            CommonTagCompound tag = new CommonTagCompound();
            readInteger(args.get("DataVersion")).ifPresent(dataVersion -> tag.putValue("DataVersion", dataVersion));
            readString(args.get("id")).ifPresent(id -> tag.putValue("id", id));
            readInteger(args.get("count")).ifPresent(count -> tag.putValue("count", count));
            readMap(args.get("components")).ifPresent(componentsMap -> {
                CommonTagCompound components = new CommonTagCompound();
                for (Map.Entry<String, Object> component : componentsMap.entrySet()) {
                    String snbt = readString(component.getValue()).orElseThrow(() -> new IllegalStateException(
                            "Missing component value for " + component.getKey()));
                    CommonTag componentTag = CommonTag.fromSNBT(snbt).getResultOrThrow(RuntimeException::new);
                    components.put(component.getKey(), componentTag);
                }
                tag.put("components", components);
            });

            return CraftItemStackHandle.T.deserializeNBT.invoke(tag);
        } catch (RuntimeException ex) {
            logFailDeserialize(args);
            throw ex;
        }
    }

    /**
     * Converts (Paper) NBT encoded data to Bukkit/Spigot's ItemMeta encoded data.
     * This only operates on data saved for Minecraft 1.21.5.
     * Older data is not supported (paper did not encode to it on older versions), and newer
     * data is migrated down to 1.21.5 before this is used.
     */
    public static class NBTToBukkit implements Function<Map<String, Object>, Map<String, Object>> {
        private final ItemStackDeserializerIdToMaterialMapper idMapper;
        private final NBTChatComponentsToJson chatComponentsToJson;
        private final Map<String, ComponentMapper> componentMappers = new HashMap<>();

        public NBTToBukkit() {
            this.idMapper = new ItemStackDeserializerIdToMaterialMapper();
            this.idMapper.loadMappings();
            this.chatComponentsToJson = new NBTChatComponentsToJson();
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
            this.componentMappers.put("minecraft:item_model", (result, nbtData) -> {
                if (nbtData instanceof String) {
                    prepareMetadata(result).put("item-model", nbtData);
                }
            });
            this.componentMappers.put("minecraft:custom_name", (result, nbtData) -> {
                if (nbtData != null) {
                    prepareMetadata(result).put("display-name", chatComponentsToJson.toJson(nbtData));
                }
            });
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> args) {
            int dataVersion = readInteger(args.get("DataVersion")).orElse(4325 /* 1.21.5 */);
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

    /**
     * Attempts to convert a ChatComponent encoded as an NBT document (but as java Map/List/etc.), into
     * a GSON String as used on older versions of Minecraft. If the String is invalid, it'll turn
     * into a plaintext representation when deserializing later. No errors are generally thrown even
     * if the format is garbage.
     */
    private static class NBTChatComponentsToJson {
        private final Gson gson;

        public NBTChatComponentsToJson() {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Byte.class, (JsonSerializer<Byte>) (src, type, jsonSerializationContext)
                    -> new JsonPrimitive(src != 0));
            this.gson = builder.create();
        }

        public String toJson(Object nbtData) {
            // A plaintext String must be encoded as a full valid json blob instead
            // For this, we got to add some garbage
            if (nbtData instanceof String) {
                nbtData = Collections.singletonMap("text", nbtData);
            }

            return gson.toJson(nbtData);
        }
    }
}
