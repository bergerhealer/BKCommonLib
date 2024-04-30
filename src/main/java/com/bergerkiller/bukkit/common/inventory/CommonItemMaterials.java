package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import org.bukkit.Material;

/**
 * Lists Material constants for materials. Supports all versions of Minecraft.
 */
public class CommonItemMaterials {
    public static final Material SKULL = MaterialUtil.getFirst("PLAYER_HEAD", "LEGACY_SKULL_ITEM");
    public static final Material FILLED_MAP = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            CommonLegacyMaterials.getMaterial("FILLED_MAP") : CommonLegacyMaterials.getLegacyMaterial("MAP");
}
