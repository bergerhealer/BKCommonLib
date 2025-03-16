package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

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
        register("damaged", (name, contextObj) -> new BooleanProperty(name) {
            @Override
            public boolean testCondition(CommonItemStack item) {
                return !item.isUnbreakable();
            }

            @Override
            public CommonItemStack applyCondition(CommonItemStack item, boolean isTrue) {
                CommonItemStack copy = item.clone();
                copy.setUnbreakable(!isTrue);
                return copy;
            }
        });
        register("damage", (name, contextObj) -> new NumericProperty(name, contextObj) {
            @Override
            protected double getExactValue(CommonItemStack item) {
                return item.getDamage();
            }

            @Override
            protected double getMaximumValue(CommonItemStack item) {
                return item.getMaxDamage();
            }
        });
        register("has_component", (name, contextObj) -> {
            if (contextObj.has("component")) {
                return new HasComponent(name, contextObj);
            } else {
                return new Incomplete(name);
            }
        });
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
     *                   the has_component property includes the component name and
     *                   ignore_default options.
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
        public CommonItemStack applyCondition(CommonItemStack item, boolean isTrue) {
            return null;
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

    public static abstract class BooleanProperty extends ItemModelProperty {

        protected BooleanProperty(String name) {
            super(name);
        }

        /**
         * Tests whether this <b>boolean</b> property evaluates true. Returns false if the property
         * is not a boolean.
         *
         * @param item CommonItemStack
         * @return True if this property evaluates true for the item
         */
        public abstract boolean testCondition(CommonItemStack item);

        /**
         * Modifies an item to make it {@link #testCondition(CommonItemStack) test} true. Input item
         * is not modified.
         *
         * @param item Item to modify
         * @param isTrue True-state to apply to the item
         * @return Modified item, or <i>null</i> if this property cannot be changed
         *         in this way or is unknown.
         */
        public abstract CommonItemStack applyCondition(CommonItemStack item, boolean isTrue);
    }

    private static abstract class NumericProperty extends ItemModelProperty {
        private final boolean normalize;
        private final double scale;

        protected NumericProperty(String name, JsonObject obj) {
            super(name);
            this.normalize = !obj.has("normalize") || obj.get("normalize").getAsBoolean();
            this.scale = obj.has("scale") ? obj.get("scale").getAsDouble() : 1.0;
        }

        /**
         * Gets the numeric value of this property for an item. Used for range dispatch properties
         * to select a model based on, for example, the damage value.
         *
         * @param item CommonItemStack
         * @return Numeric value of this property for this item, or {@link Double#NaN}
         *         if this property is not numeric.
         */
        public double getNumericValue(CommonItemStack item) {
            double max = getMaximumValue(item);
            if (max <= 0.0) {
                return 0.0;
            } else if (normalize) {
                return scale * MathUtil.clamp(getExactValue(item) / max, 0.0, 1.0);
            } else {
                return scale * MathUtil.clamp(getExactValue(item), 0.0, max);
            }
        }

        // Internal use
        protected abstract double getExactValue(CommonItemStack item);
        protected abstract double getMaximumValue(CommonItemStack item);
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
