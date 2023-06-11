package com.bergerkiller.bukkit.common.internal.legacy;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

import java.util.EnumMap;

/**
 * Fakes hanging signs as having a 'Sign' material, emulating the properties
 * like attached face and facing. Attached face is always UP, facing can be
 * any face like sign post.<br>
 * <br>
 * If isSideWallSign is specified facing is limited to the 4 cardinal directions.
 */
@Deprecated
public class CommonHangingSignDataFix extends Sign {
    private final boolean isAlignedSign;

    public CommonHangingSignDataFix(Material legacy_data_type, byte legacy_data_value, boolean isAlignedSign) {
        super(legacy_data_type, legacy_data_value);
        this.isAlignedSign = isAlignedSign;
    }

    @Override
    public boolean isWallSign() {
        return false;
    }

    @Override
    public BlockFace getFacing() {
        if (this.isAlignedSign) {
            byte data = this.getData();
            switch (data) {
                case 2:
                    return BlockFace.NORTH;
                case 3:
                    return BlockFace.SOUTH;
                case 4:
                    return BlockFace.WEST;
                case 5:
                default:
                    return BlockFace.EAST;
            }
        } else {
            return ROTATION_FACES[super.getData() & 0xF];
        }
    }

    @Override
    public void setFacingDirection(BlockFace face) {
        if (this.isAlignedSign) {
            byte data;
            switch (face) {
                case NORTH:
                    data = 2;
                    break;
                case EAST:
                default:
                    data = 5;
                    break;
                case SOUTH:
                    data = 3;
                    break;
                case WEST:
                    data = 4;
            }
            this.setData(data);
        } else {
            setData(DATA_BY_ROTATION_FACE.getOrDefault(face, (byte) 0));
        }
    }

    private static final BlockFace[] ROTATION_FACES = new BlockFace[] {
            BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST,
            BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST,
            BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST,
            BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH,
            BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST, BlockFace.EAST,
            BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_EAST
    };
    private static final EnumMap<BlockFace, Byte> DATA_BY_ROTATION_FACE = new EnumMap<>(BlockFace.class);
    static {
        for (int i = 0; i < ROTATION_FACES.length; i++) {
            DATA_BY_ROTATION_FACE.put(ROTATION_FACES[i], (byte) i);
        }
    }

    @Override
    public BlockFace getAttachedFace() {
        return BlockFace.UP;
    }

    @Override
    public Sign clone() {
        return new CommonHangingSignDataFix(super.getItemType(), super.getData(), isAlignedSign);
    }
}
