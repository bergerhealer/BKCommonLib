package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.RecipeItemStack</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.item.crafting.RecipeItemStack")
public abstract class RecipeItemStackHandle extends Template.Handle {
    /** @see RecipeItemStackClass */
    public static final RecipeItemStackClass T = Template.Class.create(RecipeItemStackClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RecipeItemStackHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<ItemStack> getChoices();
    public abstract void setChoices(List<ItemStack> choices);

    public static Object createRawRecipeItemStack(List<org.bukkit.inventory.ItemStack> choices) {
        Object raw = T.newInstanceNull();
        T.setChoices.invoke(raw, choices);
        return raw;
    }
    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.RecipeItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RecipeItemStackClass extends Template.Class<RecipeItemStackHandle> {
        public final Template.Method.Converted<List<ItemStack>> getChoices = new Template.Method.Converted<List<ItemStack>>();
        public final Template.Method.Converted<Void> setChoices = new Template.Method.Converted<Void>();

    }

}

