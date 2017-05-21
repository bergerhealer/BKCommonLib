package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.List;

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

    public static CraftingManagerHandle getInstance() {
        return T.getInstance.invokeVA();
    }

    public List<IRecipeHandle> getRecipes() {
        return T.getRecipes.invoke(instance);
    }

    public static final class CraftingManagerClass extends Template.Class<CraftingManagerHandle> {
        public final Template.StaticMethod.Converted<CraftingManagerHandle> getInstance = new Template.StaticMethod.Converted<CraftingManagerHandle>();

        public final Template.Method.Converted<List<IRecipeHandle>> getRecipes = new Template.Method.Converted<List<IRecipeHandle>>();

    }
}
