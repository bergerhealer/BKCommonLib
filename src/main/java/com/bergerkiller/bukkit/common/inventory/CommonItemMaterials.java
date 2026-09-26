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
    public static final Material EMPTY_MAP = MaterialUtil.getFirst("MAP", "LEGACY_EMPTY_MAP");
    public static final Material STICK = MaterialUtil.getFirst("STICK", "LEGACY_STICK");

    /**
     * Since 26.3 there are unique map types. These are constants for those map types, falling back to the FILLED_MAP
     * type when the server version is before this one.<br>
     * <br>
     * See: <a href="https://minecraft.wiki/w/Explorer_Map">https://minecraft.wiki/w/Explorer_Map</a>
     */
    public static final class Maps {
        public static final Material OCEAN_MONUMENT = MaterialUtil.getMaterial("OCEAN_MONUMENT_MAP", FILLED_MAP);
        public static final Material WOODLAND_MANSION = MaterialUtil.getMaterial("WOODLAND_MANSION_MAP", FILLED_MAP);
        public static final Material BURIED_TRIAL_CHAMBERS = MaterialUtil.getMaterial("BURIED_TRIAL_CHAMBERS_MAP", FILLED_MAP);
        public static final Material JUNGLE_PYRAMID = MaterialUtil.getMaterial("JUNGLE_PYRAMID_MAP", FILLED_MAP);
        public static final Material SWAMP_HUT = MaterialUtil.getMaterial("SWAMP_HUT_MAP", FILLED_MAP);
        public static final Material DESERT_VILLAGE = MaterialUtil.getMaterial("DESERT_VILLAGE_MAP", FILLED_MAP);
        public static final Material PLAINS_VILLAGE = MaterialUtil.getMaterial("PLAINS_VILLAGE_MAP", FILLED_MAP);
        public static final Material SAVANNA_VILLAGE = MaterialUtil.getMaterial("SAVANNA_VILLAGE_MAP", FILLED_MAP);
        public static final Material SNOWY_VILLAGE = MaterialUtil.getMaterial("SNOWY_VILLAGE_MAP", FILLED_MAP);
        public static final Material TAIGA_VILLAGE = MaterialUtil.getMaterial("TAIGA_VILLAGE_MAP", FILLED_MAP);
        public static final Material BURIED_TREASURE = MaterialUtil.getMaterial("BURIED_TREASURE_MAP", FILLED_MAP);
        public static final Material BURIED_ANCIENT_CITY = MaterialUtil.getMaterial("BURIED_ANCIENT_CITY_MAP", FILLED_MAP);
        public static final Material BURIED_MINESHAFT = MaterialUtil.getMaterial("BURIED_MINESHAFT_MAP", FILLED_MAP);
        public static final Material DESERT_PYRAMID = MaterialUtil.getMaterial("DESERT_PYRAMID_MAP", FILLED_MAP);
        public static final Material ABANDONED_CAMP = MaterialUtil.getMaterial("ABANDONED_CAMP_MAP", FILLED_MAP);
        public static final Material WARM_OCEAN_RUINS = MaterialUtil.getMaterial("WARM_OCEAN_RUINS_MAP", FILLED_MAP);
    }
}
