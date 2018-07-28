package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.FurnaceRecipe</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class FurnaceRecipeHandle extends IRecipeHandle {
    /** @See {@link FurnaceRecipeClass} */
    public static final FurnaceRecipeClass T = new FurnaceRecipeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(FurnaceRecipeHandle.class, "net.minecraft.server.FurnaceRecipe");

    /* ============================================================================== */

    public static FurnaceRecipeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<FurnaceRecipeHandle> getRecipes() {
        return T.getRecipes.invoke();
    }

    public abstract CraftInputSlot getIngredient();
    public abstract void setIngredient(CraftInputSlot value);
    /**
     * Stores class members for <b>net.minecraft.server.FurnaceRecipe</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FurnaceRecipeClass extends Template.Class<FurnaceRecipeHandle> {
        public final Template.Field.Converted<CraftInputSlot> ingredient = new Template.Field.Converted<CraftInputSlot>();

        public final Template.StaticMethod.Converted<Iterable<FurnaceRecipeHandle>> getRecipes = new Template.StaticMethod.Converted<Iterable<FurnaceRecipeHandle>>();

    }

}

