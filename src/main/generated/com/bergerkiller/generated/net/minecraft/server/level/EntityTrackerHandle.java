package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.EntityTracker</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.EntityTracker")
public abstract class EntityTrackerHandle extends Template.Handle {
    /** @see EntityTrackerClass */
    public static final EntityTrackerClass T = Template.Class.create(EntityTrackerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityTrackerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Collection<EntityTrackerEntryHandle> getEntries();
    public abstract EntityTrackerEntryHandle getEntry(int entityId);
    public abstract EntityTrackerEntryHandle putEntry(int entityId, EntityTrackerEntryHandle entry);
    public abstract void sendPacketToEntity(Entity entity, CommonPacket packet);
    public abstract void trackEntity(Entity entity);
    public abstract void untrackEntity(Entity entity);
    public abstract World getWorld();
    public abstract void setWorld(World value);
    public abstract int getTrackingDistance();
    public abstract void setTrackingDistance(int value);
    /**
     * Stores class members for <b>net.minecraft.server.level.EntityTracker</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTrackerClass extends Template.Class<EntityTrackerHandle> {
        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();
        public final Template.Field.Integer trackingDistance = new Template.Field.Integer();

        public final Template.Method.Converted<Collection<EntityTrackerEntryHandle>> getEntries = new Template.Method.Converted<Collection<EntityTrackerEntryHandle>>();
        public final Template.Method.Converted<EntityTrackerEntryHandle> getEntry = new Template.Method.Converted<EntityTrackerEntryHandle>();
        public final Template.Method.Converted<EntityTrackerEntryHandle> putEntry = new Template.Method.Converted<EntityTrackerEntryHandle>();
        @Template.Optional
        public final Template.Method<Void> setVisibleChunksToUpdatingChunks = new Template.Method<Void>();
        public final Template.Method.Converted<Void> sendPacketToEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> trackEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> untrackEntity = new Template.Method.Converted<Void>();

    }

}

