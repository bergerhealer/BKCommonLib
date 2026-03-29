package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NMSRecipe {
    public static final ClassTemplate<?> T = ClassTemplate.create(RecipeHandle.T.getType());

    /**
     * Deprecated: use {@link RecipeHandle#getOutput()} instead.
     */
    @Deprecated
    public static ItemStack getOutput(Object iRecipe) {
        return RecipeHandle.T.getOutput.invoke(iRecipe);
    }

    /**
     * Deprecated: use {@link RecipeHandle#getIngredients()} instead.
     * @param iRecipe
     */
    @Deprecated
    public static List<CraftInputSlot> getInputSlots(RecipeHandle iRecipe) {
        return (iRecipe == null) ? null : iRecipe.getIngredients();
    }

    /**
     * Deprecated: input slots have more than one choice. Use {@link #getInputSlots(RecipeHandle)} instead.
     */
    @Deprecated
    public static List<ItemStack> getInputItems(Object iRecipe) {
        return getInputItems(RecipeHandle.createHandle(iRecipe));
    }

    /**
     * Deprecated: input slots have more than one choice. Use {@link #getInputSlots(RecipeHandle)} instead.
     */
    @Deprecated
    public static List<ItemStack> getInputItems(RecipeHandle iRecipe) {
        List<CraftInputSlot> slots = getInputSlots(iRecipe);
        if (slots == null) {
            return null;
        } else {
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            for (CraftInputSlot slot : slots) {
                items.add(slot.getDefaultChoice());
            }
            return items;
        }
    }
}
