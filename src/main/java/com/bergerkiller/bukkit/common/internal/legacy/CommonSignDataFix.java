package com.bergerkiller.bukkit.common.internal.legacy;

import org.bukkit.Material;
import org.bukkit.material.Sign;

/**
 * Sign class for non-standard Sign material types to fix the isWallSign() function
 */
@Deprecated
public class CommonSignDataFix extends Sign {
    private final boolean _isWallSign;

    public CommonSignDataFix(Material legacy_data_type, byte legacy_data_value, boolean isWallSign) {
        super(legacy_data_type, legacy_data_value);
        this._isWallSign = isWallSign;
    }

    @Override
    public boolean isWallSign() {
        return this._isWallSign;
    }

    @Override
    public CommonSignDataFix clone() {
        return (CommonSignDataFix) super.clone();
    }
}
