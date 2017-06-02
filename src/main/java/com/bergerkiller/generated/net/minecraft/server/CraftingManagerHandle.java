package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftingManagerHandle extends Template.Handle {
    public static final CraftingManagerClass T = new CraftingManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftingManagerHandle.class, "net.minecraft.server.CraftingManager");

    /* ============================================================================== */

    public static CraftingManagerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftingManagerHandle handle = new CraftingManagerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public static Iterable<com.bergerkiller.generated.net.minecraft.server.IRecipeHandle> getRecipes() {
        if (T.opt_recipesField.isAvailable()) {
            Iterable<?> irecipeIter = T.opt_recipesField.get();
            return new com.bergerkiller.mountiplex.conversion.util.ConvertingIterable<IRecipeHandle>(irecipeIter,
                com.bergerkiller.generated.net.minecraft.server.IRecipeHandle.T.getHandleConverter());
        } else if (T.opt_getRecipes.isAvailable() && T.opt_getInstance.isAvailable()) {
            return T.opt_getRecipes.invoke(T.opt_getInstance.invokeVA());
        } else {
            throw new RuntimeException("Recipes listing information not resolved");
        }
    }
    public static final class CraftingManagerClass extends Template.Class<CraftingManagerHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Iterable> opt_recipesField = new Template.StaticField.Converted<Iterable>();

        @Template.Optional
        public final Template.StaticMethod.Converted<CraftingManagerHandle> opt_getInstance = new Template.StaticMethod.Converted<CraftingManagerHandle>();

        @Template.Optional
        public final Template.Method.Converted<List<IRecipeHandle>> opt_getRecipes = new Template.Method.Converted<List<IRecipeHandle>>();

    }

}

