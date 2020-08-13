package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

/**
 * Deserializes Bukkit ItemStack objects from a key-value map, with the added feature
 * of supporting configuration produced on mc versions newer than the one it is running on.<br>
 * <br>
 * This class also replaces ItemStack properties that were saved as raw maps with the serialized classes,
 * and double -> integer conversion, to add support for deserialization from JSON.
 */
public class ItemStackDeserializer implements Function<Map<String, Object>, ItemStack> {
    private static final ConverterFunction NO_CONVERSION = map -> { return true; };
    public static final ItemStackDeserializer INSTANCE = new ItemStackDeserializer();
    private final List<ItemStackConverter> converters;
    private final int curr_version;
    private final int max_version;

    private ItemStackDeserializer() {
        this.converters = new ArrayList<>();
        this.curr_version = CraftMagicNumbersHandle.getDataVersion();

        // All data versions where the ItemStack YAML had a change that requires conversion
        // Put in order of old to recent

        // FROM MC 1.13 to 1.12.2 (perform the material enum remapping logic in reverse)
        this.register(0, map -> {
            Object type = map.get("type");
            if (type instanceof String && ((String) type).startsWith("LEGACY_")) {
                map.put("type", ((String) type).substring(7));
                return true;
            }

            if ("BEETROOTS".equals(type)) {
                map.put("type", "BEETROOT_BLOCK");
            } else if ("SPAWNER".equals(type)) {
                map.put("type", "MOB_SPAWNER");
            }

            //TODO: There are a lot more changes here!
            return true;
        });

        // From MC 1.13.1 to 1.13 (dead coral types no longer valid)
        this.register(1519, map -> {
            Object type = map.get("type");
            if (LogicUtil.contains(type,
                    "DEAD_BRAIN_CORAL", "DEAD_BUBBLE_CORAL",
                    "DEAD_FIRE_CORAL", "DEAD_HORN_CORAL",
                    "DEAD_TUBE_CORAL"
            )) {
                return false;
            }
            return true;
        });

        // From MC 1.13.2 to 1.13.1 (no changes)
        this.register(1628, NO_CONVERSION);

        // From MC 1.14 to 1.13.2 (materials added in 1.14 no longer valid)
        // Colored dyes were added, replacing some of the original items
        // Different wood type signs now exist, renaming the SIGN/WALL_SIGN material types
        this.register(1631, map -> {
            Object type = map.get("type");
            if ("GREEN_DYE".equals(type)) {
                map.put("type", "CACTUS_GREEN");
            } else if ("YELLOW_DYE".equals(type)) {
                map.put("type", "DANDELION_YELLOW");
            } else if ("RED_DYE".equals(type)) {
                map.put("type", "ROSE_RED");
            } else if ("OAK_SIGN".equals(type)) {
                map.put("type", "SIGN");
            } else if ("OAK_WALL_SIGN".equals(type)) {
                map.put("type", "WALL_SIGN");
            }
            return !Helper.ADDED_MC_1_14.contains(type);
        });

        // From MC 1.14.1 to 1.14 (no changes)
        this.register(1952, NO_CONVERSION);

        // From MC 1.14.2 to 1.14.1 (no changes)
        this.register(1957, NO_CONVERSION);

        // From MC 1.14.3 to 1.14.2 (no changes)
        this.register(1963, NO_CONVERSION);

        // From MC 1.14.4 to 1.14.3 (no changes)
        this.register(1968, NO_CONVERSION);

        // From MC 1.15 to 1.14.4 (bees and honey materials no longer valid)
        this.register(1976, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_15.contains(type);
        });

        // From MC 1.15.1 to 1.15 (no changes)
        this.register(2225, NO_CONVERSION);

        // From MC 1.15.2+ to 1.15.1 (no changes)
        this.register(2227, NO_CONVERSION);

        // From MC 1.16 to 1.15.2 (loads of 1.16 introduced materials no longer valid)
        this.register(2230, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_16.contains(type);
        });

        // From MC 1.16.2 to MC 1.16
        this.register(2567, NO_CONVERSION);

        // Maximum supported data version
        this.max_version = 2578; // MC 1.16.2
    }

    // Registers a converter if it can convert from a future data version only
    // Current or older data versions are already natively supported by the server
    private void register(int data_version, ConverterFunction converter) {
        if (data_version <= this.curr_version && !this.converters.isEmpty()) {
            this.converters.remove(0);
        }
        this.converters.add(0, new ItemStackConverter(data_version, converter));
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        deserializeMaps(args);

        Object version_raw = args.get("v");
        if (version_raw instanceof Number) {
            int version = ((Number) version_raw).intValue();
            if (version > this.curr_version && version <= this.max_version) {
                // Requires conversion, go down the list of item stack converters and process those applicable
                for (ItemStackConverter converter : this.converters) {
                    if (version > converter.output_version) {
                        if (converter.converter.convert(args)) {
                            // Successful conversion
                            version = converter.output_version;
                        } else {
                            // Item is not supported past this point
                            break;
                        }
                    }
                }

                // Update version
                if (version == 0) {
                    args.remove("v");
                } else {
                    args.put("v", Integer.valueOf(version));
                }
            }
        }

        return ItemStack.deserialize(args);
    }

    /**
     * Gets the maximum supported Minecraft data version
     * 
     * @return max version
     */
    public int getMaxSupportedDataVersion() {
        return this.max_version;
    }

    /**
     * Deserializes ItemStack properties that are stored as just maps into the correct
     * metadata type, and converts Double to Integer where needed.
     * 
     * @param map
     */
    private void deserializeMaps(Map<String, Object> values) {
        convertNumberToIntegerInMap(values, "amount");
        convertNumberToIntegerInMapValues(values, "enchantments");
        replaceMapInMap(values, "meta", meta -> {
            convertNumberToIntegerInMap(meta, "custom-model-data");
            convertNumberToIntegerInMap(meta, "repair-cost");
            convertNumberToIntegerInMap(meta, "Damage");
            convertNumberToIntegerInMap(meta, "generation");
            convertNumberToIntegerInMap(meta, "power");
            convertNumberToIntegerInMap(meta, "map-id");
            convertNumberToIntegerInMap(meta, "fish-variant");
            convertNumberToIntegerInMapValues(meta, "enchants");
            replaceMapInMap(meta, "color", ItemStackDeserializer::deserializeColor);
            replaceMapInMap(meta, "display-map-color", ItemStackDeserializer::deserializeColor);
            replaceMapInMap(meta, "custom-color", ItemStackDeserializer::deserializeColor);
            replaceMapInMap(meta, "firework-effect", ItemStackDeserializer::deserializeFireworkEffect);
            replaceListOfMapsInMap(meta, "firework-effects", ItemStackDeserializer::deserializeFireworkEffect);
            replaceListOfMapsInMap(meta, "patterns", org.bukkit.block.banner.Pattern::new);
            replaceListOfMapsInMap(meta, "charged-projectiles", ItemStackDeserializer.this);
            replaceListOfMapsInMap(meta, "custom-effects", potionEffect -> {
                convertNumberToIntegerInMap(potionEffect, "amplifier");
                convertNumberToIntegerInMap(potionEffect, "duration");
                return new org.bukkit.potion.PotionEffect(potionEffect);
            });

            return CraftItemStackHandle.deserializeItemMeta(meta);
        });
    }

    private static ConfigurationSerializable deserializeFireworkEffect(java.util.Map<String, Object> values) {
        replaceListOfMapsInMap(values, "colors", ItemStackDeserializer::deserializeColor);
        replaceListOfMapsInMap(values, "fade-colors", ItemStackDeserializer::deserializeColor);
        return org.bukkit.FireworkEffect.deserialize(values);
    }

    private static org.bukkit.Color deserializeColor(java.util.Map<String, Object> values) {
        convertNumberToIntegerInMapValues(values);
        return org.bukkit.Color.deserialize(values);
    }

    @SuppressWarnings("unchecked")
    private static void replaceListOfMapsInMap(java.util.Map<String, Object> map, String key, Function<java.util.Map<String, Object>, ?> mapper) {
        Object value = map.get(key);
        if (value instanceof java.util.List) {
            java.util.List<Object> list = (java.util.List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                Object list_item = list.get(i);
                if (list_item instanceof java.util.Map) {
                    list.set(i, mapper.apply((java.util.Map<String, Object>) list_item));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void replaceMapInMap(java.util.Map<String, Object> map, String key, Function<java.util.Map<String, Object>, ?> mapper) {
        Object value = map.get(key);
        if (value instanceof java.util.Map) {
            map.put(key, mapper.apply((Map<String, Object>) value));
        }
    }

    private static void convertNumberToIntegerInMapValues(java.util.Map<String, Object> map, String key) {
        Object mapAtKey = map.get(key);
        if (mapAtKey instanceof java.util.Map) {
            convertNumberToIntegerInMapValues((java.util.Map<?, ?>) mapAtKey);
        }
    }

    @SuppressWarnings("unchecked")
    private static void convertNumberToIntegerInMap(Map<?, ?> map, Object key) {
        Object old = map.get(key);
        if (old instanceof Number && !(old instanceof Integer)) {
            ((Map<Object, Object>) map).put(key, Integer.valueOf(((Number) old).intValue()));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void convertNumberToIntegerInMapValues(Map<?, ?> map) {
        for (Map.Entry entry : map.entrySet()) {
            Object old = entry.getValue();
            if (old instanceof Number && !(old instanceof Integer)) {
                entry.setValue(Integer.valueOf(((Number) old).intValue()));
            }
        }
    }

    /**
     * Converts the raw YAML from a data version to an older data version
     */
    private static final class ItemStackConverter {
        public final int output_version;
        public final ConverterFunction converter;

        public ItemStackConverter(int output_version, ConverterFunction converter) {
            if (converter == null) {
                throw new IllegalArgumentException("Converter can not be null");
            }
            this.output_version = output_version;
            this.converter = converter;
        }
    }

    private static interface ConverterFunction {
        boolean convert(Map<String, Object> values);
    }

    private static class Helper {
        // All material names (Material enum) added Minecraft 1.13.2 -> 1.14
        public static final Set<String> ADDED_MC_1_14 = new HashSet<String>(Arrays.asList(
                "ACACIA_SIGN", "ACACIA_WALL_SIGN",
                "ANDESITE_SLAB", "ANDESITE_STAIRS", "ANDESITE_WALL",
                "BAMBOO", "BAMBOO_SAPLING", "BARREL", "BELL",
                "BIRCH_SIGN", "BIRCH_WALL_SIGN", "BLACK_DYE", "BLAST_FURNACE",
                "BLUE_DYE", "BRICK_WALL", "BROWN_DYE", "CAMPFIRE",
                "CARTOGRAPHY_TABLE", "CAT_SPAWN_EGG", "COMPOSTER",
                "CORNFLOWER", "CREEPER_BANNER_PATTERN", "CROSSBOW",
                "CUT_RED_SANDSTONE_SLAB", "CUT_SANDSTONE_SLAB",
                "DARK_OAK_SIGN", "DARK_OAK_WALL_SIGN", "DIORITE_SLAB",
                "DIORITE_STAIRS", "DIORITE_WALL", "END_STONE_BRICK_SLAB",
                "END_STONE_BRICK_STAIRS", "END_STONE_BRICK_WALL",
                "FLETCHING_TABLE", "FLOWER_BANNER_PATTERN", "FOX_SPAWN_EGG",
                "GLOBE_BANNER_PATTERN", "GRANITE_SLAB", "GRANITE_STAIRS",
                "GRANITE_WALL", "GRINDSTONE", "JIGSAW", "JUNGLE_SIGN",
                "JUNGLE_WALL_SIGN", "LANTERN", "LEATHER_HORSE_ARMOR",
                "LECTERN", "LILY_OF_THE_VALLEY", "LOOM",
                "MOJANG_BANNER_PATTERN", "MOSSY_COBBLESTONE_SLAB",
                "MOSSY_COBBLESTONE_STAIRS", "MOSSY_STONE_BRICK_SLAB",
                "MOSSY_STONE_BRICK_STAIRS", "MOSSY_STONE_BRICK_WALL",
                "NETHER_BRICK_WALL", "PANDA_SPAWN_EGG", "PILLAGER_SPAWN_EGG",
                "POLISHED_ANDESITE_SLAB", "POLISHED_ANDESITE_STAIRS",
                "POLISHED_DIORITE_SLAB", "POLISHED_DIORITE_STAIRS",
                "POLISHED_GRANITE_SLAB", "POLISHED_GRANITE_STAIRS",
                "POTTED_BAMBOO", "POTTED_CORNFLOWER",
                "POTTED_LILY_OF_THE_VALLEY", "POTTED_WITHER_ROSE",
                "PRISMARINE_WALL", "RAVAGER_SPAWN_EGG",
                "RED_NETHER_BRICK_SLAB", "RED_NETHER_BRICK_STAIRS",
                "RED_NETHER_BRICK_WALL", "RED_SANDSTONE_WALL",
                "SANDSTONE_WALL", "SCAFFOLDING", "SKULL_BANNER_PATTERN",
                "SMITHING_TABLE", "SMOKER", "SMOOTH_QUARTZ_SLAB",
                "SMOOTH_QUARTZ_STAIRS", "SMOOTH_RED_SANDSTONE_SLAB",
                "SMOOTH_RED_SANDSTONE_STAIRS", "SMOOTH_SANDSTONE_SLAB",
                "SMOOTH_SANDSTONE_STAIRS", "SMOOTH_STONE_SLAB",
                "SPRUCE_SIGN", "SPRUCE_WALL_SIGN", "STONECUTTER",
                "STONE_BRICK_WALL", "STONE_STAIRS", "SUSPICIOUS_STEW",
                "SWEET_BERRIES", "SWEET_BERRY_BUSH", "TRADER_LLAMA_SPAWN_EGG",
                "WANDERING_TRADER_SPAWN_EGG", "WHITE_DYE", "WITHER_ROSE"));

        // All material names (Material enum) added Minecraft 1.14.4 -> 1.15
        public static final Set<String> ADDED_MC_1_15 = new HashSet<String>(Arrays.asList(
                "BEEHIVE", "BEE_NEST", "BEE_SPAWN_EGG",
                "HONEYCOMB", "HONEYCOMB_BLOCK",
                "HONEY_BLOCK", "HONEY_BOTTLE"));

        // All material names (Material enum) added Minecraft 1.15.2 -> 1.16
        public static final Set<String> ADDED_MC_1_16 = new HashSet<String>(Arrays.asList(
                "ANCIENT_DEBRIS", "BASALT", "CHAIN", "CRYING_OBSIDIAN",
                "BLACKSTONE", "BLACKSTONE_SLAB", "BLACKSTONE_STAIRS", "BLACKSTONE_WALL",
                "GILDED_BLACKSTONE",
                "CHISELED_NETHER_BRICKS", "CHISELED_POLISHED_BLACKSTONE",
                "CRACKED_NETHER_BRICKS", "CRACKED_POLISHED_BLACKSTONE_BRICKS",
                "CRIMSON_BUTTON", "CRIMSON_DOOR", "CRIMSON_FENCE",
                "CRIMSON_FENCE_GATE", "CRIMSON_FUNGUS", "CRIMSON_HYPHAE",
                "CRIMSON_NYLIUM", "CRIMSON_PLANKS", "CRIMSON_PRESSURE_PLATE",
                "CRIMSON_ROOTS", "CRIMSON_SIGN", "CRIMSON_SLAB", "CRIMSON_STAIRS",
                "CRIMSON_STEM", "CRIMSON_TRAPDOOR", "CRIMSON_WALL_SIGN",
                "HOGLIN_SPAWN_EGG", "LODESTONE", "MUSIC_DISC_PIGSTEP",
                "NETHERITE_AXE", "NETHERITE_BLOCK", "THERITE_BOOTS",
                "NETHERITE_CHESTPLATE", "NETHERITE_HELMET",
                "NETHERITE_HOE", "NETHERITE_INGOT", "NETHERITE_LEGGINGS",
                "NETHERITE_PICKAXE", "NETHERITE_SCRAP", "NETHERITE_SHOVEL",
                "NETHERITE_SWORD",
                "NETHER_GOLD_ORE", "NETHER_SPROUTS",
                "PIGLIN_BANNER_PATTERN", "PIGLIN_SPAWN_EGG",
                "POLISHED_BASALT", "POLISHED_BLACKSTONE",
                "POLISHED_BLACKSTONE_BRICKS", "POLISHED_BLACKSTONE_BRICK_SLAB",
                "POLISHED_BLACKSTONE_BRICK_STAIRS", "POLISHED_BLACKSTONE_BRICK_WALL",
                "POLISHED_BLACKSTONE_BUTTON", "POLISHED_BLACKSTONE_PRESSURE_PLATE",
                "POLISHED_BLACKSTONE_SLAB", "POLISHED_BLACKSTONE_STAIRS",
                "POLISHED_BLACKSTONE_WALL",
                "POTTED_CRIMSON_FUNGUS", "POTTED_CRIMSON_ROOTS",
                "POTTED_WARPED_FUNGUS", "POTTED_WARPED_ROOTS", "QUARTZ_BRICKS",
                "RESPAWN_ANCHOR", "SHROOMLIGHT",
                "SOUL_CAMPFIRE", "SOUL_FIRE", "SOUL_LANTERN",
                "SOUL_SOIL", "SOUL_TORCH", "SOUL_WALL_TORCH",
                "STRIDER_SPAWN_EGG",
                "STRIPPED_CRIMSON_HYPHAE", "STRIPPED_CRIMSON_STEM",
                "STRIPPED_WARPED_HYPHAE", "STRIPPED_WARPED_STEM",
                "TARGET", "TWISTING_VINES", "TWISTING_VINES_PLANT",
                "WARPED_BUTTON", "WARPED_DOOR", "WARPED_FENCE",
                "WARPED_FENCE_GATE", "WARPED_FUNGUS",
                "WARPED_FUNGUS_ON_A_STICK", "WARPED_HYPHAE",
                "WARPED_NYLIUM", "WARPED_PLANKS",
                "WARPED_PRESSURE_PLATE", "WARPED_ROOTS",
                "WARPED_SIGN", "WARPED_SLAB", "WARPED_STAIRS",
                "WARPED_STEM", "WARPED_TRAPDOOR", "WARPED_WALL_SIGN",
                "WARPED_WART_BLOCK",
                "WEEPING_VINES", "WEEPING_VINES_PLANT",
                "ZOGLIN_SPAWN_EGG", "ZOMBIFIED_PIGLIN_SPAWN_EGG"));
    }
}
