package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

import java.util.Map;
import java.util.UUID;

public class NMSWorldServer extends NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("WorldServer");
    
    public static final FieldAccessor<Object> playerChunkMap = T.nextField("private final PlayerChunkMap manager");
    public static final FieldAccessor<Object> server = T.nextField("private final MinecraftServer server");
    public static final FieldAccessor<EntityTracker> entityTracker = T.nextField("public EntityTracker tracker").translate(DuplexConversion.entityTracker);
    
    static {
    	T.nextField("protected final VillageSiege siegeManager");
    	T.nextFieldSignature("private final WorldServer$BlockActionDataList[] S");
    	T.nextFieldSignature("private int T");
    }

    public static final FieldAccessor<Map<UUID, Object>> entitiesByUUID = T.nextField("private final Map<UUID, Entity> entitiesByUUID");

    public static final FieldAccessor<Object> chunkProviderServer = new SafeDirectField<Object>() {
        private final FieldAccessor<Object> field1 = T.getField("chunkProvider");
//		private final FieldAccessor<Object> field2 = TEMPLATE.getField("generator");
		 @Override
	        public Object get(Object instance) {
	            return field1.get(instance);
	        }

	        @Override
	        public boolean set(Object instance, Object value) {
	            return field1.set(instance, value);// && field2.set(instance, value);
	        }

    };
}
