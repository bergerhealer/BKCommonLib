package com.bergerkiller.generated.net.minecraft.server.players;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.players.PlayerList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.players.PlayerList")
public abstract class PlayerListHandle extends Template.Handle {
    /** @see PlayerListClass */
    public static final PlayerListClass T = Template.Class.create(PlayerListClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getMaxPlayers();
    public abstract void setMaxPlayers(int maxPlayers);
    public abstract CommonTagCompound migratePlayerData(CommonTagCompound playerProfileData);
    public abstract void savePlayers();
    public abstract void savePlayerFile(Player entityplayer);
    public abstract void sendRawPacketNearby(World world, double x, double y, double z, double radius, Object packet);
    public abstract List<Player> getPlayers();
    public abstract void setPlayers(List<Player> value);
    /**
     * Stores class members for <b>net.minecraft.server.players.PlayerList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerListClass extends Template.Class<PlayerListHandle> {
        public final Template.Field.Converted<List<Player>> players = new Template.Field.Converted<List<Player>>();

        public final Template.Method<Integer> getMaxPlayers = new Template.Method<Integer>();
        public final Template.Method<Void> setMaxPlayers = new Template.Method<Void>();
        public final Template.Method.Converted<CommonTagCompound> migratePlayerData = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method<Void> savePlayers = new Template.Method<Void>();
        public final Template.Method.Converted<Void> savePlayerFile = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> sendRawPacketNearby = new Template.Method.Converted<Void>();

    }

}

