package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.FurnaceRecipe</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.item.crafting.FurnaceRecipe")
public abstract class FurnaceRecipeHandle extends IRecipeHandle {
    /** @see FurnaceRecipeClass */
    public static final FurnaceRecipeClass T = Template.Class.create(FurnaceRecipeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static FurnaceRecipeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<FurnaceRecipeHandle> getRecipes() {
        return T.getRecipes.invoke();
    }

    public abstract CraftInputSlot getIngredient();
    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.FurnaceRecipe</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FurnaceRecipeClass extends Template.Class<FurnaceRecipeHandle> {
        public final Template.StaticMethod.Converted<Iterable<FurnaceRecipeHandle>> getRecipes = new Template.StaticMethod.Converted<Iterable<FurnaceRecipeHandle>>();

        public final Template.Method.Converted<CraftInputSlot> getIngredient = new Template.Method.Converted<CraftInputSlot>();

    }

}

