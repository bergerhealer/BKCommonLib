package com.bergerkiller.generated.org.bukkit.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Instance wrapper handle for type <b>org.bukkit.entity.Entity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.entity.Entity")
public abstract class EntityHandle extends Template.Handle {
    /** @see EntityClass */
    public static final EntityClass T = Template.Class.create(EntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<Entity> getPassengers();
    public abstract boolean addPassenger(Entity passenger);
    public abstract boolean removePassenger(Entity passenger);
    public abstract boolean isSeenBy(Player player);
    /**
     * Stores class members for <b>org.bukkit.entity.Entity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityClass extends Template.Class<EntityHandle> {
        public final Template.Method<List<Entity>> getPassengers = new Template.Method<List<Entity>>();
        public final Template.Method<Boolean> addPassenger = new Template.Method<Boolean>();
        public final Template.Method<Boolean> removePassenger = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isSeenBy = new Template.Method<Boolean>();

    }

}

