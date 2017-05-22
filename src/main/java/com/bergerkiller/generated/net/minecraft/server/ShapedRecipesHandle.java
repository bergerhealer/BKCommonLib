package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.ItemStack;

public class ShapedRecipesHandle extends IRecipeHandle {
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

    public List<ItemStack> getInputItems() {
        return T.inputItems.get(instance);
    }

    public void setInputItems(List<ItemStack> value) {
        T.inputItems.set(instance, value);
    }

    public static final class ShapedRecipesClass extends Template.Class<ShapedRecipesHandle> {
        public final Template.Field.Converted<List<ItemStack>> inputItems = new Template.Field.Converted<List<ItemStack>>();

    }
}
