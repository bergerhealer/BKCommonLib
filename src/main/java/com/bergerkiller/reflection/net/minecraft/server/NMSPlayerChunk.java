package com.bergerkiller.reflection.net.minecraft.server;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

/**
 * <b>Deprecated: </b>Use {@link PlayerChunkHandle} instead
 */
@Deprecated
public class NMSPlayerChunk {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerChunk");

    public static final FieldAccessor<Object> playerChunkMap = PlayerChunkHandle.T.playerChunkMap.raw.toFieldAccessor();
    public static final FieldAccessor<List<Player>> players = PlayerChunkHandle.T.players.toFieldAccessor();
    public static final FieldAccessor<IntVector2> location = PlayerChunkHandle.T.location.toFieldAccessor();
    public static final TranslatorFieldAccessor<Chunk> chunk = PlayerChunkHandle.T.chunk.toFieldAccessor();
    public static final FieldAccessor<Integer> dirtyCount = PlayerChunkHandle.T.dirtyCount.toFieldAccessor();
    public static final FieldAccessor<Integer> dirtySectionMask = PlayerChunkHandle.T.dirtySectionMask.toFieldAccessor();
    public static final FieldAccessor<Boolean> done = PlayerChunkHandle.T.done.toFieldAccessor();
    public static final FieldAccessor<Boolean> loaded = done; //TODO: Maybe private boolean loadInProgress?

    public static final MethodAccessor<Void> addPlayer = PlayerChunkHandle.T.addPlayer.raw.toMethodAccessor();
    public static final MethodAccessor<Void> removePlayer = PlayerChunkHandle.T.removePlayer.raw.toMethodAccessor();
    public static final MethodAccessor<Void> sendChunk = PlayerChunkHandle.T.sendChunk.raw.toMethodAccessor();
}
