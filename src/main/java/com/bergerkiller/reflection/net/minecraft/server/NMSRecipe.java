package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.TranslatorFieldAccessor;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NMSRecipe {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("IRecipe");
    
    private static final MethodAccessor<Object> getOutput = T.selectMethod("public abstract ItemStack b()");
    
    public static class Shaped {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("ShapedRecipes");
        public static final TranslatorFieldAccessor<List<ItemStack>> inputList = T.selectField("private final ItemStack[] items").translate(ConversionPairs.itemStackList);
    }
    
    public static class Shapeless {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("ShapelessRecipes");
        public static final TranslatorFieldAccessor<List<ItemStack>> inputList = T.selectField("private final List<ItemStack> ingredients").translate(ConversionPairs.itemStackList);
    }

    public static ItemStack getOutput(Object iRecipe) {
        return Conversion.toItemStack.convert(getOutput.invoke(iRecipe));
    }
    
    public static List<ItemStack> getInputItems(Object iRecipe) {
        if (Shaped.T.isInstance(iRecipe)) {
            return Shaped.inputList.get(iRecipe);
        } else if (Shapeless.T.isInstance(iRecipe)) {
            return Shapeless.inputList.get(iRecipe);
        } else {
            return null;
        }
    }
}
