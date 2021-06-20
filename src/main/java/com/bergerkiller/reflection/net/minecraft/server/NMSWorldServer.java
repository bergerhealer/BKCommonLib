package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.level.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;

@Deprecated
public class NMSWorldServer extends NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.create(WorldServerHandle.T.getType());

    public static final FieldAccessor<Object> chunkProviderServer = new SafeDirectField<Object>() {
        @Override
        public Object get(Object instance) {
            return Handle.getRaw(WorldServerHandle.createHandle(instance).getChunkProviderServer());
        }

        @Override
        public boolean set(Object instance, Object value) {
            WorldServerHandle.createHandle(instance).setChunkProviderServer(ChunkProviderServerHandle.createHandle(value));
            return true;
        }
    };
}
