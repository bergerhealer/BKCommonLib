package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;
import java.util.Queue;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;

public class PlayerChunkMapRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerChunkMap");
    public static final FieldAccessor<List<?>> managedPlayers = TEMPLATE.getField("managedPlayers");
    public static final FieldAccessor<Integer> radius = TEMPLATE.getField("g");
    public static final TranslatorFieldAccessor<LongHashMap<Object>> playerInstances = TEMPLATE.getField("d").translate(ConversionPairs.longHashMap);
    public static final FieldAccessor<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("f");
    public static final MethodAccessor<Boolean> shouldUnload = TEMPLATE.getMethod("a", int.class, int.class, int.class, int.class, int.class);
    public static final MethodAccessor<Object> getChunk = TEMPLATE.getMethod("a", int.class, int.class, boolean.class);
    private static final MethodAccessor<Void> flagDirty = TEMPLATE.getMethod("flagDirty", int.class, int.class, int.class);

    public static void flagBlockDirty(Object playerChunkMap, int x, int y, int z) {
        flagDirty.invoke(playerChunkMap, x, y, z);
    }

    public static Object getPlayerChunk(Object playerChunkMap, int x, int z) {
        return LongHashMapRef.get.invoke(playerInstances.getInternal(playerChunkMap), x, z);
    }
}
