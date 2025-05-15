package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.generated.net.minecraft.core.RegistryMaterialsHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is needed because Minecraft is really stupid sometimes
 */
public class ModelInfoLookup {
    private static final MinecraftKeyHandle MISSING_MODEL_NAME = MinecraftKeyHandle.createNew("minecraft", "builtin/missing");
    private static final Map<Material, List<ModelNameEntry>> byMaterial = new EnumMap<>(Material.class);
    private static final Map<String, ModelNameEntry> byItemName = new HashMap<>();
    private static final Map<String, ModelNameEntry> byBlockName = new HashMap<>();

    // Initialize the registry
    static {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // Register all materials that aren't legacy materials
            for (Material material : CommonLegacyMaterials.getAllMaterials()) {
                if (!MaterialUtil.isLegacyType(material)) {
                    byMaterial.put(material, ModelNameEntry.createEntriesFor(material));
                }
            }

            // Try to find the non-legacy name for all legacy names, or lookup otherwise
            for (Material legacyMaterial : CommonLegacyMaterials.getAllLegacyMaterials()) {
                if (MaterialUtil.isBlock(legacyMaterial)) {
                    BlockData blockData = BlockData.fromMaterial(legacyMaterial);
                    if (blockData == null) {
                        continue; // Broken?
                    }
                    List<ModelNameEntry> existing = byMaterial.get(blockData.getType());
                    if (existing != null) {
                        byMaterial.put(legacyMaterial, existing);
                        continue;
                    }
                } else {
                    ItemHandle itemHandle = ItemHandle.fromMaterial(legacyMaterial);
                    if (itemHandle == null) {
                        continue; // Broken?
                    }
                    Material actualMaterial = CraftMagicNumbersHandle.getMaterialFromItem(itemHandle.getRaw());
                    if (actualMaterial == null) {
                        continue; // Broken?
                    }
                    List<ModelNameEntry> existing = byMaterial.get(actualMaterial);
                    if (existing != null) {
                        byMaterial.put(legacyMaterial, existing);
                        continue;
                    }
                }

                // Create for a legacy material anyways. Weird! This probably shouldn't happen.
                byMaterial.put(legacyMaterial, ModelNameEntry.createEntriesFor(legacyMaterial));
            }
        } else {
            // Only legacy materials exist, so the logic is simplified
            for (Material material : CommonLegacyMaterials.getAllMaterials()) {
                byMaterial.put(material, ModelNameEntry.createEntriesFor(material));
            }
        }

        // Reverse-register all entries by name
        for (List<ModelNameEntry> list : byMaterial.values()) {
            for (ModelNameEntry entry : list) {
                entry.getItemName().ifPresent(name -> byItemName.put(name, entry));
                entry.getBlockName().ifPresent(name -> byBlockName.put(name, entry));
            }
        }

        // Weirdness with potions...
        {
            ModelNameEntry e = byItemName.get("bottle_drinkable");
            if (e != null) {
                byItemName.put("potion", e);
            }
        }
    }

    public static ItemRenderOptions lookupItemRenderOptions(ItemStack item) {
        return lookupItemRenderOptions(CommonItemStack.of(item));
    }

    public static ItemRenderOptions lookupItemRenderOptions(CommonItemStack item) {
        // Blocks
        Material type = item.getType();
        if (MaterialUtil.isBlock(type)) {
            BlockRenderOptions blockOpt = BlockData.fromItemStack(item).getDefaultRenderOptions();
            return new ItemRenderOptions(item, blockOpt);
        }

        ItemStackHandle handle = item.getHandle().orElseThrow(() -> new IllegalStateException("Item should not be empty"));

        // Some items, like leather boots, require additional render options passed
        ItemRenderOptions options = new ItemRenderOptions(item, "");
        if (MaterialUtil.ISLEATHERARMOR.get(type)) {
            options.put("layer0tint", String.format("#%06x", handle.getLeatherArmorColor()));
        }

        // Similarly, the liquid inside potion bottles have a color set
        if (MaterialUtil.ISPOTION.get(type)) {
            // Convert color to hexadecimal and store it as an option
            options.put("layer0tint", String.format("#%06x", handle.getPotionColor()));
        }

        // damage and damaged properties of weapons, armor and tools
        if (item.isDamageSupported()) {
            options.put("damaged", item.isUnbreakable() ? "0" : "1");
            options.put("damage", Double.toString((double) item.getDamage() / (double) (item.getMaxDamage() + 1)));
        }

        // custom model data
        if (CommonCapabilities.HAS_CUSTOM_MODEL_DATA) {
            if (item.hasCustomModelData()) {
                options.put("custom_model_data", Integer.toString(item.getCustomModelData()));
            }
        }

        return options;
    }

    // Used in generated code!
    public static int getPotionColor(int durability) {
        // Colors obtained by reverse-engineering the sprites
        // Top-left color of the grayscale template is value 251
        // To get the real input colors, level them input=251 output=255
        switch (durability & 0xf) {
        case 0x1: return 0xFF68FF; // Regeneration/Pink
        case 0x2: return 0x7BAEC6; // Speed/Sky Blue
        case 0x3: return 0xE39A39; // Fire Resistance/Orange
        case 0x4: return 0x4E9330; // Poison/Green
        case 0x5: return 0xF72322; // Instant Health/Red
        case 0x6: return 0x1F1FA0; // Night Vision/Navy Blue
        case 0x8: return 0x484D48; // Weakness/Gray
        case 0x9: return 0x932322; // Strength/Dark Red
        case 0xa: return 0x5A6B81; // Slowness/Blue-Gray
        case 0xb: return 0x21FF4C; // Jump Boost/Bright Green
        case 0xc: return 0x430A09; // Instant Damage/Dark-Brown
        case 0xd: return 0x2D5299; // Water Breathing/Blue2
        case 0xe: return 0x7E8392; // Invisibility/Light-Gray
        default:
            return 0x385DC6; // Water/Blue default color
        }
    }

    /**
     * Attempts to look up the item that displays a particular model name
     *
     * @param itemModelName Name of the model
     * @return The item that displays this model, or empty if not found
     */
    public static Optional<CommonItemStack> findItemStackByModelName(String itemModelName) {
        int nameSpace = itemModelName.indexOf(':');
        ModelNameEntry entry;
        if (nameSpace == -1) {
            entry = byItemName.get(itemModelName);
        } else if (itemModelName.startsWith("minecraft")) {
            entry = byItemName.get(itemModelName.substring(10));
        } else {
            entry = null; // Custom namespaces don't exist as vanilla items
        }

        if (entry != null) {
            return entry.toItemStack();
        }

        // Try to create a dummy ItemStack with the custom_model data component set
        if (CommonCapabilities.HAS_ITEM_MODEL_COMPONENT) {
            MinecraftKeyHandle key = MinecraftKeyHandle.createNew(itemModelName);
            if (key != null) {
                Material type = CommonPlugin.hasInstance()
                        ? CommonPlugin.getInstance().getFallbackItemModelType()
                        : CommonPlugin.getDefaultFallbackItemModelType();

                return Optional.of(CommonItemStack.create(type, 1).setItemModel(key));
            }
        }

        return Optional.empty();
    }

    /**
     * Attempts to look up the block material that displays a particular model name
     *
     * @param blockModelName Name of the model
     * @return The material that displays this model, or empty if not found
     */
    public static Optional<Material> findBlockMaterialByModelName(String blockModelName) {
        ModelNameEntry entry = byBlockName.get(blockModelName);
        if (entry != null) {
            return Optional.of(entry.getMaterial());
        } else {
            return Optional.empty();
        }
    }

    public static String lookupBlock(BlockRenderOptions options) {
        return byMaterial.getOrDefault(options.getBlockData().getType(), ModelNameEntry.UNKNOWN)
                .get(0).getBlockName().orElse("unknown");
    }

    public static String lookupItem(ItemRenderOptions options) {
        return lookupItem(options.getCommonItem());
    }

    public static String lookupItem(CommonItemStack itemStack) {
        MinecraftKeyHandle itemModel = itemStack.getItemModel();
        if (itemModel.getNamespace().equals("minecraft")) {
            return itemModel.getName();
        } else {
            return itemModel.toString();
        }
    }

    /**
     * Looks up the item model name of an item, completely ignoring the item model
     * data component. This is mostly for internal use.
     *
     * @param itemStack CommonItemStack
     * @return Set item model name key
     */
    public static MinecraftKeyHandle lookupVanillaItemModel(CommonItemStack itemStack) {
        return lookupItemModelNameEntry(itemStack).getItemNameKey()
                .orElse(MISSING_MODEL_NAME);
    }

    private static ModelNameEntry lookupItemModelNameEntry(CommonItemStack itemStack) {
        List<ModelNameEntry> entries = byMaterial.get(itemStack.getType());
        if (entries.size() > 1) {
            // Got to match it to the item
            for (ModelNameEntry entry : entries) {
                if (entry.matchDynamicItem(itemStack)) {
                    return entry;
                }
            }
        }
        return entries.get(0);
    }

    /**
     * A single registered name for a block and/or item.
     */
    private interface ModelNameEntry {
        List<ModelNameEntry> UNKNOWN = Collections.singletonList(new ModelNameEntry() {
            @Override
            public Material getMaterial() {
                throw new UnsupportedOperationException("UNKNOWN has no material");
            }

            @Override
            public Optional<String> getItemName() {
                return Optional.empty();
            }

            @Override
            public Optional<MinecraftKeyHandle> getItemNameKey() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getBlockName() {
                return Optional.empty();
            }

            @Override
            public Optional<CommonItemStack> toItemStack() {
                return Optional.empty();
            }
        });

        /**
         * Gets the block and/or item material that this entry represents
         *
         * @return Material
         */
        Material getMaterial();

        /**
         * Returns the item name, if this entry has an item
         *
         * @return Item model name
         */
        Optional<String> getItemName();

        /**
         * Gets the {@link #getItemName()} but as an encoded MinecraftKey
         *
         * @return Item model name as MinecraftKey
         */
        Optional<MinecraftKeyHandle> getItemNameKey();

        /**
         * Returns the block name, if this entry has a block
         *
         * @return Block name
         */
        Optional<String> getBlockName();

        /**
         * Creates a new CommonItemStack with the base properties of this model entry.
         * If this entry cannot be of an item (it's only for a Block), returns empty.
         *
         * @return CommonItemStack
         */
        Optional<CommonItemStack> toItemStack();

        /**
         * In case multiple entries can match the same Item Material, this match function can be implemented
         * to identify the correct entry by inspecting the item itself.
         *
         * @param item Item
         * @return True if it matches this specific entry
         */
        default boolean matchDynamicItem(CommonItemStack item) {
            return true;
        }

        static List<ModelNameEntry> createEntriesFor(Material material) {
            if (MaterialUtil.isBlock(material)) {
                return Collections.singletonList(new BlockDataModelNameEntry(BlockData.fromMaterial(material)));
            }

            // Legacy material logic, where some item properties that change the name are stored
            // in the item itself as damage values
            if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                String typeName = CommonLegacyMaterials.getMaterialName(material);
                if ("LEGACY_COOKED_FISH".equals(typeName)) {
                    return LegacyFishItemModelNameEntry.createLegacyFishMaterials(material, true);
                } else if ("LEGACY_RAW_FISH".equals(typeName)) {
                    return LegacyFishItemModelNameEntry.createLegacyFishMaterials(material, false);
                } else if ("LEGACY_INK_SACK".equals(typeName)) {
                    return LegacyInkSacItemModelNameEntry.createLegacyInkSacMaterials(material);
                }
            }

            return Collections.singletonList(new ItemModelNameEntry(material));
        }
    }

    /**
     * Used on Minecraft 1.8 - 1.12.2 to represent the different colors of dye,
     * as on these versions the color is stored in the item's data (durability).
     */
    private static class LegacyInkSacItemModelNameEntry implements ModelNameEntry {
        private final Material inkSacMaterial;
        private final int damageValue;
        private final String itemName;
        private final MinecraftKeyHandle itemNameKey;

        public static List<ModelNameEntry> createLegacyInkSacMaterials(Material inkSacItemMaterial) {
            return Arrays.asList(
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 0, "black"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 1, "red"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 2, "green"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 3, "brown"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 4, "blue"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 5, "purple"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 6, "cyan"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 7, "silver"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 8, "gray"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 9, "pink"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 10, "lime"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 11, "yellow"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 12, "light_blue"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 13, "magenta"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 14, "orange"),
                    new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 15, "white")
            );
        }

        private LegacyInkSacItemModelNameEntry(Material inkSacMaterial, int damageValue, String colorName) {
            this.inkSacMaterial = inkSacMaterial;
            this.damageValue = damageValue;
            this.itemName = "dye_" + colorName;
            this.itemNameKey = MinecraftKeyHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return inkSacMaterial;
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(itemName);
        }

        @Override
        public Optional<MinecraftKeyHandle> getItemNameKey() {
            return Optional.of(itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.empty();
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(CommonItemStack.create(inkSacMaterial, 1).setDamage(damageValue));
        }

        @Override
        public boolean matchDynamicItem(CommonItemStack item) {
            return item.getDamage() == damageValue;
        }
    }

    /**
     * Used on Minecraft 1.8 - 1.12.2 to represent the 4 different types of fish,
     * as on these versions the type is stored in the item's data (durability).
     */
    private static class LegacyFishItemModelNameEntry implements ModelNameEntry {
        private final Material fishItemMaterial;
        private final int damageValue;
        private final String itemName;
        private final MinecraftKeyHandle itemNameKey;

        public static List<ModelNameEntry> createLegacyFishMaterials(Material fishItemMaterial, boolean cooked) {
            return Arrays.asList(
                    new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "cod", 0),
                    new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "salmon", 1),
                    new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "clownfish", 2),
                    new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "pufferfish", 3)
            );
        }

        private LegacyFishItemModelNameEntry(Material fishItemMaterial, boolean cooked, String fishName, int damageValue) {
            this.fishItemMaterial = fishItemMaterial;
            this.damageValue = damageValue;
            this.itemName = (cooked ? "cooked_" : "") + fishName;
            this.itemNameKey = MinecraftKeyHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return fishItemMaterial;
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(itemName);
        }

        @Override
        public Optional<MinecraftKeyHandle> getItemNameKey() {
            return Optional.of(itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.empty();
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(CommonItemStack.create(fishItemMaterial, 1).setDamage(damageValue));
        }

        @Override
        public boolean matchDynamicItem(CommonItemStack item) {
            return item.getDamage() == damageValue;
        }
    }

    /**
     * This is for items derived from Materials exactly. If more than one unique name
     * exists for a Material item, then a custom implementation of ModelInfoEntry
     * must be made.
     */
    private static class ItemModelNameEntry implements ModelNameEntry {
        public final Material itemMaterial;
        public final String itemName;
        private final MinecraftKeyHandle itemNameKey;

        // Note: only for non-block materials!
        public ItemModelNameEntry(Material itemMaterial) {
            this.itemMaterial = itemMaterial;
            this.itemName = lookupItemResourceName(itemMaterial);
            this.itemNameKey = MinecraftKeyHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return itemMaterial;
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(itemName);
        }

        @Override
        public Optional<MinecraftKeyHandle> getItemNameKey() {
            return Optional.of(itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.empty();
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(CommonItemStack.create(itemMaterial, 1));
        }

        private static String lookupItemResourceName(Material itemMaterial) {
            String itemName;

            Object itemHandle = HandleConversion.toItemHandle(itemMaterial);
            Object minecraftKey = RegistryMaterialsHandle.T.getKey.invoke(ItemHandle.getRegistry(), itemHandle);
            itemName = MinecraftKeyHandle.T.getName.invoke(minecraftKey);

            // Perform renames needed to get the correct item model name
            String typeName = CommonLegacyMaterials.getMaterialName(itemMaterial);
            if (LogicUtil.contains(typeName, "POTION", "LEGACY_POTION")) {
                itemName = "bottle_drinkable";
            } else if (LogicUtil.contains(typeName, "LINGERING_POTION", "LEGACY_LINGERING_POTION")) {
                itemName = "bottle_lingering";
            } else if (LogicUtil.contains(typeName, "SPLASH_POTION", "LEGACY_SPLASH_POTION")) {
                itemName = "bottle_splash";
            } else if (LogicUtil.contains(typeName, "LEGACY_WOOD_DOOR")) {
                itemName = "oak_door";
            } else if (LogicUtil.contains(typeName, "LEGACY_BOAT")) {
                itemName = "oak_boat";
            } else if (LogicUtil.contains(typeName, "TOTEM_OF_UNDYING", "LEGACY_TOTEM")) {
                itemName = "totem"; // totem_of_undying otherwise
            }

            return itemName;
        }
    }

    private static class BlockDataModelNameEntry implements ModelNameEntry {
        public final BlockData blockData;
        public final String blockName;
        public final String itemName;
        public final MinecraftKeyHandle itemNameKey;

        public BlockDataModelNameEntry(BlockData blockData) {
            this.blockData = blockData;
            this.blockName = lookupBlockResourceName(blockData.getDefaultRenderOptions());

            // Perform renames needed to get the correct item block model name
            if (blockName.equals("fence")) {
                itemName = "oak_fence";
            } else if (blockName.equals("fence_gate")) {
                itemName = "oak_fence_gate";
            } else if (blockName.equals("wooden_door")) {
                itemName = "oak_door";
            } else {
                itemName = blockName;
            }
            itemNameKey = MinecraftKeyHandle.createNew("minecraft", itemName);
        }

        @Override
        public Material getMaterial() {
            return blockData.getType();
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(itemName);
        }

        @Override
        public Optional<MinecraftKeyHandle> getItemNameKey() {
            return Optional.of(itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.of(blockName);
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(blockData.createCommonItem(1));
        }

        private static String lookupBlockResourceName(BlockRenderOptions options) {
            String name = options.getBlockData().getBlockName();
            String variant = options.get("variant");

            // MC 1.13 sign -> legacy_standing_sign and wall_sign -> legacy_wall_sign
            if (!CommonCapabilities.HAS_MATERIAL_SIGN_TYPES) {
                if (name.equals("sign") || name.equals("standing_sign")) {
                    name = "legacy_sign";
                } else if (name.equals("wall_sign")) {
                    name = "legacy_wall_sign";
                }
            }

            // Not all slabs are equal
            if (CommonCapabilities.BLOCK_SLAB_HAS_OWN_BLOCK) {
                variant = null;
            } else {
                if (name.equals("purpur_slab") || name.equals("purpur_double_slab")) {
                    variant = null;
                } else if (name.contains("_slab")) {
                    if (variant == null) {
                        variant = "stone";
                    }
                    name = "slab";
                }
            }

            // Taxonomy is pretty important
            if (name.equals("sapling")) {
                String type = options.get("type");
                if (type == null) {
                    type = "oak";
                }
                name = type + "_" + name;
            }

            // Color me surprised
            if (
                    name.equals("wool") ||
                            name.equals("carpet") ||
                            name.equals("concrete") ||
                            name.equals("concrete_powder") ||
                            name.equals("stained_hardened_clay") ||
                            name.equals("stained_glass_pane") ||
                            name.equals("stained_glass")
            ) {
                String color = options.get("color");
                if (color == null) {
                    color = "white";
                }
                name = color + "_" + name;
            }

            // Rolls down stairs, alone or in pairs, rolls over your neighbor's dog!
            if (name.equals("log2")) {
                if (variant == null) {
                    variant = "oak";
                }
                name = "log";
            }

            // Uweh!
            if (name.equals("leaves2")) {
                name = "leaves";
            }

            // Half a plant is no plant at all
            if (name.equals("double_plant")) {
                name = ""; // only variant is used
                if (variant == null) {
                    variant = "double_grass";
                }
            }

            // FAIL
            if (name.equals("tallgrass")) {
                name = "tall_grass";
            }
            if (name.equals("deadbush")) {
                name = "dead_bush";
            }

            // Roses are red, violets are blue
            // These naming conventions need a review
            if (name.equals("red_flower") || name.equals("yellow_flower")) {
                String type = options.get("type");
                if (type == null) {
                    type = "red_tulip";
                }
                name = type;
            }

            // Special needs blocks
            if (
                    name.equals("brown_mushroom_block") ||
                            name.equals("red_mushroom_block") ||
                            name.equals("stonebrick") ||
                            name.equals("cobblestone_wall") ||
                            name.equals("stone") ||
                            name.equals("prismarine") ||
                            name.equals("purpur_slab") ||
                            name.equals("quartz_block") ||
                            name.equals("dirt") ||
                            name.equals("sand")
            ) {
                variant = null; //suppress
            }

            if (name.equals("end_gateway")) {
                name = "end_portal_frame";
            }

            // Now add the variant!
            if (variant != null) {
                if (name.length() > 0) {
                    name = variant + "_" + name;
                } else {
                    name = variant;
                }
            }

            return name;
        }
    }
}
