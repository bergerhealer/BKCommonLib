package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_8_R1.EnumEntityUseAction;

public enum UseAction {

    ATTACK(EnumEntityUseAction.ATTACK), INTERACT(EnumEntityUseAction.INTERACT);

    private final Object handle;

    private UseAction(Object handle) {
        this.handle = handle;
    }

    /**
     * Gets the internal EnumEntityUseAction handle
     *
     * @return handle
     */
    public Object getHandle() {
        return this.handle;
    }

    /**
     * Obtains the Use Action of a handle
     *
     * @param handle to get it for
     * @return UseAction
     */
    public static UseAction fromHandle(Object handle) {
        for (UseAction action : values()) {
            if (action.getHandle() == handle) {
                return action;
            }
        }
        return ATTACK;
    }
}
