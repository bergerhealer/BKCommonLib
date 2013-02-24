package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Queue;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;

public class PlayerChunkMapRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerChunkMap");
	public static final TranslatorFieldAccessor<LongHashMap<Object>> playerInstances = TEMPLATE.getField("c").translate(ConversionPairs.longHashMap);
	public static final FieldAccessor<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("d");
}
