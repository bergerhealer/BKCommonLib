package com.bergerkiller.bukkit.common.internal.legacy;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

/**
 * Simple class adding support for the Minecraft 1.16 Target block as a valid redstone source
 */
@Deprecated
public class CommonTargetDataFix extends MaterialData implements Redstone {

    public CommonTargetDataFix(Material target_material_type, byte data) {
        super(target_material_type, data);
    }

    @Override
    public boolean isPowered() {
        return this.getData() > 0;
    }

    @Override
    public CommonTargetDataFix clone() {
        return (CommonTargetDataFix) super.clone();
    }
}
