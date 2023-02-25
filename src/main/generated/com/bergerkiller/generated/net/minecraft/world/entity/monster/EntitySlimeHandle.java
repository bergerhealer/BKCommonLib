package com.bergerkiller.generated.net.minecraft.world.entity.monster;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityInsentientHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.monster.EntitySlime</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.monster.EntitySlime")
public abstract class EntitySlimeHandle extends EntityInsentientHandle {
    /** @See {@link EntitySlimeClass} */
    public static final EntitySlimeClass T = Template.Class.create(EntitySlimeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntitySlimeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<Integer> DATA_SIZE = Key.Type.SLIME_SIZE_TYPE.createKey(T.DATA_SIZE, 16);
    /**
     * Stores class members for <b>net.minecraft.world.entity.monster.EntitySlime</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntitySlimeClass extends Template.Class<EntitySlimeHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_SIZE = new Template.StaticField.Converted<Key<Integer>>();

    }

}

