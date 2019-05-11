package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public abstract class EntityAddRemoveHandler {
    public static final EntityAddRemoveHandler INSTANCE;

    static {
        if (Common.evaluateMCVersion(">=", "1.14")) {
            INSTANCE = new EntityAddRemoveHandler_1_14();
        } else {
            INSTANCE = new EntityAddRemoveHandler_1_8_to_1_13_2();
        }
    }

    public abstract void hook(World world);

    public abstract void unhook(World world);

    /**
     * This should cover the full replacement of an entity in all internal mappings.
     * This includes the chunk, world and network synchronization objects.
     * 
     * @param oldInstance to replace
     * @param newInstance to replace with
     */
    public abstract void replace(World world, EntityHandle oldEntity, EntityHandle newEntity);
}
