package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.server.IRegistryHandle;

/**
 * A type of Window (Menu UI) in Minecraft
 */
public enum WindowType {
    GENERIC_9X1("minecraft:chest", "generic_9x1", 9),
    GENERIC_9X2("minecraft:chest", "generic_9x2", 18),
    GENERIC_9X3("minecraft:chest", "generic_9x3", 27),
    GENERIC_9X4("minecraft:chest", "generic_9x4", 36),
    GENERIC_9X5("minecraft:chest", "generic_9x5", 45),
    GENERIC_9X6("minecraft:chest", "generic_9x6", 54),
    GENERIC_3X3("minecraft:dispenser", "generic_3x3", 9),
    ANVIL("minecraft:anvil", "anvil", 0),
    BEACON("minecraft:beacon", "beacon", 1),
    FURNACE("minecraft:furnace", "furnace", 3),
    BLAST_FURNACE("minecraft:furnace", "blast_furnace", 3),
    BREWING_STAND("minecraft:brewing_stand", "brewing_stand", 4),
    CRAFTING("minecraft:crafting_table", "crafting", 10),
    ENCHANTMENT("minecraft:enchanting_table", "enchantment", 0),
    GRINDSTONE("minecraft:container", "grindstone", 3),
    HOPPER("minecraft:hopper", "hopper", 5),
    LECTERN("minecraft:container", "lectern", 3),
    LOOM("minecraft:container", "loom", 3),
    MERCHANT("minecraft:villager", "merchant", 3),
    SHULKER_BOX("minecraft:shulker_box", "shulker_box", 27),
    SMOKER("minecraft:container", "smoker", 0),
    CARTOGRAPHY("minecraft:container", "cartography", 3),
    STONECUTTER("minecraft:container", "stonecutter", 11),
    // HORSE(null, null, 2), //TODO!
    UNKNOWN(null, null, 0);

    private final String name_1_8;
    private final int id_1_14;
    private final int slotCount;

    private WindowType(String name_1_8, String name_1_14, int slotCount) {
        this.name_1_8 = name_1_8;
        this.slotCount = slotCount;
        if (CommonCapabilities.HAS_WINDOW_TYPE_REGISTRY) {
            this.id_1_14 = IRegistryHandle.getWindowIdFromName(name_1_14);
        } else {
            this.id_1_14 = this.ordinal();
        }
    }

    /**
     * Gets the number of inventory item slots this window has
     * 
     * @return inventory item slots
     */
    public int getInventorySlots() {
        return this.slotCount;
    }

    /**
     * Gets a unique type id of this window type
     * 
     * @return type id
     */
    public int getTypeId() {
        return this.id_1_14;
    }

    /**
     * Gets the window name as used on MC 1.8 - MC 1.13.2
     * 
     * @return legact window name
     */
    public String getLegacyName_1_8() {
        return this.name_1_8;
    }

    /**
     * Gets whether this Window Type is supported on the current version of Minecraft
     * 
     * @return True if supported
     */
    public boolean isSupported() {
        if (CommonCapabilities.HAS_WINDOW_TYPE_REGISTRY) {
            return this.id_1_14 != -1;
        } else {
            return this.name_1_8 != null;
        }
    }

    /**
     * Obtains a Window Type from a legacy MC 1.8 - MC 1.13.2 window name
     * 
     * @param legacyName
     * @param slotCount
     * @return window type, UNKNOWN if invalid
     */
    public static WindowType fromLegacyName_1_8(String legacyName, int slotCount) {
        // Gone on 1.14 and later, but is the same inventory as the dispenser.
        if ("minecraft:dropper".equals(legacyName)) {
            return GENERIC_3X3;
        }

        WindowType result = UNKNOWN;
        for (WindowType type : WindowType.values()) {
            if (type.name_1_8.equals(legacyName)) {
                result = type;
                if (type.slotCount == slotCount) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * Obtains a Window Type from the window type id used internally on 1.14 and later
     * 
     * @param windowTypeId
     * @return window type, UNKNOWN if invalid
     */
    public static WindowType fromWindowTypeId(int windowTypeId) {
        for (WindowType type : WindowType.values()) {
            if (type.id_1_14 == windowTypeId) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
