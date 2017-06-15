package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ShapedRecipes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ShapedRecipesHandle extends IRecipeHandle {
    /** @See {@link ShapedRecipesClass} */
    public static final ShapedRecipesClass T = new ShapedRecipesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ShapedRecipesHandle.class, "net.minecraft.server.ShapedRecipes");

    /* ============================================================================== */

    public static ShapedRecipesHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ShapedRecipesHandle handle = new ShapedRecipesHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public List<CraftInputSlot> getInputItems() {
        return T.inputItems.get(instance);
    }

    public void setInputItems(List<CraftInputSlot> value) {
        T.inputItems.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.ShapedRecipes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ShapedRecipesClass extends Template.Class<ShapedRecipesHandle> {
        public final Template.Field.Converted<List<CraftInputSlot>> inputItems = new Template.Field.Converted<List<CraftInputSlot>>();

    }

}

