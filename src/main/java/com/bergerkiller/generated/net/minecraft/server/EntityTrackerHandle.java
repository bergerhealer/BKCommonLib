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
public abstract class EntityTrackerHandle extends Template.Handle {
    /** @See {@link EntityTrackerClass} */
    public static final EntityTrackerClass T = new EntityTrackerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTrackerHandle.class, "net.minecraft.server.EntityTracker");

    /* ============================================================================== */

    public static EntityTrackerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void sendPacketToEntity(Entity entity, CommonPacket packet);
    public abstract World getWorld();
    public abstract void setWorld(World value);
    public abstract Set<EntityTrackerEntryHandle> getEntries();
    public abstract void setEntries(Set<EntityTrackerEntryHandle> value);
    public abstract IntHashMap<Object> getTrackedEntities();
    public abstract void setTrackedEntities(IntHashMap<Object> value);
    public abstract int getViewDistance();
    public abstract void setViewDistance(int value);
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

