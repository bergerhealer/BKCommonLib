package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.IRecipe</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.crafting.IRecipe")
public abstract class IRecipeHandle extends Template.Handle {
    /** @see IRecipeClass */
    public static final IRecipeClass T = Template.Class.create(IRecipeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IRecipeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ItemStack getOutput();
    public abstract List<CraftInputSlot> getIngredients();
    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.IRecipe</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IRecipeClass extends Template.Class<IRecipeHandle> {
        public final Template.Method.Converted<ItemStack> getOutput = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<List<CraftInputSlot>> getIngredients = new Template.Method.Converted<List<CraftInputSlot>>();

    }

}

