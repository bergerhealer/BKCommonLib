package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.server.EnumMoveTypeHandle;

public enum MoveType {
    PISTON(EnumMoveTypeHandle.PISTON),
    PLAYER(EnumMoveTypeHandle.PLAYER),
    SELF(EnumMoveTypeHandle.SELF),
    SHULKER(EnumMoveTypeHandle.SHULKER),
    SHULKER_BOX(EnumMoveTypeHandle.SHULKER_BOX);

    private final EnumMoveTypeHandle handle;

    private MoveType(EnumMoveTypeHandle handle) {
        this.handle = handle;
    }

    /**
     * Gets the net.minecraft.server handle version of the enumeration
     * 
     * @return MoveType enum handle
     */
    public Object getHandle() {
        return handle.getRaw();
    }

    /**
     * Gets the MoveType wrapper from a net.minecraft.server handle version
     * 
     * @param handle to get the MoveType of
     * @return MoveType, or null if it could not be converted
     */
    public static MoveType getFromHandle(Object handle) {
        if (EnumMoveTypeHandle.T.isAssignableFrom(handle)) {
            for (MoveType type : MoveType.values()) {
                if (type.handle.getRaw() == handle) {
                    return type;
                }
            }
        }
        return null;
    }
}
