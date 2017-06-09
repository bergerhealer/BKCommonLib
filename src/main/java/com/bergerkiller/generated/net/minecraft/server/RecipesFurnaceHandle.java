package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RecipesFurnace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class RecipesFurnaceHandle extends Template.Handle {
    /** @See {@link RecipesFurnaceClass} */
    public static final RecipesFurnaceClass T = new RecipesFurnaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RecipesFurnaceHandle.class, "net.minecraft.server.RecipesFurnace");

    /* ============================================================================== */

    public static RecipesFurnaceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RecipesFurnaceHandle handle = new RecipesFurnaceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static RecipesFurnaceHandle getInstance() {
        return T.getInstance.invokeVA();
    }

    public ItemStackHandle getResult(ItemStackHandle itemstack) {
        return T.getResult.invoke(instance, itemstack);
    }

    public Map<ItemStack, ItemStack> getRecipes() {
        return T.recipes.get(instance);
    }

    public void setRecipes(Map<ItemStack, ItemStack> value) {
        T.recipes.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.RecipesFurnace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RecipesFurnaceClass extends Template.Class<RecipesFurnaceHandle> {
        public final Template.Field.Converted<Map<ItemStack, ItemStack>> recipes = new Template.Field.Converted<Map<ItemStack, ItemStack>>();

        public final Template.StaticMethod.Converted<RecipesFurnaceHandle> getInstance = new Template.StaticMethod.Converted<RecipesFurnaceHandle>();

        public final Template.Method.Converted<ItemStackHandle> getResult = new Template.Method.Converted<ItemStackHandle>();

    }

}

