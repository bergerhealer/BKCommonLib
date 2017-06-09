package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ShapelessRecipes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ShapelessRecipesHandle extends IRecipeHandle {
    /** @See {@link ShapelessRecipesClass} */
    public static final ShapelessRecipesClass T = new ShapelessRecipesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ShapelessRecipesHandle.class, "net.minecraft.server.ShapelessRecipes");

    /* ============================================================================== */

    public static ShapelessRecipesHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ShapelessRecipesHandle handle = new ShapelessRecipesHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public List<ItemStack> getInputItems() {
        return T.inputItems.get(instance);
    }

    public void setInputItems(List<ItemStack> value) {
        T.inputItems.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.ShapelessRecipes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ShapelessRecipesClass extends Template.Class<ShapelessRecipesHandle> {
        public final Template.Field.Converted<List<ItemStack>> inputItems = new Template.Field.Converted<List<ItemStack>>();

    }

}

