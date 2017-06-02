package com.bergerkiller.bukkit.common.wrappers;

public enum ChatMessageType {
    CHAT((byte) 0), SYSTEM((byte) 1), GAME_INFO((byte) 2);

    private final byte _id;

    private ChatMessageType(byte value) {
        this._id = value;
    }

    public byte getId() {
        return this._id;
    }

    public static ChatMessageType getById(byte id) {
        for (ChatMessageType type : values()) {
            if (type._id == id) {
                return type;
            }
        }
        return SYSTEM; // fallback
    }
}
