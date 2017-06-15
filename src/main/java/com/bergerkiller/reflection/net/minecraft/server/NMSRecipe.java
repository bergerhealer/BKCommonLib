package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.generated.net.minecraft.server.IRecipeHandle;
import com.bergerkiller.generated.net.minecraft.server.ShapedRecipesHandle;
import com.bergerkiller.generated.net.minecraft.server.ShapelessRecipesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NMSRecipe {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("IRecipe");

    public static ItemStack getOutput(Object iRecipe) {
        return IRecipeHandle.T.getOutput.invoke(iRecipe);
    }

    public static List<CraftInputSlot> getInputSlots(IRecipeHandle iRecipe) {
        if (iRecipe == null) {
            return null;
        } else if (iRecipe.isInstanceOf(ShapedRecipesHandle.T)) {
            return ShapedRecipesHandle.T.inputItems.get(iRecipe.getRaw());
        } else if (iRecipe.isInstanceOf(ShapelessRecipesHandle.T)) {
            return ShapelessRecipesHandle.T.inputItems.get(iRecipe.getRaw());
        } else {
            return null;
        }
    }

    /**
     * Deprecated: input slots have more than one choice. Use {@link #getInputSlots(IRecipeHandle)} instead.
     */
    @Deprecated
    public static List<ItemStack> getInputItems(Object iRecipe) {
        return getInputItems(IRecipeHandle.createHandle(iRecipe));
    }

    /**
     * Deprecated: input slots have more than one choice. Use {@link #getInputSlots(IRecipeHandle)} instead.
     */
    @Deprecated
    public static List<ItemStack> getInputItems(IRecipeHandle iRecipe) {
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
