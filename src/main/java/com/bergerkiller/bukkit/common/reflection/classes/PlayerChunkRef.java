package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import net.minecraft.server.v1_8_R1.EntityPlayer;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class PlayerChunkRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerChunk");
    public static final FieldAccessor<IntVector2> location = TEMPLATE.getField("location").translate(ConversionPairs.chunkIntPair);
    public static final FieldAccessor<List<Player>> players = TEMPLATE.getField("b").translate(ConversionPairs.playerList);
    public static final MethodAccessor<Void> unload = TEMPLATE.getMethod("b", EntityPlayer.class);
    public static final MethodAccessor<Void> load = TEMPLATE.getMethod("a", EntityPlayer.class);
    public static final FieldAccessor<Boolean> loaded = TEMPLATE.getField("loaded");
}
