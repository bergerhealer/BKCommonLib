package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.server.EnumMoveTypeHandle;

public enum MoveType {
    PISTON(EnumMoveTypeHandle.PISTON),
    PLAYER(EnumMoveTypeHandle.PLAYER),
    SELF(EnumMoveTypeHandle.SELF),
    SHULKER(EnumMoveTypeHandle.SHULKER),
    SHULKER_BOX(EnumMoveTypeHandle.SHULKER_BOX);

    private final Object handle;

    private MoveType(EnumMoveTypeHandle handle) {
        if (EnumMoveTypeHandle.T.isValid()) {
            this.handle = handle.getRaw();
        } else {
            this.handle = new Object(); // dummy
        }
    }

    /**
     * Gets the net.minecraft.server handle version of the enumeration
     * 
     * @return MoveType enum handle
     */
    public Object getHandle() {
        return handle;
    }

    /**
     * Gets the MoveType wrapper from a net.minecraft.server handle version
     * 
     * @param handle to get the MoveType of
     * @return MoveType, or null if it could not be converted
     */
    public static MoveType getFromHandle(Object handle) {
        for (MoveType type : MoveType.values()) {
            if (type.handle == handle) {
                return type;
            }
        }
        return null;
    }
}
