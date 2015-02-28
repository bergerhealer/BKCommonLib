package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_8_R1.EnumScoreboardAction;

public enum ScoreboardAction {

    CHANGE(EnumScoreboardAction.CHANGE), REMOVE(EnumScoreboardAction.REMOVE);

    private final Object handle;

    private ScoreboardAction(Object handle) {
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
    public static ScoreboardAction fromHandle(Object handle) {
        for (ScoreboardAction action : values()) {
            if (action.getHandle() == handle) {
                return action;
            }
        }
        return CHANGE;
    }
}
