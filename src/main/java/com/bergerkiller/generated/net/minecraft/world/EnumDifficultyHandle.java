package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.EnumDifficulty</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.EnumDifficulty")
public abstract class EnumDifficultyHandle extends Template.Handle {
    /** @See {@link EnumDifficultyClass} */
    public static final EnumDifficultyClass T = Template.Class.create(EnumDifficultyClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EnumDifficultyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static EnumDifficultyHandle getById(int id) {
        return T.getById.invoke(id);
    }

    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.EnumDifficulty</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumDifficultyClass extends Template.Class<EnumDifficultyHandle> {
        public final Template.StaticMethod.Converted<EnumDifficultyHandle> getById = new Template.StaticMethod.Converted<EnumDifficultyHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

