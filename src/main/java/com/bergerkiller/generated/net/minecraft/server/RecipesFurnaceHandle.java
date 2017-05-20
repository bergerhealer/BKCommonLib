package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class RecipesFurnaceHandle extends Template.Handle {
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

    public static final class RecipesFurnaceClass extends Template.Class<RecipesFurnaceHandle> {
        public final Template.StaticMethod.Converted<RecipesFurnaceHandle> getInstance = new Template.StaticMethod.Converted<RecipesFurnaceHandle>();

        public final Template.Method.Converted<ItemStackHandle> getResult = new Template.Method.Converted<ItemStackHandle>();

    }
}
