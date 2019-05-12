package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Queue;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerChunkMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PlayerChunkMapHandle extends Template.Handle {
    /** @See {@link PlayerChunkMapClass} */
    public static final PlayerChunkMapClass T = new PlayerChunkMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerChunkMapHandle.class, "net.minecraft.server.PlayerChunkMap", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PlayerChunkMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean shouldUnload(int i, int j, int k, int l, int i1);
    public abstract PlayerChunkHandle getChunk(int x, int z);
    public abstract void flagDirty(IntVector3 blockposition);
    public abstract void flagPosDirty(int x, int y, int z);
    public abstract boolean isChunkEntered(EntityPlayerHandle entityplayer, int chunkX, int chunkZ);

    public void markForUpdate(PlayerChunkHandle playerChunk) {
        if (T.markForUpdate_1_10_2.isAvailable()) {
            T.markForUpdate_1_10_2.invoke(getRaw(), playerChunk);
        } else {
            T.updateQueue_1_8_8.get(getRaw()).add(playerChunk);
        }
    }
    public abstract List<Player> getManagedPlayers();
    public abstract void setManagedPlayers(List<Player> value);
    public abstract int getRadius();
    public abstract void setRadius(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PlayerChunkMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerChunkMapClass extends Template.Class<PlayerChunkMapHandle> {
        public final Template.Field.Converted<List<Player>> managedPlayers = new Template.Field.Converted<List<Player>>();
        @Template.Optional
        public final Template.Field.Converted<Queue<PlayerChunkHandle>> updateQueue_1_8_8 = new Template.Field.Converted<Queue<PlayerChunkHandle>>();
        public final Template.Field.Integer radius = new Template.Field.Integer();

        @Template.Optional
        public final Template.Method.Converted<Void> markForUpdate_1_10_2 = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> shouldUnload = new Template.Method<Boolean>();
        public final Template.Method.Converted<PlayerChunkHandle> getChunk = new Template.Method.Converted<PlayerChunkHandle>();
        public final Template.Method.Converted<Void> flagDirty = new Template.Method.Converted<Void>();
        public final Template.Method<Void> flagPosDirty = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Void> trackEntity = new Template.Method.Converted<Void>();

    }

}

