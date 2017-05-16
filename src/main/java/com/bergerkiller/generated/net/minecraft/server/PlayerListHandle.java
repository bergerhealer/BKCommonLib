package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class PlayerListHandle extends Template.Handle {
    public static final PlayerListClass T = new PlayerListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerListHandle.class, "net.minecraft.server.PlayerList");


    /* ============================================================================== */

    public static PlayerListHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PlayerListHandle handle = new PlayerListHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object getPlayerFileData() {
        return T.playerFileData.get(instance);
    }

    public void setPlayerFileData(Object value) {
        T.playerFileData.set(instance, value);
    }

    public int getMaxPlayers() {
        return T.maxPlayers.getInteger(instance);
    }

    public void setMaxPlayers(int value) {
        T.maxPlayers.setInteger(instance, value);
    }

    public static final class PlayerListClass extends Template.Class {
        public final Template.Field.Converted<Object> playerFileData = new Template.Field.Converted<Object>();
        public final Template.Field.Integer maxPlayers = new Template.Field.Integer();

    }
}
