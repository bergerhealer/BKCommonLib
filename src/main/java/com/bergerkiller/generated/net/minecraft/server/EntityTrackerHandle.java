package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityTracker</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityTrackerHandle extends Template.Handle {
    /** @See {@link EntityTrackerClass} */
    public static final EntityTrackerClass T = new EntityTrackerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTrackerHandle.class, "net.minecraft.server.EntityTracker");

    /* ============================================================================== */

    public static EntityTrackerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityTrackerHandle handle = new EntityTrackerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void sendPacketToEntity(Entity entity, CommonPacket packet) {
        T.sendPacketToEntity.invoke(instance, entity, packet);
    }

    public World getWorld() {
        return T.world.get(instance);
    }

    public void setWorld(World value) {
        T.world.set(instance, value);
    }

    public Set<EntityTrackerEntryHandle> getEntries() {
        return T.entries.get(instance);
    }

    public void setEntries(Set<EntityTrackerEntryHandle> value) {
        T.entries.set(instance, value);
    }

    public IntHashMap<Object> getTrackedEntities() {
        return T.trackedEntities.get(instance);
    }

    public void setTrackedEntities(IntHashMap<Object> value) {
        T.trackedEntities.set(instance, value);
    }

    public int getViewDistance() {
        return T.viewDistance.getInteger(instance);
    }

    public void setViewDistance(int value) {
        T.viewDistance.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityTracker</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTrackerClass extends Template.Class<EntityTrackerHandle> {
        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();
        public final Template.Field.Converted<Set<EntityTrackerEntryHandle>> entries = new Template.Field.Converted<Set<EntityTrackerEntryHandle>>();
        public final Template.Field.Converted<IntHashMap<Object>> trackedEntities = new Template.Field.Converted<IntHashMap<Object>>();
        public final Template.Field.Integer viewDistance = new Template.Field.Integer();

        public final Template.Method.Converted<Void> sendPacketToEntity = new Template.Method.Converted<Void>();

    }

}

