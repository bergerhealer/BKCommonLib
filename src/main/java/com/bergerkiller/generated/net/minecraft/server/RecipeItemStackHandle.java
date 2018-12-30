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
public abstract class RecipeItemStackHandle extends Template.Handle {
    /** @See {@link RecipeItemStackClass} */
    public static final RecipeItemStackClass T = new RecipeItemStackClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RecipeItemStackHandle.class, "net.minecraft.server.RecipeItemStack", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static RecipeItemStackHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<ItemStack> getChoices();

    public static Object createRawRecipeItemStack(List<org.bukkit.inventory.ItemStack> choices) {
        Object raw = T.newInstanceNull();
        T.choices.set(raw, choices);
        return raw;
    }
    /**
     * Stores class members for <b>net.minecraft.server.RecipeItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RecipeItemStackClass extends Template.Class<RecipeItemStackHandle> {
        @Template.Optional
        public final Template.Field.Converted<List<ItemStack>> choices = new Template.Field.Converted<List<ItemStack>>();

        public final Template.Method.Converted<List<ItemStack>> getChoices = new Template.Method.Converted<List<ItemStack>>();

    }

}

