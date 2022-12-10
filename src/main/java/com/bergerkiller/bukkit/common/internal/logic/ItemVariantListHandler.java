package com.bergerkiller.bukkit.common.internal.logic;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;

/**
 * Provides the default variants listed in the creative menu for all items on
 * the server
 */
public abstract class ItemVariantListHandler implements LibraryComponent {
    public static final ItemVariantListHandler INSTANCE = LibraryComponentSelector.forModule(ItemVariantListHandler.class)
            .runFirst(CommonBootstrap::initServer)
            .addVersionOption(null, "1.12", ItemVariantListHandler_1_8::new)
            .addVersionOption("1.12.1", "1.19.2", ItemVariantListHandler_1_12_1::new)
            .addVersionOption("1.19.3", null, ItemVariantListHandler_1_19_3::new)
            .update();

    @Override
    public void disable() {
    }

    /**
     * Gets the default variants of a particular NMS Item as listed in the player's creative
     * tab search menu.
     *
     * @param nmsItem
     * @return List of items of this item listed. Might be an empty list if not listable.
     */
    public abstract List<ItemStack> getVariants(Object nmsItem);
}
