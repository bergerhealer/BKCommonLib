package com.bergerkiller.bukkit.common.inventory;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * Represents all the information stored for a particular input slot of a crafting recipe.
 * For example, the possible items that match can be queried.
 */
public class CraftInputSlot {
    private final ItemStack[] choices;

    public CraftInputSlot(List<ItemStack> choices) {
        this.choices = choices.toArray(new ItemStack[choices.size()]);
    }

    public CraftInputSlot(ItemStack[] choices) {
        this.choices = choices;
    }

    /**
     * Gets all possible items that match for this input slot
     * 
     * @return matched item choices
     */
    public ItemStack[] getChoices() {
        return this.choices;
    }

    /**
     * Gets the first choice from the available choices. Will always return non-null.
     * 
     * @return default choice
     */
    public ItemStack getDefaultChoice() {
        return (this.choices.length == 0) ? ItemUtil.emptyItem() : this.choices[0];
    }

    /**
     * Checks if an item matches one of the choices of this input slot.
     * If this input slot defines a durability, that must match as well.
     * When a match is found, the matched item is returned. Otherwise, null is returned.
     * 
     * @param item to match
     * @return matched item, or null if no choice was matched
     */
    public ItemStack match(ItemStack item) {
        if (!ItemUtil.isEmpty(item)) {
            for (ItemStack choice : this.choices) {
                if (choice.getType() != item.getType()) {
                    continue;
                }
                if (choice.getDurability() == Short.MAX_VALUE || choice.getDurability() == item.getDurability()) {
                    return choice;
                }
            }
        }
        return null;
    }

    @Override
    public CraftInputSlot clone() {
        ItemStack[] clonedChoices = new ItemStack[this.choices.length];
        for (int i = 0; i < clonedChoices.length; i++) {
            clonedChoices[i] = this.choices[i].clone();
        }
        return new CraftInputSlot(clonedChoices);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        for (int i = 0; i < this.choices.length; i++) {
            if (i > 0) result.append(", ");
            result.append(this.choices[i].toString());
        }
        result.append("}");
        return result.toString();
    }

    /**
     * Attempts to merge the input choices from this input slot with another.
     * If either slots are multiple-choice or differ in inputs, this returns null.
     * 
     * @param other
     * @return merged input slot, or null if not mergable
     */
    public CraftInputSlot tryMergeWith(CraftInputSlot other) {
        if (this.choices.length != 1 || other.choices.length != 1) {
            return null;
        }
        if (!ItemUtil.equalsIgnoreAmount(this.choices[0], other.choices[0])) {
            return null;
        }
        ItemStack item = this.choices[0].clone();
        ItemUtil.addAmount(item, other.choices[0].getAmount());
        return new CraftInputSlot(new ItemStack[] { item });
    }
}
