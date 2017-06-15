package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RecipeItemStack</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class RecipeItemStackHandle extends Template.Handle {
    /** @See {@link RecipeItemStackClass} */
    public static final RecipeItemStackClass T = new RecipeItemStackClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RecipeItemStackHandle.class, "net.minecraft.server.RecipeItemStack");

    /* ============================================================================== */

    public static RecipeItemStackHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RecipeItemStackHandle handle = new RecipeItemStackHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public List<ItemStack> getChoices() {
        return T.choices.get(instance);
    }

    public void setChoices(List<ItemStack> value) {
        T.choices.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.RecipeItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RecipeItemStackClass extends Template.Class<RecipeItemStackHandle> {
        public final Template.Field.Converted<List<ItemStack>> choices = new Template.Field.Converted<List<ItemStack>>();

    }

}

