package com.bergerkiller.bukkit.common.internal.logic;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.google.common.collect.MapMaker;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Deserializes Bukkit ItemStack objects from a key-value map, with the added feature
 * of supporting configuration produced on mc versions newer than the one it is running on.<br>
 * <br>
 * This class also replaces ItemStack properties that were saved as raw maps with the serialized classes,
 * and double -> integer conversion, to add support for deserialization from JSON.
 */
public class ItemStackDeserializerMigratorBukkit extends ItemStackDeserializerMigrator implements Function<Map<String, Object>, ItemStack> {
    private static final Material FALLBACK_MATERIAL = MaterialUtil.getFirst("OAK_WOOD", "LEGACY_WOOD");
    private final ItemMetaDeserializer metaDeserializer = new ItemMetaDeserializer();

    ItemStackDeserializerMigratorBukkit() {
        // All data versions where the ItemStack YAML had a change that requires conversion
        // Put in order of old to recent

        // FROM MC 1.13 to 1.12.2 (perform the material enum remapping logic in reverse)
        this.register(0, map -> {
            // Damage value needs to be moved from Meta to be part of the ItemStack
            // However, CraftItemMeta doesn't store this original Damage value anywhere. We
            // cache the original Map that de-serialized into this meta in a WeakHashMap for
            // that reason.
            Object meta = map.get("meta");
            if (meta != null) {
                Map<String, Object> metaMap = metaDeserializer.legacyCachedMeta.get(meta);
                if (metaMap != null) {
                    map.putAll(metaMap);
                }
            }

            Object type = map.get("type");
            if (type instanceof String && ((String) type).startsWith("LEGACY_")) {
                map.put("type", ((String) type).substring(7));
                return true;
            }

            String repl = Helper.LEGACY_MAPPING_1_13.get(type);
            if (repl != null) {
                map.put("type", repl);
                return true;
            }

            // Not a legacy material, can't use
            return false;
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
        this.register(1628, ConverterFunction.NO_CONVERSION);

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
        this.register(1952, ConverterFunction.NO_CONVERSION);

        // From MC 1.14.2 to 1.14.1 (no changes)
        this.register(1957, ConverterFunction.NO_CONVERSION);

        // From MC 1.14.3 to 1.14.2 (no changes)
        this.register(1963, ConverterFunction.NO_CONVERSION);

        // From MC 1.14.4 to 1.14.3 (no changes)
        this.register(1968, ConverterFunction.NO_CONVERSION);

        // From MC 1.15 to 1.14.4 (bees and honey materials no longer valid)
        this.register(1976, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_15.contains(type);
        });

        // From MC 1.15.1 to 1.15 (no changes)
        this.register(2225, ConverterFunction.NO_CONVERSION);

        // From MC 1.15.2+ to 1.15.1 (no changes)
        this.register(2227, ConverterFunction.NO_CONVERSION);

        // From MC 1.16 to 1.15.2 (loads of 1.16 introduced materials no longer valid)
        this.register(2230, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_16.contains(type);
        });

        // From MC 1.16.2 to MC 1.16.1
        this.register(2567, ConverterFunction.NO_CONVERSION);

        // From MC 1.16.3 to MC 1.16.2
        this.register(2578, ConverterFunction.NO_CONVERSION);

        // From MC 1.16.4 to MC 1.16.3
        this.register(2580, ConverterFunction.NO_CONVERSION);

        // From MC 1.16.5 to MC 1.16.4
        this.register(2584, ConverterFunction.NO_CONVERSION);

        // From MC 1.17 to MC 1.16.5
        this.register(2586, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_17.contains(type);
        });

        // From MC 1.17.1 to MC 1.17
        this.register(2724, ConverterFunction.NO_CONVERSION);

        // From MC 1.17.1 to MC 1.18
        this.register(2730, ConverterFunction.NO_CONVERSION);

        // From MC 1.18 to MC 1.18.1
        this.register(2860, ConverterFunction.NO_CONVERSION);

        // From MC 1.18.1 to MC 1.18.2
        this.register(2865, ConverterFunction.NO_CONVERSION);

        // From MC 1.18.2 to MC 1.19
        this.register(2975, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_19.contains(type);
        });

        // From MC 1.19.1 to 1.19
        this.register(3105, ConverterFunction.NO_CONVERSION);

        // From MC 1.19.2 to 1.19.1
        this.register(3117, ConverterFunction.NO_CONVERSION);

        // From MC 1.19.3 to 1.19.2
        this.register(3120, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_19_3.contains(type);
        });

        // From MC 1.19.4 to 1.19.3
        this.register(3218, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_19_4.contains(type);
        });

        // From MC 1.20 to 1.19.4
        this.register(3337, map -> {
            Object type = map.get("type");

            // Remap 4 old archeology items
            if ("ARCHER_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_ARCHER");
                return true;
            } else if ("PRIZE_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_PRIZE");
                return true;
            } else if ("ARMS_UP_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_ARMS_UP");
                return true;
            } else if ("SKULL_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_SKULL");
                return true;
            } else {
                return !Helper.ADDED_MC_1_20.contains(type);
            }
        });

        // From MC 1.20.1 to 1.20
        this.register(3463, ConverterFunction.NO_CONVERSION);

        // From MC 1.20.2 to 1.20.1
        this.register(3465, ConverterFunction.NO_CONVERSION);

        // From MC 1.20.3 to 1.20.2
        this.register(3578, map -> {
            Object type = map.get("type");

            if ("SHORT_GRASS".equals(type)) {
                map.put("type", "GRASS");
                return true;
            } else {
                return !Helper.ADDED_MC_1_20_3.contains(type);
            }
        });

        // From MC 1.20.4 to 1.20.3
        this.register(3698, ConverterFunction.NO_CONVERSION);

        // From MC 1.20.5 to 1.20.4
        // TODO: Did the data components break any of the item meta that needs correcting?
        this.register(3700, map -> {
            Object type = map.get("type");

            if ("TURTLE_SCUTE".equals(type)) {
                map.put("type", "SCUTE");
                return true;
            } else {
                return !Helper.ADDED_MC_1_20_5.contains(type);
            }
        });

        // From MC 1.20.6 to 1.20.5
        this.register(3837, ConverterFunction.NO_CONVERSION);

        // From MC 1.21 to 1.20.6
        this.register(3839, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21.contains(type);
        });

        // From MC 1.21.1 to 1.21
        this.register(3953, ConverterFunction.NO_CONVERSION);

        // From MC 1.21.2 to 1.21.1
        this.register(3955, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_2.contains(type);
        });

        // From MC 1.21.3 to 1.21.2
        this.register(4080, ConverterFunction.NO_CONVERSION);

        // From MC 1.21.4 to 1.21.3
        this.register(4082, map -> {
            Object type = map.get("type");
            if (Helper.ADDED_MC_1_21_4.contains(type)) {
                return false;
            }

            //TODO: Migrate custom model data?
            return true;
        });

        // From MC 1.21.4 to 1.21.5
        this.register(4189, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_5.contains(type);
        });

        // Maximum supported data version
        this.setMaximumDataVersion(4325); // MC 1.21.5
    }

    public ItemMetaDeserializer getItemMetaDeserializer() {
        return metaDeserializer;
    }

    @Override
    protected void baseMigrate(Map<String, Object> args) {
        // Migrate double -> integer
        // Also takes care of older gson where metadata was stored without a == qualifier
        convertNumberToIntegerInMap(args, "amount");
        convertNumberToIntegerInMap(args, "damage");
        convertNumberToIntegerInMapValues(args, "enchantments");
        replaceMapInMap(args, "meta", metaDeserializer);
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        this.migrate(args, "v");

        try {
            return ItemStack.deserialize(args);
        } catch (NullPointerException | IllegalArgumentException ex) {
            // This is sometimes thrown when the Material type cannot be found
            Object typeNameObj = args.get("type");
            if (typeNameObj instanceof String) {
                String typeName = (String) typeNameObj;
                Material type;
                if (CommonCapabilities.MATERIAL_ENUM_CHANGES && args.containsKey("v")) {
                    // Uses post-1.13 format
                    type = MaterialUtil.getMaterial(typeName);
                } else {
                    // Uses pre-1.13 format. Prefix LEGACY_ to the name
                    type = CommonLegacyMaterials.getLegacyMaterial(typeName);
                }
                if (type != null) {
                    // Valid material. Rethrow original exception. It's valid.
                    logFailDeserialize(args);
                    throw ex;
                }
            }

            // Type doesn't exist on this version of Minecraft. As work-around, return
            // a generic wood block
            return new ItemStack(FALLBACK_MATERIAL);
        } catch (RuntimeException ex) {
            logFailDeserialize(args);
            throw ex;
        }
    }

    private static ConfigurationSerializable deserializeFireworkEffect(java.util.Map<String, Object> values) {
        replaceListOfMapsInMap(values, "colors", ItemStackDeserializerMigratorBukkit::deserializeColor);
        replaceListOfMapsInMap(values, "fade-colors", ItemStackDeserializerMigratorBukkit::deserializeColor);
        return org.bukkit.FireworkEffect.deserialize(values);
    }

    private static org.bukkit.Color deserializeColor(java.util.Map<String, Object> values) {
        convertNumberToIntegerInMapValues(values);
        return org.bukkit.Color.deserialize(values);
    }

    // >= 1.21.4
    public static Object deserializeCustomModelData(java.util.Map<String, Object> values) {
        convertNumberListToFloatInMap(values, "floats");
        replaceListOfMapsInMap(values, "colors", ItemStackDeserializerMigratorBukkit::deserializeColor);
        return CraftItemStackHandle.T.deserializeCustomModelData.invoke(values);
    }

    public class ItemMetaDeserializer implements Function<Map<String, Object>, ItemMeta> {
        private final Map<Object, Map<String, Object>> legacyCachedMeta = new MapMaker().weakKeys().concurrencyLevel(4).makeMap();

        /**
         * ItemMeta de-serializer that migrates double to integer where required for GSON decoding.<br>
         * <br>
         * On versions before 1.13 it also stores some original Map contents that created an
         * ItemMeta so that it can be restored when deserializing the ItemStack later. This migrates
         * the "Damage" value to the ItemStack's "damage" field.
         *
         * @param mapping Mapping
         */
        @Override
        public ItemMeta apply(Map<String, Object> mapping) {
            // Migrate double -> integer, this is required for loading items from GSON

            // Custom model data can be of the format of a dict when it stores the different types of fields.
            // Or it can be a single number, which translates to just a float[] {number}
            // Fix this format. This same logic is also used for the Paper NBT migrator
            // On old versions of the server that does not support custom model data, decode only the floats as a number
            {
                Object value = mapping.get("custom-model-data");
                if (value instanceof Number) {
                    mapping.put("custom-model-data", ((Number) value).intValue());
                } else if (value instanceof Map) {
                    Map<String, Object> cmdValues = (Map<String, Object>) value;
                    if (CraftItemStackHandle.T.deserializeCustomModelData.isAvailable()) {
                        mapping.put("custom-model-data", deserializeCustomModelData(cmdValues));
                    } else {
                        Object rawFloats = cmdValues.get("floats");
                        if (rawFloats instanceof List) {
                            List<?> floats = (List<?>) rawFloats;
                            if (!floats.isEmpty() && floats.get(0) instanceof Number) {
                                mapping.put("custom-model-data", ((Number) floats.get(0)).intValue());
                            } else {
                                mapping.remove("custom-model-data");
                            }
                        } else {
                            mapping.remove("custom-model-data");
                        }
                    }
                }
            }

            convertNumberToIntegerInMap(mapping, "repair-cost");
            convertNumberToIntegerInMap(mapping, "Damage");
            convertNumberToIntegerInMap(mapping, "max-damage");
            convertNumberToIntegerInMap(mapping, "max-stack-size");
            convertNumberToIntegerInMap(mapping, "generation");
            convertNumberToIntegerInMap(mapping, "power");
            convertNumberToIntegerInMap(mapping, "map-id");
            convertNumberToIntegerInMap(mapping, "fish-variant");
            convertNumberToIntegerInMapValues(mapping, "enchants");
            replaceMapInMap(mapping, "color", ItemStackDeserializerMigratorBukkit::deserializeColor);
            replaceMapInMap(mapping, "display-map-color", ItemStackDeserializerMigratorBukkit::deserializeColor);
            replaceMapInMap(mapping, "custom-color", ItemStackDeserializerMigratorBukkit::deserializeColor);
            replaceMapInMap(mapping, "firework-effect", ItemStackDeserializerMigratorBukkit::deserializeFireworkEffect);
            replaceListOfMapsInMap(mapping, "firework-effects", ItemStackDeserializerMigratorBukkit::deserializeFireworkEffect);
            replaceListOfMapsInMap(mapping, "patterns", org.bukkit.block.banner.Pattern::new);
            replaceListOfMapsInMap(mapping, "charged-projectiles", ItemStackDeserializerMigratorBukkit.this);
            replaceListOfMapsInMap(mapping, "custom-effects", potionEffect -> {
                convertNumberToIntegerInMap(potionEffect, "amplifier");
                convertNumberToIntegerInMap(potionEffect, "duration");
                return new org.bukkit.potion.PotionEffect(potionEffect);
            });

            ItemMeta meta = CraftItemStackHandle.deserializeItemMeta(mapping);

            if (CommonCapabilities.NEEDS_LEGACY_ITEMMETA_MIGRATION) {
                Object damage = mapping.get("Damage");
                if (damage != null) {
                    Map<String, Object> itemStackMeta = new HashMap<>();
                    itemStackMeta.put("damage", damage);
                    legacyCachedMeta.put(meta, itemStackMeta);
                }
            }

            return meta;
        }
    }

    private static class Helper {
        // Remappings from 1.13 materials to the 1.12.2 and before LEGACY material names
        public static final Map<String, String> LEGACY_MAPPING_1_13 = new HashMap<>();
        static {
            try {
                String mat_cat_path = "/com/bergerkiller/bukkit/common/internal/resources/mat_to_legacy.txt";
                try (InputStream input = ItemStackDeserializerMigratorBukkit.class.getResourceAsStream(mat_cat_path)) {
                    try (Scanner scanner = new Scanner(input, "UTF-8")) {
                        while (scanner.hasNext()) {
                            String line = scanner.nextLine();
                            int splitIdx = line.indexOf('=');
                            if (splitIdx != -1) {
                                LEGACY_MAPPING_1_13.put(line.substring(0, splitIdx), line.substring(splitIdx + 1));
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to initialize legacy material conversion table", t);
            }
        }

        @SafeVarargs
        private static <T> Set<T> makeSet(List<T>... lists) {
            Set<T> result = new HashSet<T>();
            for (List<T> list : lists) {
                result.addAll(list);
            }
            return result;
        }

        private static List<String> makeWoodMaterials(String woodName) {
            return Arrays.asList(
                    woodName + "_PLANKS", woodName + "_SAPLING",
                    woodName + "_LOG", woodName + "_WOOD",
                    woodName + "_LEAVES", woodName + "_SLAB",
                    woodName + "_FENCE", woodName + "_STAIRS",
                    woodName + "_BUTTON", woodName + "_PRESSURE_PLATE",
                    woodName + "_DOOR", woodName + "_TRAPDOOR",
                    woodName + "_FENCE_GATE", woodName + "_BOAT",
                    woodName + "_CHEST_BOAT", woodName + "_SIGN",
                    woodName + "_HANGING_SIGN", woodName + "_WALL_SIGN",
                    woodName + "_WALL_HANGING_SIGN",
                    "POTTED_" + woodName + "_SAPLING",
                    "STRIPPED_" + woodName + "_LOG",
                    "STRIPPED_" + woodName + "_WOOD");
        }

        private static List<String> makeColoredMaterials(String materialName) {
            return Arrays.asList(
                    "WHITE_" + materialName, "ORANGE_" + materialName,
                    "MAGENTA_" + materialName, "LIGHT_BLUE_" + materialName,
                    "YELLOW_" + materialName, "LIME_" + materialName,
                    "PINK_" + materialName, "GRAY_" + materialName,
                    "LIGHT_GRAY_" + materialName, "CYAN_" + materialName,
                    "PURPLE_" + materialName, "BLUE_" + materialName,
                    "BROWN_" + materialName, "GREEN_" + materialName,
                    "RED_" + materialName, "BLACK_" + materialName);
        }

        // All material names (Material enum) added Minecraft 1.13.2 -> 1.14
        public static final Set<String> ADDED_MC_1_14 = makeSet(Arrays.asList(
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
        public static final Set<String> ADDED_MC_1_15 = makeSet(Arrays.asList(
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

        // All material names (Material enum) added Minecraft 1.16.5 -> 1.17
        public static final Set<String> ADDED_MC_1_17 = makeSet(
                makeColoredMaterials("CANDLE"),
                makeColoredMaterials("CANDLE_CAKE"),
                Arrays.asList(
                "DEEPSLATE", "COBBLED_DEEPSLATE", "POLISHED_DEEPSLATE",
                "CALCITE",  "TUFF", "DRIPSTONE_BLOCK", "ROOTED_DIRT",
                "DEEPSLATE_COAL_ORE", "DEEPSLATE_IRON_ORE", "COPPER_ORE",
                "DEEPSLATE_COPPER_ORE", "GOLD_ORE", "DEEPSLATE_GOLD_ORE",
                "REDSTONE_ORE", "DEEPSLATE_REDSTONE_ORE", "EMERALD_ORE",
                "DEEPSLATE_EMERALD_ORE", "LAPIS_ORE", "DEEPSLATE_LAPIS_ORE",
                "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE", "NETHER_GOLD_ORE",
                "NETHER_QUARTZ_ORE", "ANCIENT_DEBRIS", "COAL_BLOCK",
                "RAW_IRON_BLOCK", "RAW_COPPER_BLOCK", "RAW_GOLD_BLOCK",
                "AMETHYST_BLOCK", "BUDDING_AMETHYST",
                "IRON_BLOCK", "COPPER_BLOCK", "GOLD_BLOCK", "DIAMOND_BLOCK",
                "NETHERITE_BLOCK", "EXPOSED_COPPER", "WEATHERED_COPPER",
                "OXIDIZED_COPPER", "CUT_COPPER", "EXPOSED_CUT_COPPER",
                "WEATHERED_CUT_COPPER", "OXIDIZED_CUT_COPPER", "CUT_COPPER_STAIRS",
                "EXPOSED_CUT_COPPER_STAIRS", "WEATHERED_CUT_COPPER_STAIRS",
                "OXIDIZED_CUT_COPPER_STAIRS", "CUT_COPPER_SLAB",
                "EXPOSED_CUT_COPPER_SLAB", "WEATHERED_CUT_COPPER_SLAB",
                "OXIDIZED_CUT_COPPER_SLAB", "WAXED_COPPER_BLOCK",
                "WAXED_EXPOSED_COPPER", "WAXED_WEATHERED_COPPER",
                "WAXED_OXIDIZED_COPPER", "WAXED_CUT_COPPER",
                "WAXED_EXPOSED_CUT_COPPER", "WAXED_WEATHERED_CUT_COPPER",
                "WAXED_OXIDIZED_CUT_COPPER", "WAXED_CUT_COPPER_STAIRS",
                "WAXED_EXPOSED_CUT_COPPER_STAIRS", "WAXED_WEATHERED_CUT_COPPER_STAIRS",
                "WAXED_OXIDIZED_CUT_COPPER_STAIRS", "WAXED_CUT_COPPER_SLAB",
                "WAXED_EXPOSED_CUT_COPPER_SLAB", "WAXED_WEATHERED_CUT_COPPER_SLAB",
                "WAXED_OXIDIZED_CUT_COPPER_SLAB", "AZALEA_LEAVES",
                "FLOWERING_AZALEA_LEAVES", "TINTED_GLASS", "AZALEA",
                "FLOWERING_AZALEA", "SPORE_BLOSSOM", "MOSS_CARPET",  "MOSS_BLOCK",
                "HANGING_ROOTS", "BIG_DRIPLEAF", "SMALL_DRIPLEAF", "SMOOTH_BASALT",
                "INFESTED_DEEPSLATE", "DEEPSLATE_BRICKS", "CRACKED_DEEPSLATE_BRICKS",
                "DEEPSLATE_TILES", "CRACKED_DEEPSLATE_TILES", "CHISELED_DEEPSLATE",
                "GLOW_LICHEN", "COBBLED_DEEPSLATE_WALL", "POLISHED_DEEPSLATE_WALL",
                "DEEPSLATE_BRICK_WALL", "DEEPSLATE_TILE_WALL", "LIGHT", "DIRT_PATH",
                "COBBLED_DEEPSLATE_STAIRS", "POLISHED_DEEPSLATE_STAIRS",
                "DEEPSLATE_BRICK_STAIRS", "DEEPSLATE_TILE_STAIRS",
                "COBBLED_DEEPSLATE_SLAB", "POLISHED_DEEPSLATE_SLAB",
                "DEEPSLATE_BRICK_SLAB", "DEEPSLATE_TILE_SLAB",
                "LIGHTNING_ROD", "SCULK_SENSOR", "POLISHED_BLACKSTONE_BUTTON",
                "POLISHED_BLACKSTONE_PRESSURE_PLATE", "RAW_IRON", "RAW_GOLD",
                "RAW_COPPER", "COPPER_INGOT", "AMETHYST_SHARD", "POWDER_SNOW_BUCKET",
                "AXOLOTL_BUCKET", "BUNDLE", "SPYGLASS", "GLOW_INK_SAC",
                "AXOLOTL_SPAWN_EGG", "GLOW_SQUID_SPAWN_EGG", "GOAT_SPAWN_EGG",
                "GLOW_ITEM_FRAME", "GLOW_BERRIES", "CANDLE",
                "SMALL_AMETHYST_BUD", "MEDIUM_AMETHYST_BUD",
                "LARGE_AMETHYST_BUD", "AMETHYST_CLUSTER", "POINTED_DRIPSTONE",
                "WATER_CAULDRON", "LAVA_CAULDRON", "POWDER_SNOW_CAULDRON",
                "CANDLE_CAKE",
                "POWDER_SNOW", "CAVE_VINES", "CAVE_VINES_PLANT",
                "BIG_DRIPLEAF_STEM",  "POTTED_AZALEA_BUSH", "POTTED_FLOWERING_AZALEA_BUSH"));

        // All material names (Material enum) added Minecraft 1.18.2 -> 1.19
        public static final Set<String> ADDED_MC_1_19 = makeSet(Arrays.asList(
                "MUD", "MANGROVE_PLANKS", "MANGROVE_PROPAGULE",
                "MANGROVE_LOG", "MANGROVE_ROOTS", "MUDDY_MANGROVE_ROOTS",
                "STRIPPED_MANGROVE_LOG", "STRIPPED_MANGROVE_WOOD",
                "MANGROVE_WOOD", "MANGROVE_LEAVES", "MANGROVE_SLAB",
                "MUD_BRICK_SLAB", "MANGROVE_FENCE", "PACKED_MUD",
                "MUD_BRICKS", "REINFORCED_DEEPSLATE", "MUD_BRICK_STAIRS",
                "SCULK", "SCULK_VEIN", "SCULK_CATALYST", "SCULK_SHRIEKER",
                "MANGROVE_STAIRS", "MUD_BRICK_WALL", "MANGROVE_BUTTON",
                "MANGROVE_PRESSURE_PLATE", "MANGROVE_DOOR",
                "MANGROVE_TRAPDOOR", "MANGROVE_FENCE_GATE",
                "OAK_CHEST_BOAT", "SPRUCE_CHEST_BOAT",
                "BIRCH_CHEST_BOAT", "JUNGLE_CHEST_BOAT",
                "ACACIA_CHEST_BOAT", "DARK_OAK_CHEST_BOAT",
                "MANGROVE_BOAT", "MANGROVE_CHEST_BOAT", "MANGROVE_SIGN",
                "TADPOLE_BUCKET", "RECOVERY_COMPASS", "ALLAY_SPAWN_EGG",
                "FROG_SPAWN_EGG", "TADPOLE_SPAWN_EGG", "WARDEN_SPAWN_EGG",
                "MUSIC_DISC_5", "DISC_FRAGMENT_5", "GOAT_HORN",
                "OCHRE_FROGLIGHT", "VERDANT_FROGLIGHT", "PEARLESCENT_FROGLIGHT",
                "FROGSPAWN", "ECHO_SHARD", "MANGROVE_WALL_SIGN",
                "POTTED_MANGROVE_PROPAGULE"));

        // All material names (Material enum) added Minecraft 1.19.2 -> 1.19.3
        public static final Set<String> ADDED_MC_1_19_3 = makeSet(Arrays.asList(
                "BAMBOO_PLANKS", "BAMBOO_MOSAIC", "BAMBOO_BLOCK",
                "STRIPPED_BAMBOO_BLOCK", "BAMBOO_SLAB", "BAMBOO_MOSAIC_SLAB",
                "CHISELED_BOOKSHELF", "BAMBOO_FENCE", "SCULK_VEIN",
                "BAMBOO_STAIRS", "BAMBOO_MOSAIC_STAIRS", "BAMBOO_BUTTON",
                "BAMBOO_PRESSURE_PLATE", "BAMBOO_DOOR", "BAMBOO_TRAPDOOR",
                "BAMBOO_FENCE_GATE", "BAMBOO_RAFT", "BAMBOO_CHEST_RAFT",
                "BAMBOO_SIGN", "BAMBOO_WALL_SIGN",
                "OAK_HANGING_SIGN", "SPRUCE_HANGING_SIGN",
                "BIRCH_HANGING_SIGN", "JUNGLE_HANGING_SIGN",
                "ACACIA_HANGING_SIGN", "DARK_OAK_HANGING_SIGN",
                "MANGROVE_HANGING_SIGN", "BAMBOO_HANGING_SIGN",
                "CRIMSON_HANGING_SIGN", "WARPED_HANGING_SIGN",
                "OAK_WALL_HANGING_SIGN", "SPRUCE_WALL_HANGING_SIGN",
                "BIRCH_WALL_HANGING_SIGN", "ACACIA_WALL_HANGING_SIGN",
                "JUNGLE_WALL_HANGING_SIGN", "DARK_OAK_WALL_HANGING_SIGN",
                "MANGROVE_WALL_HANGING_SIGN", "CRIMSON_WALL_HANGING_SIGN",
                "WARPED_WALL_HANGING_SIGN", "BAMBOO_WALL_HANGING_SIGN",
                "CAMEL_SPAWN_EGG", "ENDER_DRAGON_SPAWN_EGG",
                "IRON_GOLEM_SPAWN_EGG", "SNOW_GOLEM_SPAWN_EGG",
                "WITHER_SPAWN_EGG", "PIGLIN_HEAD", "PIGLIN_WALL_HEAD"));

        // All material names (Material enum) added Minecraft 1.19.3 -> 1.19.4
        public static final Set<String> ADDED_MC_1_19_4 = makeSet(
                makeWoodMaterials("CHERRY"),
                Arrays.asList(
                        "SUSPICIOUS_SAND",
                        "TORCHFLOWER", "PINK_PETALS",
                        "DECORATED_POT",
                        "SNIFFER_SPAWN_EGG", "TORCHFLOWER_SEEDS", "BRUSH",
                        "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE",
                        "DUNE_ARMOR_TRIM_SMITHING_TEMPLATE", "COAST_ARMOR_TRIM_SMITHING_TEMPLATE",
                        "WILD_ARMOR_TRIM_SMITHING_TEMPLATE", "WARD_ARMOR_TRIM_SMITHING_TEMPLATE",
                        "EYE_ARMOR_TRIM_SMITHING_TEMPLATE", "VEX_ARMOR_TRIM_SMITHING_TEMPLATE",
                        "TIDE_ARMOR_TRIM_SMITHING_TEMPLATE", "SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE",
                        "RIB_ARMOR_TRIM_SMITHING_TEMPLATE", "SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE",
                        "POTTERY_SHARD_ARCHER", "POTTERY_SHARD_PRIZE", "POTTERY_SHARD_ARMS_UP",
                        "POTTERY_SHARD_SKULL",
                        "POTTED_TORCHFLOWER", "TORCHFLOWER_CROP"
        ));

        // All material names (Material enum) added Minecraft 1.19.4 -> 1.20
        public static final Set<String> ADDED_MC_1_20 = makeSet(Arrays.asList(
                "SUSPICIOUS_GRAVEL", "PITCHER_PLANT", "SNIFFER_EGG", "PITCHER_CROP",
                "CALIBRATED_SCULK_SENSOR", "PITCHER_POD", "MUSIC_DISC_RELIC",
                "WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE", "SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE",
                "SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE", "RAISER_ARMOR_TRIM_SMITHING_TEMPLATE",
                "HOST_ARMOR_TRIM_SMITHING_TEMPLATE", "ANGLER_POTTERY_SHERD",
                "ARCHER_POTTERY_SHERD", "ARMS_UP_POTTERY_SHERD", "BLADE_POTTERY_SHERD",
                "BREWER_POTTERY_SHERD", "BURN_POTTERY_SHERD", "DANGER_POTTERY_SHERD",
                "EXPLORER_POTTERY_SHERD", "FRIEND_POTTERY_SHERD", "HEART_POTTERY_SHERD",
                "HEARTBREAK_POTTERY_SHERD", "HOWL_POTTERY_SHERD", "MINER_POTTERY_SHERD",
                "MOURNER_POTTERY_SHERD", "PLENTY_POTTERY_SHERD", "PRIZE_POTTERY_SHERD",
                "SHEAF_POTTERY_SHERD", "SHELTER_POTTERY_SHERD", "SKULL_POTTERY_SHERD",
                "SNORT_POTTERY_SHERD"
        ));

        // All material names (Material enum) added Minecraft 1.20.2 -> 1.20.3
        public static final Set<String> ADDED_MC_1_20_3 = makeSet(Arrays.asList(
                "TUFF_SLAB", "TUFF_STAIRS", "TUFF_WALL", "CHISELED_TUFF", "POLISHED_TUFF",
                "POLISHED_TUFF_SLAB", "POLISHED_TUFF_STAIRS", "POLISHED_TUFF_WALL",
                "TUFF_BRICKS", "TUFF_BRICK_SLAB", "TUFF_BRICK_STAIRS", "TUFF_BRICK_WALL",
                "CHISELED_TUFF_BRICKS", "CHISELED_COPPER", "EXPOSED_CHISELED_COPPER",
                "WEATHERED_CHISELED_COPPER", "OXIDIZED_CHISELED_COPPER",
                "WAXED_CHISELED_COPPER", "WAXED_EXPOSED_CHISELED_COPPER",
                "WAXED_WEATHERED_CHISELED_COPPER", "WAXED_OXIDIZED_CHISELED_COPPER",
                "COPPER_DOOR", "EXPOSED_COPPER_DOOR", "WEATHERED_COPPER_DOOR",
                "OXIDIZED_COPPER_DOOR", "WAXED_COPPER_DOOR", "WAXED_EXPOSED_COPPER_DOOR",
                "WAXED_WEATHERED_COPPER_DOOR", "WAXED_OXIDIZED_COPPER_DOOR",
                "COPPER_TRAPDOOR", "EXPOSED_COPPER_TRAPDOOR", "WEATHERED_COPPER_TRAPDOOR",
                "OXIDIZED_COPPER_TRAPDOOR", "WAXED_COPPER_TRAPDOOR", "WAXED_EXPOSED_COPPER_TRAPDOOR",
                "WAXED_WEATHERED_COPPER_TRAPDOOR", "WAXED_OXIDIZED_COPPER_TRAPDOOR",
                "CRAFTER", "BREEZE_SPAWN_EGG", "COPPER_GRATE", "EXPOSED_COPPER_GRATE",
                "WEATHERED_COPPER_GRATE", "OXIDIZED_COPPER_GRATE", "WAXED_COPPER_GRATE",
                "WAXED_EXPOSED_COPPER_GRATE", "WAXED_WEATHERED_COPPER_GRATE",
                "WAXED_OXIDIZED_COPPER_GRATE", "COPPER_BULB", "EXPOSED_COPPER_BULB",
                "WEATHERED_COPPER_BULB", "OXIDIZED_COPPER_BULB", "WAXED_COPPER_BULB",
                "WAXED_EXPOSED_COPPER_BULB", "WAXED_WEATHERED_COPPER_BULB",
                "WAXED_OXIDIZED_COPPER_BULB", "TRIAL_SPAWNER", "TRIAL_KEY"
        ));

        // All material names (Material enum) added Minecraft 1.20.4 -> 1.20.5
        public static final Set<String> ADDED_MC_1_20_5 = makeSet(Arrays.asList(
                "HEAVY_CORE", "TURTLE_SCUTE", "ARMADILLO_SCUTE", "WOLF_ARMOR",
                "ARMADILLO_SPAWN_EGG", "BOGGED_SPAWN_EGG", "WIND_CHARGE",
                "MACE", "FLOW_BANNER_PATTERN", "GUSTER_BANNER_PATTERN",
                "FLOW_ARMOR_TRIM_SMITHING_TEMPLATE", "BOLT_ARMOR_TRIM_SMITHING_TEMPLATE",
                "FLOW_POTTERY_SHERD", "GUSTER_POTTERY_SHERD", "SCRAPE_POTTERY_SHERD",
                "OMINOUS_TRIAL_KEY", "VAULT", "OMINOUS_BOTTLE", "BREEZE_ROD"
        ));

        // All material names (Material enum) added Minecraft 1.20.6 -> 1.21
        public static final Set<String> ADDED_MC_1_21 = makeSet(Arrays.asList(
                "MUSIC_DISC_CREATOR", "MUSIC_DISC_CREATOR_MUSIC_BOX", "MUSIC_DISC_PRECIPICE"
        ));

        // All material names (Material enum) added Minecraft 1.21.1 -> 1.21.2
        public static final Set<String> ADDED_MC_1_21_2 = makeSet(
                makeWoodMaterials("PALE_OAK"),
                makeColoredMaterials("BUNDLE"),
                Arrays.asList(
                        "PALE_MOSS_CARPET", "PALE_HANGING_MOSS", "PALE_MOSS_BLOCK",
                        "CREAKING_HEART", "CREAKING_SPAWN_EGG",
                        "FIELD_MASONED_BANNER_PATTERN", "BORDURE_INDENTED_BANNER_PATTERN"

        ));

        // All material names (Material enum) added Minecraft 1.21.3 -> 1.21.4
        public static final Set<String> ADDED_MC_1_21_4 = makeSet(Arrays.asList(
                "OPEN_EYEBLOSSOM", "CLOSED_EYEBLOSSOM",
                "RESIN_CLUMP", "RESIN_BLOCK", "RESIN_BRICKS",
                "RESIN_BRICK_STAIRS", "RESIN_BRICK_SLAB",
                "RESIN_BRICK_WALL", "CHISELED_RESIN_BRICKS",
                "RESIN_BRICK",
                "POTTED_OPEN_EYEBLOSSOM", "POTTED_CLOSED_EYEBLOSSOM"
        ));

        // All material names (Material enum) added Minecraft 1.21.4 -> 1.21.5
        public static final Set<String> ADDED_MC_1_21_5 = makeSet(Arrays.asList(
                "BUSH", "FIREFLY_BUSH", "SHORT_DRY_GRASS", "TALL_DRY_GRASS",
                "WILDFLOWERS", "LEAF_LITTER", "CACTUS_FLOWER",
                "TEST_BLOCK", "TEST_INSTANCE_BLOCK",
                "BLUE_EGG", "BROWN_EGG"
        ));
    }
}
