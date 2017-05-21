package com.bergerkiller.generated.net.minecraft.server;

import org.bukkit.inventory.ItemStack;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IRecipeHandle extends Template.Handle {
    public static final IRecipeClass T = new IRecipeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IRecipeHandle.class, "net.minecraft.server.IRecipe");


    /* ============================================================================== */

    public static IRecipeHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IRecipeHandle handle = new IRecipeHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public ItemStack getOutput() {
        return T.getOutput.invoke(instance);
    }

    public static final class IRecipeClass extends Template.Class<IRecipeHandle> {
        public final Template.Method.Converted<ItemStack> getOutput = new Template.Method.Converted<ItemStack>();

    }
}
