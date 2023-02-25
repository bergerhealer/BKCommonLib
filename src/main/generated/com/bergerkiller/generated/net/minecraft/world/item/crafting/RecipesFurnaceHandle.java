package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.RecipesFurnace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.item.crafting.RecipesFurnace")
public abstract class RecipesFurnaceHandle extends Template.Handle {
    /** @see RecipesFurnaceClass */
    public static final RecipesFurnaceClass T = Template.Class.create(RecipesFurnaceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RecipesFurnaceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static RecipesFurnaceHandle getInstance() {
        return T.getInstance.invoke();
    }

    public abstract ItemStackHandle getResult(ItemStackHandle itemstack);
    public abstract Map<ItemStack, ItemStack> getRecipes();
    public abstract void setRecipes(Map<ItemStack, ItemStack> value);
    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.RecipesFurnace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RecipesFurnaceClass extends Template.Class<RecipesFurnaceHandle> {
        public final Template.Field.Converted<Map<ItemStack, ItemStack>> recipes = new Template.Field.Converted<Map<ItemStack, ItemStack>>();

        public final Template.StaticMethod.Converted<RecipesFurnaceHandle> getInstance = new Template.StaticMethod.Converted<RecipesFurnaceHandle>();

        public final Template.Method.Converted<ItemStackHandle> getResult = new Template.Method.Converted<ItemStackHandle>();

    }

}

