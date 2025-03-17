package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A type of property that controls the display of an item model.
 * Only some out of all of them are supported, as not all of them can be
 * inferred server-side.
 */
public abstract class ItemModelProperty implements IndentedStringBuilder.AppendableToString {
    private static final Map<String, PropertyCreator> BY_NAME = new HashMap<>();
    private static final JsonObject NO_OBJ = new JsonObject();
    private final String name;

    static {
        register("damaged", (name, contextObj) -> new BaseBooleanProperty(name) {
            @Override
            public boolean testCondition(CommonItemStack item) {
                return !item.isUnbreakable();
            }

            @Override
            public Optional<CommonItemStack> applyCondition(CommonItemStack item, boolean isTrue) {
                CommonItemStack copy = item.clone();
                copy.setUnbreakable(!isTrue);
                return Optional.of(copy);
            }
        });
        register("damage", (name, contextObj) -> new BaseNumericProperty(name, contextObj) {
            @Override
            public double getExactValue(CommonItemStack item) {
                return item.getDamage();
            }

            @Override
            public Optional<CommonItemStack> setExactValue(CommonItemStack item, double value) {
                int maxDamage = item.getMaxDamage();
                int newDamage = (int) value;
                if (newDamage < 0 || newDamage > maxDamage) {
                    return Optional.empty();
                } else {
                    return Optional.of(item.clone().setDamage(newDamage));
                }
            }

            @Override
            public double getMaximumValue(CommonItemStack item) {
                return item.getMaxDamage() + 1;
            }
        });
        register("count", (name, contextObj) -> new BaseNumericProperty(name, contextObj) {
            @Override
            public double getExactValue(CommonItemStack item) {
                return item.getAmount();
            }

            @Override
            public Optional<CommonItemStack> setExactValue(CommonItemStack item, double value) {
                int maxAmount = item.getMaxStackSize();
                int newAmount = (int) value;
                if (newAmount <= 0 || newAmount > maxAmount) {
                    return Optional.empty();
                } else {
                    return Optional.of(item.clone().setAmount(newAmount));
                }
            }

            @Override
            public double getMaximumValue(CommonItemStack item) {
                return item.getMaxStackSize();
            }
        });
        register("custom_model_data", CustomModelDataProperty::new);

        /*
        register("has_component", (name, contextObj) -> {
            if (contextObj.has("component")) {
                return new HasComponent(name, contextObj);
            } else {
                return new Incomplete(name);
            }
        });
         */
    }

    /**
     * Looks up a property by name. If a property by this name cannot be found,
     * returns a fallback no-op property.
     *
     * @param name Name of the property
     * @return ItemModelProperty
     */
    public static ItemModelProperty get(String name) {
        return get(name, NO_OBJ);
    }

    /**
     * Looks up a property by name. If a property by this name cannot be found,
     * returns a fallback no-op property.
     *
     * @param name Name of the property
     * @param contextObj Additional context options for the property. For example,
     *                   the numeric properties include normalize and scale properties.
     * @return ItemModelProperty
     */
    public static ItemModelProperty get(String name, JsonObject contextObj) {
        return BY_NAME.getOrDefault(name, PropertyCreator.UNKNOWN).create(name, contextObj);
    }

    protected ItemModelProperty(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this property
     *
     * @return Name
     */
    public final String getName() {
        return name;
    }

    /**
     * Treats this property as a predicate property, which allows it to be used with the legacy
     * item overrides (predicates) system. If this property is not compatible, returns a
     * fallback predicate that always evaluates false.
     *
     * @return PredicateProperty
     */
    public final PredicateProperty<?> asPredicateProperty() {
        if (this instanceof PredicateProperty) {
            return (PredicateProperty<?>) this;
        } else {
            return new PredicateProperty.Incompatible(this);
        }
    }

    @Override
    public final String toString() {
        return IndentedStringBuilder.toString(this);
    }

    @Override
    public void toString(IndentedStringBuilder str) {
        str.append("Property { name: ").append(name).append(" }");
    }

    private static void register(String name, PropertyCreator creator) {
        BY_NAME.put(name, creator);
        BY_NAME.put("minecraft:" + name, creator);
    }

    /**
     * A type of property that supports the legacy predicates system. Can parse a raw predicate value
     * and then use it to check if an item matches, or makes changes to the item so that it matches.
     *
     * @param <T> Predicate value type
     */
    public interface PredicateProperty<T> extends IndentedStringBuilder.AppendableToString {
        /**
         * Gets back the original ItemModelProperty that this predicate property represents.
         *
         * @return ItemModelProperty
         */
        ItemModelProperty asItemModelProperty();

        /**
         * Whether the predicate value is the normalized value of an item property. For example,
         * the damage property is normalized between 0 and the maximum damage value.
         *
         * @return True if normalized
         */
        default boolean isPredicateNormalized() {
            return true;
        }

        /**
         * Parses a raw JSON element into a value suitable for this property. Other methods that
         * need this value type can then use this value. This is primarily used for legacy
         * item overrides / item predicate system.
         *
         * @param element Element to parse
         * @return Parsed value, or an empty optional if parsing is not possible/compatible
         */
        Optional<T> tryParsePredicateValue(JsonElement element);

        /**
         * Gets whether the current property value of an item equals the value specified.
         *
         * @param item CommonItemStack whose property to read
         * @param value Value from {@link #tryParsePredicateValue(JsonElement)}
         * @return True if this property matches value with the item
         */
        boolean isMatchingPredicate(CommonItemStack item, T value);

        /**
         * Modifies the item so that the property will equal the value specified. If this is
         * not possible, returns <i>empty</i> instead.
         *
         * @param item CommonItemStack to modify
         * @param value Value to set it to, from {@link #tryParsePredicateValue(JsonElement)}
         * @return A copy of the item with the property modified, or <i>empty</i> otherwise
         */
        Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, T value);

        /**
         * Constructs a PredicateCondition using this predicate property and a raw JSON value
         *
         * @param predicateValue JSON value assigned to the predicate property
         * @return PredicateCondition
         */
        default ItemModel.Overrides.PredicateCondition<T> asPredicateCondition(JsonElement predicateValue) {
            ItemModel.Overrides.PredicateCondition<T> condition = new ItemModel.Overrides.PredicateCondition<>();
            condition.property = this;
            condition.value = this.tryParsePredicateValue(predicateValue).orElse(null);
            return condition;
        }

        /**
         * Used when a property is specified that cannot be used as a predicate. Evaluates everything
         * as false.
         */
        class Incompatible implements PredicateProperty<Object> {
            private final ItemModelProperty property;

            protected Incompatible(ItemModelProperty property) {
                this.property = property;
            }

            @Override
            public ItemModelProperty asItemModelProperty() {
                return property;
            }

            @Override
            public Optional<Object> tryParsePredicateValue(JsonElement element) {
                return Optional.empty();
            }

            @Override
            public boolean isMatchingPredicate(CommonItemStack item, Object value) {
                return false;
            }

            @Override
            public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Object value) {
                return Optional.empty();
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Incompatible Predicate {");
                str.indent()
                        .append("\nproperty: ").append(property);
                str.append("\n}");
            }
        }
    }

    public interface BooleanProperty {

        /**
         * Tests whether this <b>boolean</b> property evaluates true. Returns false if the property
         * is not a boolean.
         *
         * @param item CommonItemStack
         * @return True if this property evaluates true for the item
         */
        boolean testCondition(CommonItemStack item);

        /**
         * Modifies an item to make it {@link #testCondition(CommonItemStack) test} true. Input item
         * is not modified.
         *
         * @param item Item to modify
         * @param isTrue True-state to apply to the item
         * @return Modified item, or <i>empty</i> if this property cannot be changed
         *         in this way or is unknown.
         */
        Optional<CommonItemStack> applyCondition(CommonItemStack item, boolean isTrue);
    }

    public interface NumericProperty {
        /**
         * Gets the scale applied to the numeric property before comparing
         *
         * @return Scale, 1.0 is default
         */
        double getScale();

        /**
         * Gets whether the value is normalized by whatever the maximum quantity of the
         * property can be. If there is no such maximum this is ignored.
         *
         * @return True if normalized
         */
        boolean isNormalized();

        /**
         * Gets the exact value of this property before normalization and scaling
         *
         * @param item CommonItemStack
         * @return Exact value
         */
        double getExactValue(CommonItemStack item);

        /**
         * Modifies an item to change the value of this property, if possible.
         * Returns <i>empty</i> otherwise.
         *
         * @param item CommonItemStack item to modify
         * @param value New value to set to
         * @return Copy of the item with property updated, or <i>empty</i> if not possible
         *         (out of range for example)
         */
        Optional<CommonItemStack> setExactValue(CommonItemStack item, double value);

        /**
         * Gets the maximum value of this property for an item. Used for normalization.
         *
         * @param item CommonItemStack
         * @return Maximum value, or NaN if there is no maximum quantity
         */
        double getMaximumValue(CommonItemStack item);

        /**
         * Gets the numeric value of this property for an item. Used for range dispatch properties
         * to select a model based on, for example, the damage value.
         *
         * @param item CommonItemStack
         * @return Numeric value of this property for this item, or {@link Double#NaN}
         *         if this property is not numeric.
         */
        default double getNumericValue(CommonItemStack item) {
            double max = getMaximumValue(item);
            if (Double.isNaN(max)) {
                return getScale() * getExactValue(item);
            } else if (max <= 0.0) {
                return 0.0;
            } else if (isNormalized()) {
                return getScale() * MathUtil.clamp(getExactValue(item) / max, 0.0, 1.0);
            } else {
                return getScale() * MathUtil.clamp(getExactValue(item), 0.0, max);
            }
        }

        /**
         * Sets the numeric value of this property for an item, making use of a set normalization
         * or scale.
         *
         * @param item CommonItemStack
         * @param value New value this property should have
         * @return A copy of the item with the property updated, or <i>empty</i> if this is not
         *         possible (out of range, for example).
         */
        default Optional<CommonItemStack> setNumericValue(CommonItemStack item, double value) {
            double max, scale;

            if ((scale = getScale()) != 0.0) {
                value /= scale;
            }
            if (isNormalized() && !Double.isNaN(max = getMaximumValue(item))) {
                value *= max;
            }
            return setExactValue(item, value);
        }
    }

    public interface StringProperty {

        /**
         * Gets the String value of this property from an item
         *
         * @param item CommonItemStack
         * @return String value, an empty String if it could not be read
         */
        String getStringValue(CommonItemStack item);

        /**
         * Modifies the item so that this property {@link #getStringValue(CommonItemStack)} will
         * return the value specified. Returns <i>empty</i> if this could not be done.
         *
         * @param item CommonItemStack
         * @param value String value this property should return
         * @return new CommonItemStack modified, or <i>empty</i> if this failed
         */
        Optional<CommonItemStack> applyStringValue(CommonItemStack item, String value);
    }

    /* ========================= Implementations ========================= */

    // This stuff is too annoying to implement, due to having to 'apply' a component by name
    /*
    private static class HasComponent extends BooleanProperty {
        private final String component;
        private final boolean ignoreDefault;

        public HasComponent(String name, JsonObject obj) {
            super(name);
            this.component = obj.get("component").getAsString();
            this.ignoreDefault = obj.has("ignore_default") && obj.get("ignore_default").getAsBoolean();
        }

        @Override
        public boolean testCondition(CommonItemStack item) {
            return false;
        }

        @Override
        public Optional<CommonItemStack> applyCondition(CommonItemStack item, boolean isTrue) {
            return Optional.empty();
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Property {");
            str.indent(2)
                    .append("\nname: ").append(getName())
                    .append("\ncomponent: ").append(component)
                    .append("\nignore_default: ").append(ignoreDefault);
            str.append("\n}");
        }
    }
    */

    private static class CustomModelDataProperty extends ItemModelProperty implements NumericProperty, StringProperty, BooleanProperty, PredicateProperty<Integer> {
        private final int index;
        private final boolean normalize;
        private final double scale;

        protected CustomModelDataProperty(String name, JsonObject obj) {
            super(name);
            this.index = obj.has("index") ? obj.get("index").getAsInt() : 0;

            // Numeric only
            this.normalize = !obj.has("normalize") || obj.get("normalize").getAsBoolean();
            this.scale = obj.has("scale") ? obj.get("scale").getAsDouble() : 1.0;
        }

        @Override
        public double getScale() {
            return scale;
        }

        @Override
        public boolean isNormalized() {
            return normalize;
        }

        @Override
        public double getExactValue(CommonItemStack item) {
            List<Float> floats = item.getCustomModelDataComponents().floats();
            return (index >= 0 && index < floats.size()) ? floats.get(index) : 0.0;
        }

        @Override
        public Optional<CommonItemStack> setExactValue(CommonItemStack item, double value) {
            return Optional.empty();
        }

        @Override
        public double getMaximumValue(CommonItemStack item) {
            return Double.NaN;
        }

        @Override
        public String getStringValue(CommonItemStack item) {
            List<String> strings = item.getCustomModelDataComponents().strings();
            return (index >= 0 && index < strings.size()) ? strings.get(index) : "";
        }

        @Override
        public Optional<CommonItemStack> applyStringValue(CommonItemStack item, String value) {
            if (index < 0) {
                return Optional.empty();
            }

            CustomModelData cmd = item.getCustomModelDataComponents();
            List<String> strings = new ArrayList<>(cmd.strings());

            // Need to add padding values if index is beyond the range
            while (index >= strings.size()) {
                strings.add("");
            }

            // Modify at this index
            strings.set(index, value);

            return Optional.of(item.clone().setCustomModelDataComponents(cmd.withStrings(strings)));
        }

        @Override
        public boolean testCondition(CommonItemStack item) {
            List<Boolean> flags = item.getCustomModelDataComponents().flags();
            return index >= 0 && index < flags.size() && flags.get(index);
        }

        @Override
        public Optional<CommonItemStack> applyCondition(CommonItemStack item, boolean isTrue) {
            if (index < 0) {
                return Optional.empty();
            }

            CustomModelData cmd = item.getCustomModelDataComponents();
            List<Boolean> flags = new ArrayList<>(cmd.flags());
            if (isTrue) {
                // Add padding 'FALSE' flags if needed
                while (index >= flags.size()) {
                    flags.add(Boolean.FALSE);
                }
                flags.set(index, Boolean.TRUE);
                return Optional.of(item.clone().setCustomModelDataComponents(cmd.withFlags(flags)));
            } else if (index >= flags.size() || !flags.get(index)) {
                // No need to set it because condition is already false
                return Optional.of(item.clone());
            } else {
                // Switch flag to false
                flags.set(index, Boolean.FALSE);
                return Optional.of(item.clone().setCustomModelDataComponents(cmd.withFlags(flags)));
            }
        }

        @Override
        public ItemModelProperty asItemModelProperty() {
            return this;
        }

        @Override
        public boolean isPredicateNormalized() {
            return false;
        }

        @Override
        public Optional<Integer> tryParsePredicateValue(JsonElement element) {
            return MapResourcePackDeserializer.tryParseAsInt(element);
        }

        @Override
        public boolean isMatchingPredicate(CommonItemStack item, Integer value) {
            return item.hasCustomModelData() && item.getCustomModelData() == value;
        }

        @Override
        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Integer value) {
            return Optional.of(item.clone().setCustomModelData(value));
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Property {")
                    .append(" name: ").append(getName())
                    .append(", index: ").append(index)
                    .append(" }");
        }
    }

    private static abstract class BaseBooleanProperty extends ItemModelProperty implements BooleanProperty, PredicateProperty<Boolean> {

        protected BaseBooleanProperty(String name) {
            super(name);
        }

        @Override
        public ItemModelProperty asItemModelProperty() {
            return this;
        }

        @Override
        public Optional<Boolean> tryParsePredicateValue(JsonElement element) {
            return MapResourcePackDeserializer.tryParseAsBoolean(element);
        }

        @Override
        public boolean isMatchingPredicate(CommonItemStack item, Boolean value) {
            return this.testCondition(item) == value;
        }

        @Override
        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Boolean value) {
            return this.applyCondition(item, value);
        }
    }

    private static abstract class BaseNumericProperty extends ItemModelProperty implements NumericProperty, PredicateProperty<Double> {
        private final boolean normalize;
        private final double scale;

        protected BaseNumericProperty(String name, JsonObject obj) {
            super(name);
            this.normalize = !obj.has("normalize") || obj.get("normalize").getAsBoolean();
            this.scale = obj.has("scale") ? obj.get("scale").getAsDouble() : 1.0;
        }

        @Override
        public ItemModelProperty asItemModelProperty() {
            return this;
        }

        @Override
        public Optional<Double> tryParsePredicateValue(JsonElement element) {
            return MapResourcePackDeserializer.tryParseAsDouble(element);
        }

        @Override
        public boolean isMatchingPredicate(CommonItemStack item, Double value) {
            double itemValue = getExactValue(item);
            if (isPredicateNormalized()) {
                double max = getMaximumValue(item);
                if (!Double.isNaN(max)) {
                    if (max == 0.0) {
                        itemValue = 0.0; // Eh?
                    } else {
                        itemValue /= max;
                    }
                }
            }
            return Math.abs(itemValue - value) <= 0.0000001;
        }

        @Override
        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Double value) {
            if (!isPredicateNormalized()) {
                return setExactValue(item, value);
            }
            double max = getMaximumValue(item);
            if (Double.isNaN(max)) {
                return setExactValue(item, value);
            } else if (max != 0.0) {
                return setExactValue(item, value * max);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public double getScale() {
            return scale;
        }

        @Override
        public boolean isNormalized() {
            return normalize;
        }
    }

    private static class Incomplete extends ItemModelProperty {

        public Incomplete(String name) {
            super(name);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Incomplete Property { name: ").append(getName()).append(" }");
        }
    }

    private static class Unknown extends ItemModelProperty {

        public Unknown(String name) {
            super(name);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Unknown Property { name: ").append(getName()).append(" }");
        }
    }

    @FunctionalInterface
    private interface PropertyCreator {
        PropertyCreator UNKNOWN = (name, jsonObject) -> new Unknown(name);

        ItemModelProperty create(String name, JsonObject jsonObject);
    }
}
