package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.CraftingManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftingManagerHandle extends Template.Handle {
    /** @See {@link CraftingManagerClass} */
    public static final CraftingManagerClass T = new CraftingManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftingManagerHandle.class, "net.minecraft.server.CraftingManager");

    /* ============================================================================== */

    public static CraftingManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<IRecipeHandle> getRecipes() {
        return T.getRecipes.invoke();
    }

    /**
     * Stores class members for <b>net.minecraft.server.CraftingManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftingManagerClass extends Template.Class<CraftingManagerHandle> {
        public final Template.StaticMethod.Converted<Iterable<IRecipeHandle>> getRecipes = new Template.StaticMethod.Converted<Iterable<IRecipeHandle>>();

    }

}

