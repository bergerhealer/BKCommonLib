package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeDirectField;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

public class WorldServerRef extends WorldRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("WorldServer");
    public static final FieldAccessor<Object> playerChunkMap = TEMPLATE.getField("manager");
    public static final FieldAccessor<List<Object>> accessList = TEMPLATE.getField("u");
    public static final FieldAccessor<EntityTracker> entityTracker = TEMPLATE.getField("tracker").translate(ConversionPairs.entityTracker);
    /**
     * Type: IntHashMap
     */
    public static final TranslatorFieldAccessor<IntHashMap<Object>> entitiesById = TEMPLATE.getField("entitiesById").translate(ConversionPairs.intHashMap);
    public static final FieldAccessor<Object> chunkProviderServer = new SafeDirectField<Object>() {
        private final FieldAccessor<Object> field1 = TEMPLATE.getField("chunkProviderServer");
        private final FieldAccessor<Object> field2 = TEMPLATE.getField("chunkProvider");

        @Override
        public Object get(Object instance) {
            return field1.get(instance);
        }

        @Override
        public boolean set(Object instance, Object value) {
            return field1.set(instance, value) && field2.set(instance, value);
        }
    };
}
