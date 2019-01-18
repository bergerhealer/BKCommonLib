package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PlayerListHandle extends Template.Handle {
    /** @See {@link PlayerListClass} */
    public static final PlayerListClass T = new PlayerListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerListHandle.class, "net.minecraft.server.PlayerList", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PlayerListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void savePlayers();
    public abstract void sendRawPacketNearby(World world, double x, double y, double z, double radius, Object packet);
    public abstract List<Player> getPlayers();
    public abstract void setPlayers(List<Player> value);
    public abstract IPlayerFileDataHandle getPlayerFileData();
    public abstract void setPlayerFileData(IPlayerFileDataHandle value);
    public abstract int getMaxPlayers();
    public abstract void setMaxPlayers(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PlayerList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerListClass extends Template.Class<PlayerListHandle> {
        public final Template.Field.Converted<List<Player>> players = new Template.Field.Converted<List<Player>>();
        public final Template.Field.Converted<IPlayerFileDataHandle> playerFileData = new Template.Field.Converted<IPlayerFileDataHandle>();
        public final Template.Field.Integer maxPlayers = new Template.Field.Integer();

        public final Template.Method<Void> savePlayers = new Template.Method<Void>();
        public final Template.Method.Converted<Void> sendRawPacketNearby = new Template.Method.Converted<Void>();

    }

}

