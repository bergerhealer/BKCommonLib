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
public class PlayerChunkMapHandle extends Template.Handle {
    /** @See {@link PlayerChunkMapClass} */
    public static final PlayerChunkMapClass T = new PlayerChunkMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerChunkMapHandle.class, "net.minecraft.server.PlayerChunkMap");

    /* ============================================================================== */

    public static PlayerChunkMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public boolean shouldUnload(int i, int j, int k, int l, int i1) {
        return T.shouldUnload.invoke(getRaw(), i, j, k, l, i1);
    }

    public void flagDirty(IntVector3 blockposition) {
        T.flagDirty.invoke(getRaw(), blockposition);
    }

    public boolean isChunkEntered(EntityPlayerHandle entityplayer, int chunkX, int chunkZ) {
        return T.isChunkEntered.invoke(getRaw(), entityplayer, chunkX, chunkZ);
    }


    public void markForUpdate(PlayerChunkHandle playerChunk) {
        if (T.markForUpdate_1_10_2.isAvailable()) {
            T.markForUpdate_1_10_2.invoke(getRaw(), playerChunk);
        } else {
            T.updateQueue_1_8_8.get(getRaw()).add(playerChunk);
        }
    }


    public PlayerChunkHandle getChunk(int x, int z) {
        if (T.getChunk_1_9.isAvailable()) {
            return T.getChunk_1_9.invoke(getRaw(), x, z);
        } else {
            return T.getChunk_1_8_8.invoke(getRaw(), x, z, false);
        }
    }


    public void flagDirty(int x, int y, int z) {
        T.flagDirty.raw.invoke(getRaw(), BlockPositionHandle.T.constr_x_y_z.newInstance(x, y, z));
    }
    public List<Player> getManagedPlayers() {
        return T.managedPlayers.get(getRaw());
    }

    public void setManagedPlayers(List<Player> value) {
        T.managedPlayers.set(getRaw(), value);
    }

    public int getRadius() {
        return T.radius.getInteger(getRaw());
    }

    public void setRadius(int value) {
        T.radius.setInteger(getRaw(), value);
    }

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
        @Template.Optional
        public final Template.Method.Converted<PlayerChunkHandle> getChunk_1_8_8 = new Template.Method.Converted<PlayerChunkHandle>();
        @Template.Optional
        public final Template.Method.Converted<PlayerChunkHandle> getChunk_1_9 = new Template.Method.Converted<PlayerChunkHandle>();
        public final Template.Method.Converted<Void> flagDirty = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();

    }

}

