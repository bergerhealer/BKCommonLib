package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.RecipeManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.crafting.RecipeManager")
public abstract class RecipeManagerHandle extends Template.Handle {
    /** @see RecipeManagerClass */
    public static final RecipeManagerClass T = Template.Class.create(RecipeManagerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RecipeManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<RecipeHandle> getRecipes() {
        return T.getRecipes.invoke();
    }

    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.RecipeManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RecipeManagerClass extends Template.Class<RecipeManagerHandle> {
        public final Template.StaticMethod.Converted<Iterable<RecipeHandle>> getRecipes = new Template.StaticMethod.Converted<Iterable<RecipeHandle>>();

    }

}

