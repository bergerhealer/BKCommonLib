package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;

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
     * Creates a new override for a case that is true by default, always returning a
     * specific List of models.
     *
     * @param models List of models
     * @return ItemModelOverride
     */
    static ItemModelOverride of(final List<ItemModel.MinecraftModel> models) {
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
        };
    }

    /**
     * Creates a new override by combining a predicate and a List of models
     *
     * @param predicate ItemModelPredicate
     * @param models List of models
     * @return ItemModelOverride
     */
    static ItemModelOverride of(final ItemModelPredicate predicate, final List<ItemModel.MinecraftModel> models) {
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
        };
    }
}
