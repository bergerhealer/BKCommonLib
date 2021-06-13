package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInUseEntityHandle.EnumEntityUseActionHandle;

public enum UseAction {

    ATTACK(EnumEntityUseActionHandle.ATTACK),
    INTERACT(EnumEntityUseActionHandle.INTERACT),
    INTERACT_AT(EnumEntityUseActionHandle.INTERACT_AT);

    private final EnumEntityUseActionHandle handle;

    private UseAction(EnumEntityUseActionHandle handle) {
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
    public static UseAction fromHandle(Object handle) {
        for (UseAction action : values()) {
            if (action.getHandle() == handle) {
                return action;
            }
        }
        return ATTACK;
    }
}
