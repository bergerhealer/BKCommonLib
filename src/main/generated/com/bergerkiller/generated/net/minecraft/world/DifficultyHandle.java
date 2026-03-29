package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.Difficulty</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.Difficulty")
public abstract class DifficultyHandle extends Template.Handle {
    /** @see DifficultyClass */
    public static final DifficultyClass T = Template.Class.create(DifficultyClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DifficultyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static DifficultyHandle getById(int id) {
        return T.getById.invoke(id);
    }

    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.Difficulty</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DifficultyClass extends Template.Class<DifficultyHandle> {
        public final Template.StaticMethod.Converted<DifficultyHandle> getById = new Template.StaticMethod.Converted<DifficultyHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

