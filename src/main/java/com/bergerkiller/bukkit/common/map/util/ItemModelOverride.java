package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A unique override for a model. Provides ways to check whether an item
 * would trigger the override and to modify an item so that it matches this override
 * ({@link ItemModelPredicate}). Provides a list of {@link ItemModel.MinecraftModel models}
 * that are displayed when this override is active.
 */
public interface ItemModelOverride extends ItemModelPredicate {
    /**
     * Gets the MinecraftModel models that are displayed when this override is
     * {@link #isMatching(CommonItemStack) matching}. Can return more than one
     * in case {@link ItemModel.Composite} is used.
     *
     * @return List of models displayed for this override
     */
    List<ItemModel.MinecraftModel> getOverrideModels();

    /**
     * Gets the ItemStack that will display this model override. This is the base
     * vanilla item with all override predicates applied, if possible. If some of
     * these predicates could not be applied, or the item name is invalid, then this will
     * return empty.
     *
     * @return ItemStack that displays this model override, or empty if not available
     */
    Optional<CommonItemStack> getItemStack();

    /**
     * Creates a new override for a case that is true by default, always returning a
     * specific List of models.
     *
     * @param itemStack The ItemStack that will display this override, use null if not available
     * @param models List of models
     * @return ItemModelOverride
     */
    static ItemModelOverride of(@Nullable final CommonItemStack itemStack, final List<ItemModel.MinecraftModel> models) {
        return new ItemModelOverride() {
            @Override
            public boolean isMatching(CommonItemStack item) {
                return true;
            }

            @Override
            public boolean isMatchingAlways() {
                return true;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                return Optional.of(item);
            }

            @Override
            public List<ItemModel.MinecraftModel> getOverrideModels() {
                return models;
            }

            @Override
            public Optional<CommonItemStack> getItemStack() {
                return Optional.ofNullable(itemStack);
            }
        };
    }

    /**
     * Creates a new override by combining a predicate and a List of models
     *
     * @param itemStack The ItemStack that will display this override, use null if not available
     * @param predicate ItemModelPredicate
     * @param models List of models
     * @return ItemModelOverride
     */
    static ItemModelOverride of(@Nullable final CommonItemStack itemStack, final ItemModelPredicate predicate, final List<ItemModel.MinecraftModel> models) {
        return new ItemModelOverride() {
            @Override
            public boolean isMatching(CommonItemStack item) {
                return predicate.isMatching(item);
            }

            @Override
            public boolean isMatchingAlways() {
                return predicate.isMatchingAlways();
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                return predicate.tryMakeMatching(item);
            }

            @Override
            public List<ItemModel.MinecraftModel> getOverrideModels() {
                return models;
            }

            @Override
            public Optional<CommonItemStack> getItemStack() {
                return Optional.ofNullable(itemStack);
            }
        };
    }
}
