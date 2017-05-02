package com.bergerkiller.reflection.net.minecraft.server;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.conversion2.DuplexConversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

public class NMSPlayerChunk {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerChunk");

    public static final FieldAccessor<Object> playerChunkMap = T.nextField("private final PlayerChunkMap playerChunkMap");
    public static final FieldAccessor<List<Player>> players = T.nextFieldSignature("public final List<EntityPlayer> c").translate(DuplexConversion.playerList);
    public static final FieldAccessor<IntVector2> location = T.nextField("private final ChunkCoordIntPair location").translate(DuplexConversion.chunkIntPair);
    public static final TranslatorFieldAccessor<Chunk> chunk = T.nextField("public Chunk chunk").translate(DuplexConversion.chunk);
    public static final FieldAccessor<Integer> dirtyCount = T.nextField("private int dirtyCount");
    public static final FieldAccessor<Integer> dirtySectionMask = T.nextFieldSignature("private int h");
    public static final FieldAccessor<Boolean> done = T.nextField("private boolean done");
    public static final FieldAccessor<Boolean> loaded = done; //TODO: Maybe private boolean loadInProgress?

    public static final MethodAccessor<Void> addPlayer = T.selectMethod("public void a(EntityPlayer entityplayer)");
    public static final MethodAccessor<Void> removePlayer = T.selectMethod("public void b(EntityPlayer entityplayer)");
    public static final MethodAccessor<Void> sendChunk = T.selectMethod("public void sendChunk(EntityPlayer entityplayer)");
}
