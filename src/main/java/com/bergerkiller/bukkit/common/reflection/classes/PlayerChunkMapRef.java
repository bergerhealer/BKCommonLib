package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.*;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import net.minecraft.server.v1_8_R3.BlockPosition;

import java.util.List;
import java.util.Queue;

public class PlayerChunkMapRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerChunkMap");
    public static final FieldAccessor<List<?>> managedPlayers = TEMPLATE.getField("managedPlayers");
<<<<<<< HEAD
    public static final FieldAccessor<Integer> radius = TEMPLATE.getField("j");
    public static final TranslatorFieldAccessor<LongHashMap<Object>> playerInstances = TEMPLATE.getField("e").translate(ConversionPairs.longHashMap);
=======
    public static final FieldAccessor<Integer> radius = TEMPLATE.getField("g");
    public static final TranslatorFieldAccessor<LongHashMap<Object>> playerInstances = TEMPLATE.getField("d").translate(ConversionPairs.longHashMap);
>>>>>>> 6c6809c31fa3f2895f50a974cd9b182317b26eb3
    public static final FieldAccessor<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("f");
    public static final MethodAccessor<Boolean> shouldUnload = TEMPLATE.getMethod("a", int.class, int.class, int.class, int.class, int.class);
    public static final MethodAccessor<Object> getChunk = TEMPLATE.getMethod("a", int.class, int.class, boolean.class);
    private static final MethodAccessor<Void> flagDirty = TEMPLATE.getMethod("flagDirty", BlockPosition.class);

    public static void flagBlockDirty(Object playerChunkMap, int x, int y, int z) {
        flagDirty.invoke(playerChunkMap, new BlockPosition(x, y, z));
    }

    public static Object getPlayerChunk(Object playerChunkMap, int x, int z) {
        return LongHashMapRef.get.invoke(playerInstances.getInternal(playerChunkMap), x, z);
    }
}
