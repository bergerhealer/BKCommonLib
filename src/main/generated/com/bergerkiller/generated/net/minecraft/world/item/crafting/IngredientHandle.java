package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.Ingredient</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.item.crafting.Ingredient")
public abstract class IngredientHandle extends Template.Handle {
    /** @see IngredientClass */
    public static final IngredientClass T = Template.Class.create(IngredientClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IngredientHandle createHandle(Object handleInstance) {
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
     * Stores class members for <b>net.minecraft.world.item.crafting.Ingredient</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IngredientClass extends Template.Class<IngredientHandle> {
        public final Template.Method.Converted<List<ItemStack>> getChoices = new Template.Method.Converted<List<ItemStack>>();
        public final Template.Method.Converted<Void> setChoices = new Template.Method.Converted<Void>();

    }

}

