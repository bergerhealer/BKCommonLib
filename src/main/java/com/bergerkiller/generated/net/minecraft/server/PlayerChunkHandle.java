package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PlayerChunkHandle extends Template.Handle {
    /** @See {@link PlayerChunkClass} */
    public static final PlayerChunkClass T = new PlayerChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerChunkHandle.class, "net.minecraft.server.PlayerChunk");

    /* ============================================================================== */

    public static PlayerChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public void addPlayer(Player player) {
        T.addPlayer.invoke(getRaw(), player);
    }

    public void removePlayer(Player player) {
        T.removePlayer.invoke(getRaw(), player);
    }

    public void sendChunk(Player player) {
        T.sendChunk.invoke(getRaw(), player);
    }


    public org.bukkit.Chunk getChunk(org.bukkit.World world) {
        if (T.opt_loaded_chunk.isAvailable()) {
            return T.opt_loaded_chunk.get(getRaw());
        } else {
            IntVector2 loc = this.getLocation();
            if (world.isChunkLoaded(loc.x, loc.z)) {
                return world.getChunkAt(loc.x, loc.z);
            } else {
                return null;
            }
        }
    }
    public PlayerChunkMapHandle getPlayerChunkMap() {
        return T.playerChunkMap.get(getRaw());
    }

    public void setPlayerChunkMap(PlayerChunkMapHandle value) {
        T.playerChunkMap.set(getRaw(), value);
    }

    public List<Player> getPlayers() {
        return T.players.get(getRaw());
    }

    public void setPlayers(List<Player> value) {
        T.players.set(getRaw(), value);
    }

    public IntVector2 getLocation() {
        return T.location.get(getRaw());
    }

    public void setLocation(IntVector2 value) {
        T.location.set(getRaw(), value);
    }

    public int getDirtyCount() {
        return T.dirtyCount.getInteger(getRaw());
    }

    public void setDirtyCount(int value) {
        T.dirtyCount.setInteger(getRaw(), value);
    }

    public int getDirtySectionMask() {
        return T.dirtySectionMask.getInteger(getRaw());
    }

    public void setDirtySectionMask(int value) {
        T.dirtySectionMask.setInteger(getRaw(), value);
    }

    public boolean isDone() {
        return T.done.getBoolean(getRaw());
    }

    public void setDone(boolean value) {
        T.done.setBoolean(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PlayerChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerChunkClass extends Template.Class<PlayerChunkHandle> {
        public final Template.Field.Converted<PlayerChunkMapHandle> playerChunkMap = new Template.Field.Converted<PlayerChunkMapHandle>();
        public final Template.Field.Converted<List<Player>> players = new Template.Field.Converted<List<Player>>();
        public final Template.Field.Converted<IntVector2> location = new Template.Field.Converted<IntVector2>();
        @Template.Optional
        public final Template.Field.Converted<Chunk> opt_loaded_chunk = new Template.Field.Converted<Chunk>();
        public final Template.Field.Integer dirtyCount = new Template.Field.Integer();
        public final Template.Field.Integer dirtySectionMask = new Template.Field.Integer();
        public final Template.Field.Boolean done = new Template.Field.Boolean();

        public final Template.Method.Converted<Void> addPlayer = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> removePlayer = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> sendChunk = new Template.Method.Converted<Void>();

    }

}

