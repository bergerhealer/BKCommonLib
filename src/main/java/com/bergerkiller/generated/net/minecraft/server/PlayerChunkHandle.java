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
public abstract class PlayerChunkHandle extends Template.Handle {
    /** @See {@link PlayerChunkClass} */
    public static final PlayerChunkClass T = new PlayerChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerChunkHandle.class, "net.minecraft.server.PlayerChunk", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PlayerChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void addPlayer(Player player);
    public abstract void removePlayer(Player player);
    public abstract void sendChunk(Player player);

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
    public abstract PlayerChunkMapHandle getPlayerChunkMap();
    public abstract void setPlayerChunkMap(PlayerChunkMapHandle value);
    public abstract List<Player> getPlayers();
    public abstract void setPlayers(List<Player> value);
    public abstract IntVector2 getLocation();
    public abstract void setLocation(IntVector2 value);
    public abstract int getDirtyCount();
    public abstract void setDirtyCount(int value);
    public abstract int getDirtySectionMask();
    public abstract void setDirtySectionMask(int value);
    public abstract boolean isDone();
    public abstract void setDone(boolean value);
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

