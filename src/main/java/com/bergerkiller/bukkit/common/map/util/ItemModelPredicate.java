package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * One or more requirements for an item model to be used. Also provides
 * a method to modify an item so that it meets this predicate.
 */
public interface ItemModelPredicate {
    ItemModelPredicate ALWAYS_TRUE_PREDICATE = new ItemModelPredicate() {
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
    };

    /**
     * Gets whether this predicate evaluates true for the item specified
     *
     * @param item CommonItemStack
     * @return True if the item matches this override
     */
    boolean isMatching(CommonItemStack item);

    /**
     * Gets whether this predicate is always matching true. This is the case for
     * root predicates that do nothing. This method might be helpful as a filter
     * for unwanted fallback models/overrides.
     *
     * @return True if this predicate always matches true
     */
    default boolean isMatchingAlways() {
        return false;
    }

    /**
     * Attempts to modify an item so that it will match this predicate.
     * Returns <i>empty</i> if this is not possible (due to an invalid
     * configuration or logical impossibility).
     *
     * @param item CommonItemStack
     * @return A copy of the item that {@link #isMatching(CommonItemStack) matches true},
     *         or <i>empty</i> on failure
     */
    Optional<CommonItemStack> tryMakeMatching(CommonItemStack item);

    /**
     * A chain of item model predicates in a tree document structure.
     * Each immutable link of the chain handles a portion of the
     * matching / apply logic. Each node also stores a List of MinecraftModel
     * values that are displayed there, which can be populated.
     */
    final class ModelChain implements ItemModelPredicate {
        private final List<ModelChain> allChainLeafs;
        private final @Nullable ModelChain parent;
        private final @Nullable CommonItemStack itemStack;
        private final ItemModelPredicate predicate;
        private @Nullable List<ItemModel.MinecraftModel> allModels = null; // cached
        private List<ItemModel.MinecraftModel> models = Collections.emptyList();

        /**
         * Creates a new empty ModelChain that has not yet been populated with models.
         * This empty chain acts as the root terminator element. Will always match true and
         * won't do anything to make an item match.
         *
         * @param itemStack The ItemStack that is the base vanilla item, or null if not available
         * @return Empty ModelChain root
         */
        public static ModelChain newRoot(@Nullable CommonItemStack itemStack) {
            return new ModelChain(null, new ArrayList<>(), itemStack, ALWAYS_TRUE_PREDICATE /* not really used */);
        }

        /**
         * Creates the next link in the chain, referring to this chain's
         * elements and the one additional new predicate.
         *
         * @param predicate ItemModelPredicate
         * @return New Chain
         */
        public ModelChain next(ItemModelPredicate predicate) {
            CommonItemStack nextItem = (itemStack == null) ? null : predicate.tryMakeMatching(itemStack).orElse(null);
            return new ModelChain(this, allChainLeafs, nextItem, predicate);
        }

        /**
         * Gets all model chain leaf nodes that have been produced so far. Every time
         * {@link #next(ItemModelPredicate)} is called, a new leaf node is created.
         * This includes the {@link #newRoot(CommonItemStack) root} chain node.
         *
         * @return All model chain leafs produced
         */
        public List<ModelChain> getAllLeafs() {
            return allChainLeafs;
        }

        private ModelChain(@Nullable ModelChain parent, List<ModelChain> allChainLeafs, @Nullable CommonItemStack itemStack, ItemModelPredicate predicate) {
            this.allChainLeafs = allChainLeafs;
            this.parent = parent;
            this.itemStack = itemStack;
            this.predicate = predicate;
            allChainLeafs.add(this);
        }

        /**
         * Gets whether this chain node declares any unique models. Chain nodes without
         * model definitions are ignored as overrides.
         *
         * @return True if models are declared
         */
        public boolean hasModels() {
            return !this.models.isEmpty();
        }

        /**
         * Adds a model that is visible for this chain's node
         *
         * @param model Model
         */
        public void addModel(ItemModel.MinecraftModel model) {
            addModels(Collections.singletonList(model));
        }

        /**
         * Adds multiple models that are visible for this chain's node
         *
         * @param models Models
         */
        public void addModels(List<ItemModel.MinecraftModel> models) {
            this.models = LogicUtil.combineUnmodifiableLists(this.models, models);
        }

        /**
         * {@link #collectAllModels() collects all models} and produces a new model override
         * with this node's predicate. If the input predicate to {@link #next(ItemModelPredicate)}
         * that produced this chain node is already an override with identical models,
         * and is the only predicate, then this simply returns that override.
         *
         * @return
         */
        public ItemModelOverride collectAsOverride() {
            List<ItemModel.MinecraftModel> allModels = this.collectAllModels(); // Won't be empty in normal logic
            if (this.isSinglePredicate()) {
                // Only has to test the one predicate (faster)
                ItemModelPredicate predicate = this.predicate;
                if (predicate instanceof ItemModelOverride) {
                    ItemModelOverride asOverride = (ItemModelOverride) predicate;
                    if (allModels.equals(asOverride.getOverrideModels())) {
                        return asOverride;
                    }
                }
                return ItemModelOverride.of(itemStack, predicate, allModels);
            } else {
                // Makes use of the chain predicate logic, which checks parents too
                return ItemModelOverride.of(itemStack, this, allModels);
            }
        }

        /**
         * Gets an unmodifiable list of MinecraftModel models that are displayed for
         * this node of the chain. Includes all models of parent chain nodes which are also
         * displayed.<br>
         * <br>
         * <b>Important note:</b> the result is cached, so it is illegal to call any further
         * {@link #addModel(ItemModel.MinecraftModel)} after this method has been called.
         *
         * @return All displayed models
         */
        public List<ItemModel.MinecraftModel> collectAllModels() {
            List<ItemModel.MinecraftModel> allModels = this.allModels;
            if (allModels != null) {
                return allModels; // Cache
            }

            ModelChain parent = this.parent;
            List<ItemModel.MinecraftModel> parentModels;
            if (parent == null || (parentModels = parent.collectAllModels()).isEmpty()) {
                return this.allModels = this.models;
            } else {
                return this.allModels = LogicUtil.combineUnmodifiableLists(parentModels, this.models);
            }
        }

        /**
         * Gets whether this chain node is not root, and is the only predicate that exists.
         * If there is more than one, or this is root, returns false. These predicates
         * are subject to some optimizations.
         *
         * @return True if this is only a single predicate
         */
        private boolean isSinglePredicate() {
            ModelChain parent = this.parent;
            return parent != null && parent.parent == null;
        }

        @Override
        public boolean isMatching(CommonItemStack item) {
            ModelChain parent = this.parent;
            if (parent == null) {
                return true; // Reached chain root
            } else {
                return parent.isMatching(item) && predicate.isMatching(item);
            }
        }

        @Override
        public boolean isMatchingAlways() {
            ModelChain parent = this.parent;
            if (parent == null) {
                return true; // Reached chain root, always matches
            } else {
                return parent.isMatchingAlways() && predicate.isMatchingAlways();
            }
        }

        @Override
        public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
            ModelChain parent = this.parent;
            if (parent == null) {
                return Optional.of(item); // Reached chain root
            } else {
                return parent.tryMakeMatching(item).flatMap(predicate::tryMakeMatching);
            }
        }
    }
}
