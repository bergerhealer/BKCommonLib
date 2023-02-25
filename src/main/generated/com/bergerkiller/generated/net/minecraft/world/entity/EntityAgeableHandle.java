package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.EntityAgeable</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.EntityAgeable")
public abstract class EntityAgeableHandle extends EntityInsentientHandle {
    /** @see EntityAgeableClass */
    public static final EntityAgeableClass T = Template.Class.create(EntityAgeableClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityAgeableHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<Boolean> DATA_IS_BABY = Key.Type.BOOLEAN.createKey(T.DATA_IS_BABY, 12);
    /**
     * Stores class members for <b>net.minecraft.world.entity.EntityAgeable</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityAgeableClass extends Template.Class<EntityAgeableHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_IS_BABY = new Template.StaticField.Converted<Key<Boolean>>();

    }

}

