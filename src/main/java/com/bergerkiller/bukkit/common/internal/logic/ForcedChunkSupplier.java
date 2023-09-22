package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.generated.net.minecraft.world.level.ForcedChunkHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;

/**
 * Creates new ForcedChunk instances. Required for glue code in NMS.
 * Not used anymore after 1.20.2 when a factory() method was added that we can use.
 */
public class ForcedChunkSupplier implements Supplier<Object>, Function<String, Object> {
    public static final ForcedChunkSupplier INSTANCE = new ForcedChunkSupplier();
    private final Constructor<?> constructor;

    public ForcedChunkSupplier() {
        Constructor<?> constructor = null;
        Class<?> type = ForcedChunkHandle.T.getType();
        if (type != null) {
            try {
                try {
                    constructor = type.getConstructor(String.class);
                } catch (Throwable t) {
                    constructor = type.getConstructor();
                }
            } catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error finding ForcedChunkSupplier constructor", t);
            }
        }
        this.constructor = constructor;
    }

    @Override
    public Object get() {
        // Used on 1.14 and later: Empty constructor
        try {
            return this.constructor.newInstance();
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    @Override
    public Object apply(String name) {
        // Used on 1.13.1 and 1.13.2: String parameter
        try {
            return this.constructor.newInstance(name);
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
}
