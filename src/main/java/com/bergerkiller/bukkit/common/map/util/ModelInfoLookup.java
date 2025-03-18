package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.Common;
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

/**
 * This is needed because Minecraft is really stupid sometimes
 */
public class ModelInfoLookup {

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

    private static String lookupBlock(BlockRenderOptions options, boolean item) {
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

    public static String lookupBlock(BlockRenderOptions options) {
        return lookupBlock(options, false);
    }

    public static String lookupItem(ItemRenderOptions options) {
        ItemStack item = options.getItem();
        Material type = item.getType();
        String itemName;
        if (MaterialUtil.isBlock(type)) {
            itemName = lookupBlock(BlockData.fromItemStack(item).getDefaultRenderOptions(), true);

            // Perform renames needed to get the correct item block model name
            if (itemName.equals("fence")) {
                itemName = "oak_fence";
            } else if (itemName.equals("fence_gate")) {
                itemName = "oak_fence_gate";
            } else if (itemName.equals("wooden_door")) {
                itemName = "oak_door";
            }
        } else {
            Object itemHandle = HandleConversion.toItemHandle(type);
            Object minecraftKey = RegistryMaterialsHandle.T.getKey.invoke(ItemHandle.getRegistry(), itemHandle);
            itemName = MinecraftKeyHandle.T.name.get(minecraftKey);

            // Perform renames needed to get the correct item model name
            String typeName = CommonLegacyMaterials.getMaterialName(type);
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
            } else if (LogicUtil.contains(typeName, "LEGACY_COOKED_FISH", "LEGACY_RAW_FISH")) {
                itemName = ItemHandle.T.getInternalName.invoke(itemHandle, item);

                if (itemName.startsWith("item.fish.")) {
                    if (itemName.endsWith(".raw")) {
                        itemName = itemName.substring(10, itemName.length() - 4);
                    } else if (itemName.endsWith(".cooked")) {
                        itemName = "cooked_" + itemName.substring(10, itemName.length() - 7);
                    }
                }
            } else if (LogicUtil.contains(typeName, "INK_SAC", "LEGACY_INK_SACK")) {
                // For dyes we must parse the color from the internal name, then stick dye_ in front of it
                itemName = ItemHandle.T.getInternalName.invoke(itemHandle, item);
                int lastIdx = itemName.lastIndexOf('.');
                if (lastIdx != -1) {
                    itemName = itemName.substring(lastIdx + 1);
                }
                itemName = "dye_" + itemName;

                // Fix some derps on Mojang's part...
                if (itemName.equals("dye_lightBlue")) {
                    itemName = "dye_light_blue";
                }

                // On MC 1.13, for some reason, ink sac itself is an exception
                if (itemName.equals("dye_ink_sac") && Common.evaluateMCVersion(">=", "1.13")) {
                    itemName = "ink_sac";
                }
            }
        }

        return itemName;
    }
}
