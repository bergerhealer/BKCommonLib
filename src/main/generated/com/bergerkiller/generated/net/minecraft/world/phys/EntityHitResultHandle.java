package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.EntityHitResult</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.EntityHitResult")
public abstract class EntityHitResultHandle extends HitResultHandle {
    /** @see EntityHitResultClass */
    public static final EntityHitResultClass T = Template.Class.create(EntityHitResultClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityHitResultHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static EntityHitResultHandle createNew(Entity entity) {
        return T.createNew.invoke(entity);
    }

    public static EntityHitResultHandle createNewAt(Entity entity, Vector location) {
        return T.createNewAt.invoke(entity, location);
    }

    public abstract Entity getEntity();
    public EntityHitResultHandle withLocation(org.bukkit.util.Vector location) {
        return createNewAt(getEntity(), location);
    }
    /**
     * Stores class members for <b>net.minecraft.world.phys.EntityHitResult</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityHitResultClass extends Template.Class<EntityHitResultHandle> {
        public final Template.StaticMethod.Converted<EntityHitResultHandle> createNew = new Template.StaticMethod.Converted<EntityHitResultHandle>();
        public final Template.StaticMethod.Converted<EntityHitResultHandle> createNewAt = new Template.StaticMethod.Converted<EntityHitResultHandle>();

        public final Template.Method.Converted<Entity> getEntity = new Template.Method.Converted<Entity>();

    }

}

