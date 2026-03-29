package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.SmeltingRecipe</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.item.crafting.SmeltingRecipe")
public abstract class SmeltingRecipeHandle extends RecipeHandle {
    /** @see SmeltingRecipeClass */
    public static final SmeltingRecipeClass T = Template.Class.create(SmeltingRecipeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SmeltingRecipeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<SmeltingRecipeHandle> getRecipes() {
        return T.getRecipes.invoke();
    }

    public abstract CraftInputSlot getIngredient();
    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.SmeltingRecipe</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SmeltingRecipeClass extends Template.Class<SmeltingRecipeHandle> {
        public final Template.StaticMethod.Converted<Iterable<SmeltingRecipeHandle>> getRecipes = new Template.StaticMethod.Converted<Iterable<SmeltingRecipeHandle>>();

        public final Template.Method.Converted<CraftInputSlot> getIngredient = new Template.Method.Converted<CraftInputSlot>();

    }

}

