package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.generated.net.minecraft.server.IRecipeHandle;
import com.bergerkiller.generated.net.minecraft.server.ShapedRecipesHandle;
import com.bergerkiller.generated.net.minecraft.server.ShapelessRecipesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NMSRecipe {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("IRecipe");
    
    private static final MethodAccessor<Object> getOutput = T.selectMethod("public abstract ItemStack b()");
    
    public static class Shaped {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("ShapedRecipes");
        public static final TranslatorFieldAccessor<List<ItemStack>> inputList = T.selectField("private final ItemStack[] items").translate(DuplexConversion.itemStackList);
    }
    
    public static class Shapeless {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("ShapelessRecipes");
        public static final TranslatorFieldAccessor<List<ItemStack>> inputList = T.selectField("private final List<ItemStack> ingredients").translate(DuplexConversion.itemStackList);
    }

    public static ItemStack getOutput(Object iRecipe) {
        return Conversion.toItemStack.convert(getOutput.invoke(iRecipe));
    }

    @Deprecated
    public static List<ItemStack> getInputItems(Object iRecipe) {
        return getInputItems(IRecipeHandle.createHandle(iRecipe));
    }

    public static List<ItemStack> getInputItems(IRecipeHandle iRecipe) {
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
}
