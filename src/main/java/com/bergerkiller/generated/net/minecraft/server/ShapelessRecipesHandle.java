package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ShapelessRecipes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ShapelessRecipesHandle extends IRecipeHandle {
    /** @See {@link ShapelessRecipesClass} */
    public static final ShapelessRecipesClass T = new ShapelessRecipesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ShapelessRecipesHandle.class, "net.minecraft.server.ShapelessRecipes");

    /* ============================================================================== */

    public static ShapelessRecipesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<CraftInputSlot> getInputItems();
    public abstract void setInputItems(List<CraftInputSlot> value);
    /**
     * Stores class members for <b>net.minecraft.server.ShapelessRecipes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ShapelessRecipesClass extends Template.Class<ShapelessRecipesHandle> {
        public final Template.Field.Converted<List<CraftInputSlot>> inputItems = new Template.Field.Converted<List<CraftInputSlot>>();

    }

}

