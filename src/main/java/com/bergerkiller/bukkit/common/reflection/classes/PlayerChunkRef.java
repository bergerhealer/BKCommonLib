package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerChunkMap;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerChunkRef {

    public static ClassTemplate<?> TEMPLATE = null;
    public static FieldAccessor<IntVector2> location = null;
    public static FieldAccessor<List<Player>> players = null;
    public static MethodAccessor<Void> unload = null;
    public static MethodAccessor<Void> load = null;
    public static FieldAccessor<Boolean> loaded = null;

    static {
        Class[] possible = PlayerChunkMap.class.getDeclaredClasses();
        Class qp = null;
        for (Class p : possible) {
            if (p.getName().endsWith("PlayerChunk")) {
                qp = p;
            }
        }
        TEMPLATE = ClassTemplate.create(qp);
        location = TEMPLATE.getField("location").translate(ConversionPairs.chunkIntPair);
        players = TEMPLATE.getField("b").translate(ConversionPairs.playerList);
        unload = TEMPLATE.getMethod("b", EntityPlayer.class);
        load = TEMPLATE.getMethod("a", EntityPlayer.class);
        loaded = TEMPLATE.getField("loaded");
    }
}
