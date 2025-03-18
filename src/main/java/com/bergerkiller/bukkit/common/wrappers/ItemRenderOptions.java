package com.bergerkiller.bukkit.common.wrappers;

import java.util.Map;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * Render options for item models
 */
public class ItemRenderOptions extends RenderOptions {
    private CommonItemStack item;

    public ItemRenderOptions(ItemStack item, String optionsToken) {
        this(CommonItemStack.of(item), optionsToken);
    }

    public ItemRenderOptions(ItemStack item, Map<String, String> optionsMap) {
        this(CommonItemStack.of(item), optionsMap);
    }

    public ItemRenderOptions(CommonItemStack item, String optionsToken) {
        super(optionsToken);
        this.item = item;
    }

    public ItemRenderOptions(CommonItemStack item, Map<String, String> optionsMap) {
        super(optionsMap);
        this.item = item;
    }

    /**
     * Gets the ItemStack of the item this model is for
     * 
     * @return item stack
     */
    public final ItemStack getItem() {
        return this.item.toBukkit();
    }

    /**
     * Gets the CommonItemStack of the item this model is for
     *
     * @return item stack
     */
    public final CommonItemStack getCommonItem() {
        return this.item;
    }

    @Override
    public final String lookupModelName() {
        return ModelInfoLookup.lookupItem(this);
    }

    @Override
    public ItemRenderOptions clone() {
        if (this.optionsMap != null) {
            return new ItemRenderOptions(this.item.clone(), this.optionsMap);
        } else {
            return new ItemRenderOptions(this.item.clone(), this.optionsToken);
        }
    }
}
