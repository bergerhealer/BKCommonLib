package com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.monster.cubemob.Slime</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.monster.cubemob.Slime")
public abstract class SlimeHandle extends AbstractCubeMobHandle {
    /** @see SlimeClass */
    public static final SlimeClass T = Template.Class.create(SlimeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SlimeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.monster.cubemob.Slime</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SlimeClass extends Template.Class<SlimeHandle> {
    }

}

