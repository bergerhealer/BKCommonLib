package com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.monster.cubemob.AbstractCubeMob</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.monster.cubemob.AbstractCubeMob")
public abstract class AbstractCubeMobHandle extends MobHandle {
    /** @see AbstractCubeMobClass */
    public static final AbstractCubeMobClass T = Template.Class.create(AbstractCubeMobClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AbstractCubeMobHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<Integer> DATA_SIZE = Key.Type.SLIME_SIZE_TYPE.createKey(T.DATA_SIZE, 16);
    /**
     * Stores class members for <b>net.minecraft.world.entity.monster.cubemob.AbstractCubeMob</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AbstractCubeMobClass extends Template.Class<AbstractCubeMobHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_SIZE = new Template.StaticField.Converted<Key<Integer>>();

    }

}

