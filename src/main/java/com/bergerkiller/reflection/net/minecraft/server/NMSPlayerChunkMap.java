package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import net.minecraft.server.v1_11_R1.BlockPosition;

import java.util.List;
import java.util.Set;

public class NMSPlayerChunkMap {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerChunkMap")
    		.addImport("it.unimi.dsi.fastutil.longs.Long2ObjectMap");
    
    public static final FieldAccessor<List<?>> managedPlayers = T.nextField("private final List<EntityPlayer> managedPlayers");
    public static final TranslatorFieldAccessor<LongHashMap<Object>> playerInstances = 
    		T.nextFieldSignature("private final Long2ObjectMap<PlayerChunk> e").translate(DuplexConversion.longHashMap);
    
    public static final FieldAccessor<Set<?>> dirtyBlockChunks = T.nextField("private final Set<PlayerChunk> f");
    
    static {
    	  T.skipFieldSignature("private final List<PlayerChunk> g");
    	  T.skipFieldSignature("private final List<PlayerChunk> h");
    	  T.skipFieldSignature("private final List<PlayerChunk> i");
    }
    
    public static final FieldAccessor<Integer> radius = T.nextFieldSignature("private int j");

    public static final MethodAccessor<Void> markForUpdate = T.selectMethod("public void a(PlayerChunk playerchunk)");
    public static final MethodAccessor<Boolean> shouldUnload = T.selectMethod("private boolean a(int i, int j, int k, int l, int i1)");
    public static final MethodAccessor<Object> getChunk = T.selectMethod("public PlayerChunk getChunk(int i, int j)");
    private static final MethodAccessor<Void> flagDirty = T.selectMethod("public void flagDirty(BlockPosition blockposition)");

    public static void flagBlockDirty(Object playerChunkMap, int x, int y, int z) {
        flagDirty.invoke(playerChunkMap, new BlockPosition(x, y, z));
    }

    public static Object getPlayerChunk(Object playerChunkMap, int x, int z) {
    	return playerInstances.get(playerChunkMap).get(x, z);
    }
}
