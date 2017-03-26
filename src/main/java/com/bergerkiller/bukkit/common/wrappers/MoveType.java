package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_11_R1.EnumMoveType;

public enum MoveType {
    PISTON(EnumMoveType.PISTON),
    PLAYER(EnumMoveType.PLAYER),
    SELF(EnumMoveType.SELF),
    SHULKER(EnumMoveType.SHULKER),
    SHULKER_BOX(EnumMoveType.SHULKER_BOX);

    private final Object handle;

    private MoveType(Object handle) {
        this.handle = handle;
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
        if (handle instanceof EnumMoveType) {
            for (MoveType type : MoveType.values()) {
                if (type.handle == handle) {
                    return type;
                }
            }
        }
        return null;
    }
}
