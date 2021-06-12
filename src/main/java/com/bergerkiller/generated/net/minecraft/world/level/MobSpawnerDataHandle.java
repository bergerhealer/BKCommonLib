package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.MobSpawnerData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.MobSpawnerData")
public abstract class MobSpawnerDataHandle extends Template.Handle {
    /** @See {@link MobSpawnerDataClass} */
    public static final MobSpawnerDataClass T = Template.Class.create(MobSpawnerDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobSpawnerDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.MobSpawnerData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobSpawnerDataClass extends Template.Class<MobSpawnerDataHandle> {
    }

}

