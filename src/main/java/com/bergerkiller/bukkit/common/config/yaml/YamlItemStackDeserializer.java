package com.bergerkiller.bukkit.common.config.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

/**
 * Deserializes Bukkit ItemStack objects from raw yaml, with the added feature
 * of supporting yaml produced on mc versions newer than the one it is running on.
 */
public class YamlItemStackDeserializer implements Function<Map<String, Object>, ItemStack> {
    private static final Function<Map<String, Object>, Boolean> NO_CONVERSION = map -> { return Boolean.TRUE; };
    private final List<ItemStackConverter> converters;
    private final int curr_version;
    private final int max_version;

    public YamlItemStackDeserializer() {
        this.converters = new ArrayList<>();
        this.curr_version = CraftMagicNumbersHandle.getDataVersion();

        // All data versions where the ItemStack YAML had a change that requires conversion
        // Put in order of old to recent

        // FROM MC 1.13 to 1.12.2 (perform the material enum remapping logic in reverse)
        this.register(0, map -> {
            Object type = map.get("type");
            if (type instanceof String && ((String) type).startsWith("LEGACY_")) {
                map.put("type", ((String) type).substring(7));
                return Boolean.TRUE;
            }

            if ("BEETROOTS".equals(type)) {
                map.put("type", "BEETROOT_BLOCK");
            } else if ("SPAWNER".equals(type)) {
                map.put("type", "MOB_SPAWNER");
            }

            //TODO: There are a lot more changes here!
            return Boolean.TRUE;
        });

        // From MC 1.13.1 to 1.13 (dead coral types no longer valid)
        this.register(1519, map -> {
            Object type = map.get("type");
            if (LogicUtil.contains(type,
                    "DEAD_BRAIN_CORAL", "DEAD_BUBBLE_CORAL",
                    "DEAD_FIRE_CORAL", "DEAD_HORN_CORAL",
                    "DEAD_TUBE_CORAL"
            )) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
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
            if (LogicUtil.contains(type,
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
                    "WANDERING_TRADER_SPAWN_EGG", "WHITE_DYE", "WITHER_ROSE"
            )) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
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
            if (LogicUtil.contains(type,
                    "BEEHIVE", "BEE_NEST", "BEE_SPAWN_EGG",
                    "HONEYCOMB", "HONEYCOMB_BLOCK",
                    "HONEY_BLOCK", "HONEY_BOTTLE"
            )) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });

        // From MC 1.15.1+ to 1.15 (no changes)
        this.register(2225, NO_CONVERSION);

        // Maximum supported data version
        this.max_version = 2227; // MC 1.15.1
    }

    // Registers a converter if it can convert from a future data version only
    // Current or older data versions are already natively supported by the server
    private void register(int data_version, Function<Map<String, Object>, Boolean> converter) {
        if (data_version <= this.curr_version && !this.converters.isEmpty()) {
            this.converters.remove(0);
        }
        this.converters.add(0, new ItemStackConverter(data_version, converter));
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        Object version_raw = args.get("v");
        if (version_raw instanceof Number) {
            int version = ((Number) version_raw).intValue();
            if (version > this.curr_version && version <= this.max_version) {
                // Requires conversion, go down the list of item stack converters and process those applicable
                for (ItemStackConverter converter : this.converters) {
                    if (version > converter.output_version) {
                        if (converter.converter.apply(args).booleanValue()) {
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
     * Converts the raw YAML from a data version to an older data version
     */
    private static final class ItemStackConverter {
        public final int output_version;
        public final Function<Map<String, Object>, Boolean> converter;

        public ItemStackConverter(int output_version, Function<Map<String, Object>, Boolean> converter) {
            this.output_version = output_version;
            this.converter = converter;
        }
    }
}
