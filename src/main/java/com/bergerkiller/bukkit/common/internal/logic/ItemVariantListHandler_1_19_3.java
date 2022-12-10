package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Mojang decided to remove the API previously present to find these variants. This means
 * we'll have to re-implement most of this ourselves. Thankfully, most of those methods
 * use standard registries, so the code isn't too verbose.
 */
class ItemVariantListHandler_1_19_3 extends ItemVariantListHandler {
    private final HandlerLogic handler = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
    private final Function<Object, List<?>> defaultHandler = handler::getVariants;
    private final Map<Class<?>, Function<Object, List<?>>> specialHandlers = new LinkedHashMap<>();
    private IdentityHashMap<Object, Function<Object, List<?>>> handlersByItem = new IdentityHashMap<>();

    @Override
    public void enable() throws Throwable {
        handler.forceInitialization();
        registerHandler("InstrumentItem", handler::getInstrumentItemVariants);
        registerHandler("ItemEnchantedBook", handler::getEnchantedBookVariants);
        registerHandler("ItemPotion", handler::getPotionVariants);
        registerHandler("ItemTippedArrow", handler::getTippedArrowVariants);

        handlersByItem.put(null, nmsItem -> new ArrayList<>(0)); // Protection

        // Performance: forward-initialize the handlers for all items in the registry
        // Avoids massive lag of creating hashmap copies when someone requests a full item list
        for (Object nmsItem : ItemHandle.getRegistry()) {
            handlersByItem.put(nmsItem, findHandler(nmsItem));
        }
    }

    @Override
    public List<ItemStack> getVariants(Object nmsItem) {
        // Find the handler for this item
        // Uses copy on write logic to eliminate lock overhead
        Function<Object, List<?>> handlerFunc = LogicUtil.synchronizeCopyOnWrite(
                this, /* lock */
                () -> handlersByItem, /* supplier */
                nmsItem, /* key */
                IdentityHashMap::get, /* getter */
                (map, key) -> { /* computer */
                    Function<Object, List<?>> handler = findHandler(key);
                    IdentityHashMap<Object, Function<Object, List<?>>> newMap = new IdentityHashMap<>(map);
                    newMap.put(key, handler);
                    handlersByItem = newMap;
                    return handler;
                });

        // Get variants using handler
        return new ConvertingList<ItemStack>(handlerFunc.apply(nmsItem), DuplexConversion.itemStack);
    }

    private void registerHandler(String itemTypeName, Function<Object, List<?>> handler) {
        Class<?> type = CommonUtil.getClass("net.minecraft.world.item." + itemTypeName);
        if (type == null) {
            throw new IllegalStateException("Required item type not found: " + itemTypeName);
        }
        specialHandlers.put(type, handler);
    }

    private Function<Object, List<?>> findHandler(Object nmsItem) {
        Class<?> type = nmsItem.getClass();
        for (Map.Entry<Class<?>, Function<Object, List<?>>> entry : specialHandlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return entry.getValue();
            }
        }
        return defaultHandler;
    }

    @Template.Optional
    @Template.Import("net.minecraft.world.item.ItemStack")
    @Template.Import("net.minecraft.world.item.InstrumentItem")
    @Template.Import("net.minecraft.world.item.ItemEnchantedBook")
    @Template.Import("net.minecraft.world.item.ItemPotion")
    @Template.Import("net.minecraft.world.item.ItemTippedArrow")
    @Template.Import("net.minecraft.world.item.Items")
    @Template.Import("net.minecraft.world.item.enchantment.Enchantment")
    @Template.Import("net.minecraft.world.item.enchantment.WeightedRandomEnchant")
    @Template.Import("net.minecraft.world.item.alchemy.Potions")
    @Template.Import("net.minecraft.world.item.alchemy.PotionRegistry")
    @Template.Import("net.minecraft.world.item.alchemy.PotionUtil")
    @Template.Import("net.minecraft.core.Holder")
    @Template.Import("net.minecraft.tags.TagKey")
    @Template.Import("net.minecraft.core.registries.BuiltInRegistries")
    @Template.InstanceType("net.minecraft.world.item.Item")
    public static abstract class HandlerLogic extends Template.Class<Template.Handle> {

        /*
         * <GET_VARIANTS>
         * public static List<ItemStack> getVariants(Item item) {
         *     List result = new ArrayList(1);
         *     result.add(new ItemStack(item));
         *     return result;
         * }
         */
        @Template.Generated("%GET_VARIANTS%")
        public abstract List<?> getVariants(Object nmsItem);

        /*
         * <GET_INSTRUMENT_VARIANTS>
         * public static List<ItemStack> getVariants(InstrumentItem item) {
         *     List result = new ArrayList();
         * 
         *     #require InstrumentItem private final TagKey<Instrument> instruments;
         *     TagKey instruments = item#instruments;
         *     java.util.Iterator iterator = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(instruments).iterator();
         *     while (iterator.hasNext()) {
         *         Holder holder = (Holder) iterator.next();
         *         result.add(InstrumentItem.create(Items.GOAT_HORN, holder));
         *     }
         *     return result;
         * }
         */
        @Template.Generated("%GET_INSTRUMENT_VARIANTS%")
        public abstract List<?> getInstrumentItemVariants(Object nmsItem);

        /*
         * <GET_ENCHANTED_BOOK_VARIANTS>
         * public static List<ItemStack> getVariants(ItemEnchantedBook item) {
         *     List result = new ArrayList();
         *     java.util.Iterator iterator = BuiltInRegistries.ENCHANTMENT.iterator();
         *     while (iterator.hasNext()) {
         *         Enchantment enchantment = (Enchantment) iterator.next();
         *         if (enchantment.category != null) {
         *             for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
         *                 result.add(ItemEnchantedBook.createForEnchantment(new WeightedRandomEnchant(enchantment, i)));
         *             }
         *         }
         *     }
         *     return result;
         * }
         */
        @Template.Generated("%GET_ENCHANTED_BOOK_VARIANTS%")
        public abstract List<?> getEnchantedBookVariants(Object nmsItem);

        /*
         * <GET_POTION_VARIANTS>
         * public static List<ItemStack> getVariants(ItemPotion item) {
         *     List result = new ArrayList();
         *     java.util.Iterator iterator = BuiltInRegistries.POTION.iterator();
         *     while (iterator.hasNext()) {
         *         PotionRegistry potionregistry = (PotionRegistry) iterator.next();
         *         if (potionregistry != Potions.EMPTY) {
         *             result.add(PotionUtil.setPotion(new ItemStack(item), potionregistry));
         *         }
         *     }
         *     return result;
         * }
         */
        @Template.Generated("%GET_POTION_VARIANTS%")
        public abstract List<?> getPotionVariants(Object nmsItem);

        /*
         * <GET_TIPPED_ARROW_VARIANTS>
         * public static List<ItemStack> getVariants(ItemTippedArrow item) {
         *     List result = new ArrayList();
         *     java.util.Iterator iterator = BuiltInRegistries.POTION.iterator();
         *     while (iterator.hasNext()) {
         *         PotionRegistry potionregistry = (PotionRegistry) iterator.next();
         *         if (!potionregistry.getEffects().isEmpty()) {
         *             result.add(PotionUtil.setPotion(new ItemStack(item), potionregistry));
         *         }
         *     }
         *     return result;
         * }
         */
        @Template.Generated("%GET_TIPPED_ARROW_VARIANTS%")
        public abstract List<?> getTippedArrowVariants(Object nmsItem);
    }
}
