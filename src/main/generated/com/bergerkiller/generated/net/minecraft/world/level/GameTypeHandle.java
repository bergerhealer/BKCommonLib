package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.GameType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.GameType")
public abstract class GameTypeHandle extends Template.Handle {
    /** @see GameTypeClass */
    public static final GameTypeClass T = Template.Class.create(GameTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static GameTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static GameTypeHandle getById(int id) {
        return T.getById.invoke(id);
    }

    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.GameType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class GameTypeClass extends Template.Class<GameTypeHandle> {
        public final Template.StaticMethod.Converted<GameTypeHandle> getById = new Template.StaticMethod.Converted<GameTypeHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

