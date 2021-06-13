package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutScoreboardScoreHandle.EnumScoreboardActionHandle;

public enum ScoreboardAction {

    CHANGE(EnumScoreboardActionHandle.CHANGE),
    REMOVE(EnumScoreboardActionHandle.REMOVE);

    private final EnumScoreboardActionHandle handle;

    private ScoreboardAction(EnumScoreboardActionHandle handle) {
        this.handle = handle;
    }

    /**
     * Gets the internal EnumEntityUseAction handle
     *
     * @return handle
     */
    public Object getHandle() {
        return this.handle.getRaw();
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
