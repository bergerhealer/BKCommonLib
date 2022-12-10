package com.bergerkiller.bukkit.common.internal.logic;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

/**
 * Call Item.fillItemCategory() to get the creative tab search items
 */
class ItemVariantListHandler_1_12_1 extends ItemVariantListHandler {
    private final HandlerLogic handler = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
    private Converter<Object, List<ItemStack>> converter;

    @Override
    public void enable() throws Throwable {
        converter = CommonUtil.unsafeCast(Conversion.find(TypeDeclaration.parse("net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack>"),
                                                          TypeDeclaration.createGeneric(List.class, ItemStack.class)));
        if (converter == null) {
            throw new IllegalStateException("Converter from NonNullList<ItemStack> to List<bukkit.ItemStack> not found!");
        }
        handler.forceInitialization();
    }

    @Override
    public List<ItemStack> getVariants(Object nmsItem) {
        return converter.convertInput(handler.getVariants(nmsItem));
    }

    @Template.Optional
    @Template.Import("net.minecraft.world.item.ItemStack")
    @Template.Import("net.minecraft.world.item.CreativeModeTab")
    @Template.Import("net.minecraft.core.NonNullList")
    @Template.InstanceType("net.minecraft.world.item.Item")
    public static abstract class HandlerLogic extends Template.Class<Template.Handle> {

        /*
         * <GET_VARIANTS>
         * public static NonNullList<ItemStack> getVariants(Item item) {
         * #if version >= 1.18
         *     NonNullList result = NonNullList.create();
         * #else
         *     NonNullList result = NonNullList.a();
         * #endif
         * 
         * #if version >= 1.18
         *     item.fillItemCategory(CreativeModeTab.TAB_SEARCH, result);
         * #elseif version >= 1.17
         *     item.a(CreativeModeTab.TAB_SEARCH, result);
         * #else
         *     item.a(CreativeModeTab.g, result);
         * #endif
         * 
         *     return result;
         * }
         */
        @Template.Generated("%GET_VARIANTS%")
        public abstract Object getVariants(Object nmsItem);
    }
}
