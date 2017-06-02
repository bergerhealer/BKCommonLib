package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.ItemStack;

public class ShapelessRecipesHandle extends IRecipeHandle {
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

    public static final class ShapelessRecipesClass extends Template.Class<ShapelessRecipesHandle> {
        public final Template.Field.Converted<List<ItemStack>> inputItems = new Template.Field.Converted<List<ItemStack>>();

    }

}

