package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.*;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

import java.util.List;

public class WorldServerRef extends WorldRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("WorldServer");
    public static final FieldAccessor<Object> playerChunkMap = TEMPLATE.getField("manager");
    public static final FieldAccessor<List<Object>> accessList = TEMPLATE.getField("u");
    public static final FieldAccessor<EntityTracker> entityTracker = TEMPLATE.getField("tracker").translate(ConversionPairs.entityTracker);
    /**
     * Type: IntHashMap
     */
    public static final TranslatorFieldAccessor<IntHashMap<Object>> entitiesById = TEMPLATE.getField("entitiesById").translate(ConversionPairs.intHashMap);
    //public static final FieldAccessor<Object> chunkProviderServer = new SafeDirectField<Object>() {
        private final MethodAccessor<Object> field1 = TEMPLATE.getMethod("chunkProviderServer");
//		private final FieldAccessor<Object> field2 = TEMPLATE.getField("chunkProvider");

   
}
