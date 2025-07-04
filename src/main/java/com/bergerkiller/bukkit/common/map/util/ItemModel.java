package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Used on Minecraft 1.21.4 and later to represent the "item model" graph. This sets up
 * the conditions for showing particular models for items. For resource packs using the
 * older predicate system, it makes use of {@link Overrides}<br>
 * <br>
 * The ItemModel represents a single node of this graph.
 */
public abstract class ItemModel implements IndentedStringBuilder.AppendableToString {

    @Override
    public final String toString() {
        return IndentedStringBuilder.toString(this);
    }

    /**
     * Resolves the models that should be displayed for rendering the item. Can return
     * more than one in case a {@link Composite} item model node is used.
     *
     * @param item Item whose models to resolve
     * @return List of MinecraftModel model names
     */
    public abstract List<MinecraftModel> resolveModels(CommonItemStack item);

    /**
     * Gets whether any valid models exist. If false, then {@link #resolveModels(CommonItemStack)}
     * will only ever return {@link MinecraftModel#NOT_SET}.
     *
     * @return True if item models exist
     */
    public abstract boolean hasValidModels();

    /**
     * Creates a flattened List of all item model overrides that exist for this
     * item model configuration. This might not be perfectly accurate if identical
     * predicates exist in several places of the item model tree, but it works
     * well enough for most usual cases.<br>
     * <br>
     * For the root entry, this automatically sets the base ItemStack. So the
     * override {@link ItemModelOverride#getItemStack()} can be used. This will
     * not happen for nested entries.
     *
     * @return Flattened List of ItemModelOverrides
     */
    public List<ItemModelOverride> listAllOverrides() {
        return listAllOverrides(null);
    }

    /**
     * Creates a flattened List of all item model overrides that exist for this
     * item model configuration. This might not be perfectly accurate if identical
     * predicates exist in several places of the item model tree, but it works
     * well enough for most usual cases.
     *
     * @param baseItemStack The base ItemStack that displays this vanilla item, or null if not available
     * @return Flattened List of ItemModelOverrides
     */
    public final List<ItemModelOverride> listAllOverrides(@Nullable CommonItemStack baseItemStack) {
        ItemModelPredicate.ModelChain root = ItemModelPredicate.ModelChain.newRoot(baseItemStack);
        this.populateModelChain(root);

        return root.getAllLeafs().stream()
                .filter(ItemModelPredicate.ModelChain::hasModels)
                .map(ItemModelPredicate.ModelChain::collectAsOverride)
                .collect(Collectors.toList());
    }

    /**
     * Fills a {@link ItemModelPredicate.ModelChain} with predicates and model definitions.
     * Produces a List of leaf nodes, which can then be turned into unique overrides.
     *
     * @param chain Predicate chain navigated so far
     */
    protected abstract void populateModelChain(ItemModelPredicate.ModelChain chain);

    public static class ItemModelDeserializer implements JsonDeserializer<ItemModel> {
        private final Map<String, JsonDeserializer<? extends ItemModel>> byName = new HashMap<>();

        public ItemModelDeserializer() {
            registerDeserializerToType("model", MinecraftModel.class);
            registerDeserializerToType("condition", Condition.class);
            registerDeserializerToType("range_dispatch", RangeDispatch.class);
            registerDeserializerToType("select", Select.class);
            registerDeserializerToType("composite", Composite.class);
        }

        private void registerDeserializerToType(String name, final Class<? extends ItemModel> itemModelType) {
            registerDeserializer(name, (jsonElement, type, jsonDeserializationContext) -> jsonDeserializationContext.deserialize(jsonElement, itemModelType));
        }

        private void registerDeserializer(String name, JsonDeserializer<? extends ItemModel> deserializer) {
            byName.put(name, deserializer);
            byName.put("minecraft:" + name, deserializer);
        }

        @Override
        public ItemModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String typeName = obj.get("type").getAsString();
            JsonDeserializer<? extends ItemModel> deserializer = byName.get(typeName);
            if (deserializer != null) {
                return deserializer.deserialize(jsonElement, type, jsonDeserializationContext);
            }
            return new UnknownItemModel(typeName);
        }
    }

    public static void registerDeserializers(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(ItemModel.class, new ItemModelDeserializer());

        gsonBuilder.registerTypeAdapter(Condition.class, (JsonDeserializer<Condition>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Condition condition = new Condition();
            if (obj.has("property")) {

            } else {

            }
            condition.property = tryParseProperty(jsonDeserializationContext, obj, "property");
            condition.on_true = tryParseItemModel(jsonDeserializationContext, obj, "on_true");
            condition.on_false = tryParseItemModel(jsonDeserializationContext, obj, "on_false");
            return condition;
        });

        gsonBuilder.registerTypeAdapter(RangeDispatch.class, (JsonDeserializer<RangeDispatch>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            RangeDispatch dispatch = new RangeDispatch();

            dispatch.property = tryParseProperty(jsonDeserializationContext, obj, "property");
            dispatch.fallback = tryParseItemModel(jsonDeserializationContext, obj, "fallback");

            if (obj.has("entries")) {
                JsonArray arr = obj.get("entries").getAsJsonArray();
                int size = arr.size();
                dispatch.entries = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    RangeDispatch.Entry e = jsonDeserializationContext.deserialize(arr.get(i), RangeDispatch.Entry.class);
                    e.property = dispatch.property;
                    dispatch.entries.add(e);
                }

                // Populate the adjacent entry maximum threshold information
                // According to wiki: "Will select last entry with threshold less or equal to property value."
                // This behavior is odd but hopefully this is correct...
                double maxThreshold = Double.MAX_VALUE;
                for (int i = size - 1; i >= 0; --i) {
                    RangeDispatch.Entry e = dispatch.entries.get(i);
                    e.maxThreshold = maxThreshold;
                    maxThreshold = Math.min(e.minThreshold, maxThreshold);
                }

                dispatch.entries = Collections.unmodifiableList(dispatch.entries);
            } else {
                dispatch.entries = Collections.emptyList();
            }

            return dispatch;
        });

        gsonBuilder.registerTypeAdapter(Select.Case.class, (JsonDeserializer<Select.Case>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Select.Case c = new Select.Case();
            if (obj.has("when")) {
                JsonElement whenElement = obj.get("when");
                if (whenElement.isJsonArray()) {
                    JsonArray whenArray = whenElement.getAsJsonArray();
                    int whenCount = whenArray.size();
                    ArrayList<Select.WhenValue> whenArrayValues = new ArrayList<>(whenCount);
                    for (int i = 0; i < whenCount; i++) {
                        whenArrayValues.add(Select.WhenValue.deserialize(whenArray.get(i)));
                    }
                    c.when = Collections.unmodifiableList(whenArrayValues);
                } else {
                    c.when = Collections.singletonList(Select.WhenValue.deserialize(whenElement));
                }
            } else {
                c.when = Collections.emptyList();
            }

            c.model = tryParseItemModel(jsonDeserializationContext, obj, "model");

            return c;
        });

        gsonBuilder.registerTypeAdapter(Select.class, (JsonDeserializer<Select>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Select select = new Select();

            select.property = tryParseProperty(jsonDeserializationContext, obj, "property");
            select.fallback = tryParseItemModel(jsonDeserializationContext, obj, "fallback");

            if (obj.has("cases")) {
                JsonArray arr = obj.get("cases").getAsJsonArray();
                int size = arr.size();
                select.cases = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    Select.Case c = jsonDeserializationContext.deserialize(arr.get(i), Select.Case.class);
                    c.property = select.property;
                    select.cases.add(c);
                }
                select.cases = Collections.unmodifiableList(select.cases);
            } else {
                select.cases = Collections.emptyList();
            }

            return select;
        });

        /*
         * Deserializer for a single override in the overrides list
         * {
         *   "_cmd_": "give @p minecraft:golden_pickaxe 1 10 {Unbreakable:1}",
         *   "predicate": {"damaged": 0, "damage": 0.30303030303030304},
         *   "model": "traincarts:nubx/traincarts_locomotive_full"
         * }
         */
        gsonBuilder.registerTypeAdapter(Overrides.OverriddenModel.class, (JsonDeserializer<Overrides.OverriddenModel>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Overrides.OverriddenModel result = new Overrides.OverriddenModel();

            if (obj.has("predicate")) {
                JsonObject predicateListObj = obj.get("predicate").getAsJsonObject();
                List<Overrides.PredicateCondition<?>> conditions = new ArrayList<>(predicateListObj.size());
                for (Map.Entry<String, JsonElement> predicateObj : predicateListObj.entrySet()) {
                    ItemModelProperty.PredicateProperty<?> property = ItemModelProperty.get(predicateObj.getKey()).asPredicateProperty();
                    conditions.add(property.asPredicateCondition(predicateObj.getValue()));
                }
                result.predicate = Collections.unmodifiableList(conditions);
            }

            if (obj.has("model")) {
                result.models = Collections.singletonList(MinecraftModel.of(obj.get("model").getAsString()));
            }

            return result;
        });
    }

    private static ItemModelProperty tryParseProperty(JsonDeserializationContext jsonDeserializationContext, JsonObject obj, String key) {
        if (!obj.has(key)) {
            return ItemModelProperty.NONE;
        }

        String name = "<unknown>";
        try {
            name = obj.get(key).getAsString();
            return ItemModelProperty.get(name, obj);
        } catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to deserialize an item model property '" + name + "'", t);
            return ItemModelProperty.NONE;
        }
    }

    private static ItemModel tryParseItemModel(JsonDeserializationContext jsonDeserializationContext, JsonObject obj, String key) {
        if (!obj.has(key)) {
            return MinecraftModel.NOT_SET;
        }

        try {
            return jsonDeserializationContext.deserialize(obj.get(key), ItemModel.class);
        } catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to deserialize an item model", t);
            return MinecraftModel.NOT_SET;
        }
    }

    // Unknown or unsupported
    public static class UnknownItemModel extends ItemModel {
        public final String type;

        public UnknownItemModel(String type) {
            this.type = type;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Unknown { type: ").append(type).append(" }");
        }

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return Collections.emptyList();
        }

        @Override
        public boolean hasValidModels() {
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
        }
    }

    /**
     * The root document of the item model json file
     */
    public static class Root extends ItemModel {
        /** Set to the vanilla ItemStack that displays the model, if available */
        public transient @Nullable CommonItemStack baseItemStack = null;
        public ItemModel model;
        public boolean hand_animation_on_swap = true;

        public ItemModel getModel() {
            ItemModel model = this.model;
            return (model != null) ? model : MinecraftModel.NOT_SET;
        }

        @Override
        public List<ItemModelOverride> listAllOverrides() {
            return listAllOverrides(baseItemStack);
        }

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return getModel().resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            return getModel().hasValidModels();
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            getModel().populateModelChain(chain);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("{");
            str.indent().append("\nmodel: ").append(model);
            str.append("\n}");
        }
    }

    public static class MinecraftModel extends ItemModel {
        /** Placeholder model value when an ItemModel model field is not set, such as with range_dispatch fallback */
        public static final MinecraftModel NOT_SET = new MinecraftModel();
        public static final List<MinecraftModel> NOT_SET_LIST = Collections.singletonList(NOT_SET);
        static {
            NOT_SET.model = "minecraft:builtin/missing";
        }

        public static MinecraftModel of(String name) {
            MinecraftModel model = new MinecraftModel();
            model.model = name;
            return model;
        }

        public String model;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return Collections.singletonList(this);
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            chain.addModel(this);
        }

        @Override
        public boolean hasValidModels() {
            return this != NOT_SET;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Model { name: " + model + " }");
        }
    }

    /**
     * Used for the older predicates system, stored in the models/item/*.json files directly.
     * See: <a href="https://minecraft.wiki/w/Tutorial:Models#Item_predicates">Wiki page about predicates</a>
     */
    public static class Overrides extends ItemModel {
        /** List of model overrides based on the predicates system */
        public List<OverriddenModel> overrides = Collections.emptyList();
        /** Model to use if none of the overrides match. Refers to the original item model */
        public transient MinecraftModel fallback = MinecraftModel.NOT_SET;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            for (OverriddenModel override : overrides) {
                if (override.isMatching(item)) {
                    return override.getOverrideModels();
                }
            }
            return Collections.singletonList(fallback);
        }

        @Override
        public boolean hasValidModels() {
            if (fallback.hasValidModels()) {
                return true;
            }

            for (OverriddenModel override : overrides) {
                if (override.hasValidOverrideModels()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (OverriddenModel override : overrides) {
                chain.next(override).addModels(override.models);
            }
            chain.next(PredicateCondition.ALWAYS_TRUE_PREDICATE).addModel(fallback);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Predicate Overrides {");
            str.indent()
                    .append("\noverrides: [")
                    .appendWithIndent(ov_str -> ov_str.appendLines(overrides))
                    .append("\n]")
                    .append("\nfallback: ").append(fallback);
            str.append("\n}");
        }

        public static class OverriddenModel implements IndentedStringBuilder.AppendableToString, ItemModelOverride {
            public List<PredicateCondition<?>> predicate = Collections.emptyList();
            public List<MinecraftModel> models = MinecraftModel.NOT_SET_LIST;

            /** Set to the ItemStack that will display this overridden model. Set externally. */
            public @Nullable CommonItemStack itemStack = null;

            @Override
            public boolean isMatching(CommonItemStack item) {
                for (PredicateCondition<?> condition : predicate) {
                    if (!condition.isMatching(item)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean isMatchingAlways() {
                return predicate.isEmpty();
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                Optional<CommonItemStack> result = Optional.of(item);
                for (PredicateCondition<?> condition : predicate) {
                    result = condition.tryMakeMatching(result.get());
                    if (!result.isPresent()) {
                        break;
                    }
                }
                return result;
            }

            @Override
            public List<MinecraftModel> getOverrideModels() {
                return models;
            }

            @Override
            public Optional<CommonItemStack> getItemStack() {
                return Optional.ofNullable(itemStack);
            }

            @Override
            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Override {");
                str.indent()
                        .append("\npredicates: [")
                        .appendWithIndent(predicatesStr -> predicatesStr.appendLines(predicate))
                        .append("\n]")
                        .append("\nmodel: ").append(models.get(0)); // Always stores exactly one model
                str.append("\n}");
            }
        }

        public static class PredicateCondition<T> implements IndentedStringBuilder.AppendableToString, ItemModelPredicate {
            public ItemModelProperty.PredicateProperty<T> property;
            public @Nullable T value;

            @Override
            public boolean isMatching(CommonItemStack item) {
                return value != null && property.isMatchingPredicate(item, value);
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                return (value == null) ? Optional.empty() : property.tryApplyPredicate(item, value);
            }

            @Override
            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Predicate {");
                str.indent()
                        .append("\npredicate: ").append(property)
                        .append("\nvalue: ").append(value == null ? "<failed to parse>" : value);
                str.append("\n}");
            }
        }
    }

    public static class Condition extends ItemModel {
        public ItemModelProperty property;
        public ItemModel on_true;
        public ItemModel on_false;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            boolean isTrue = (property instanceof ItemModelProperty.BooleanProperty) &&
                    ((ItemModelProperty.BooleanProperty) property).testCondition(item);
            return (isTrue ? on_true : on_false).resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            return on_true.hasValidModels() || on_false.hasValidModels();
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            if (property instanceof ItemModelProperty.BooleanProperty) {
                ItemModelProperty.BooleanProperty bProp = (ItemModelProperty.BooleanProperty) property;
                on_true.populateModelChain(chain.next(bProp.asPredicate(true)));
                on_false.populateModelChain(chain.next(bProp.asPredicate(false)));
            } else {
                on_false.populateModelChain(chain);
            }
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Condition {");
            str.indent()
                    .append("\nproperty: ").append(property)
                    .append("\non_true: ").append(on_true)
                    .append("\non_false: ").append(on_false);
            str.append("\n}");
        }
    }

    public static class RangeDispatch extends ItemModel {
        public ItemModelProperty property;
        public List<Entry> entries;
        public ItemModel fallback;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            if (property instanceof ItemModelProperty.NumericProperty) {
                double propertyValue = ((ItemModelProperty.NumericProperty) property).getNumericValue(item);
                List<Entry> entries = this.entries;
                for (int i = entries.size() - 1; i >= 0; --i) {
                    Entry e = entries.get(i);
                    if (propertyValue >= e.minThreshold) {
                        return e.model.resolveModels(item);
                    }
                }
            }
            return fallback.resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            if (fallback.hasValidModels()) {
                return true;
            }

            for (Entry e : entries) {
                if (e.model.hasValidModels()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (Entry e : entries) {
                e.model.populateModelChain(chain.next(e));
            }
            fallback.populateModelChain(chain.next(ItemModelPredicate.ALWAYS_TRUE_PREDICATE));
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Range Dispatch {");
            IndentedStringBuilder ind = str.indent();
            ind.append("\nproperty: ").append(property);
            ind.append("\nentries: [");
            {
                IndentedStringBuilder entryStr = ind.indent();
                for (Entry e : entries) {
                    entryStr.append("\n{");
                    entryStr.indent()
                            .append("\nthreshold: ").append(e.minThreshold)
                            .append("\nmodel: ").append(e.model);
                    entryStr.append("\n}");
                }
            }
            ind.append("\n]");
            ind.append("\nfallback: ").append(fallback);
            str.append("\n}");
        }

        public static class Entry implements ItemModelPredicate, IndentedStringBuilder.AppendableToString {
            protected transient ItemModelProperty property = ItemModelProperty.NONE;
            @SerializedName("threshold")
            public double minThreshold = -Double.MAX_VALUE;
            public transient double maxThreshold = Double.MAX_VALUE;
            public ItemModel model = MinecraftModel.NOT_SET;

            @Override
            public boolean isMatching(CommonItemStack item) {
                if (!(property instanceof ItemModelProperty.NumericProperty)) {
                    return false;
                }

                double propertyValue = ((ItemModelProperty.NumericProperty) property).getNumericValue(item);
                return propertyValue >= minThreshold && propertyValue < maxThreshold;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                if (!(property instanceof ItemModelProperty.NumericProperty)) {
                    return Optional.empty();
                }

                return ((ItemModelProperty.NumericProperty) property).setNumericValue(item, minThreshold);
            }

            @Override
            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("RangeDispatch.Entry {");
                str.indent()
                        .append("\nproperty: ").append(property)
                        .append("\nminThreshold: ").append(minThreshold)
                        .append("\nmaxThreshold: ").append(maxThreshold)
                        .append("\nmodel: ").append(model);
                str.append("\n}");
            }
        }
    }

    public static class Composite extends ItemModel {
        public List<ItemModel> models = Collections.emptyList();

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return models.stream()
                    .flatMap(m -> m.resolveModels(item).stream())
                    .collect(Collectors.toList());
        }

        @Override
        public boolean hasValidModels() {
            for (ItemModel model : models) {
                if (model.hasValidModels()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (ItemModel model : models) {
                model.populateModelChain(chain);
            }
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Composite [");
            str.indent().appendLines(models);
            str.append("\n]");
        }
    }

    public static class Select extends ItemModel {
        public ItemModelProperty property;
        public List<Case> cases;
        public ItemModel fallback;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            for (Case c : cases) {
                for (Select.WhenValue whenValue : c.when) {
                    if (whenValue.isMatching(property, item)) {
                        return c.model.resolveModels(item);
                    }
                }
            }
            return fallback.resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            if (fallback.hasValidModels()) {
                return true;
            }

            for (Case c : cases) {
                if (c.model.hasValidModels()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (Case c : cases) {
                c.model.populateModelChain(chain.next(c));
            }
            fallback.populateModelChain(chain.next(ItemModelPredicate.ALWAYS_TRUE_PREDICATE));
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Select {");
            IndentedStringBuilder ind = str.indent();
            ind.append("\nproperty: ").append(property);
            ind.append("\ncases: [");
            {
                IndentedStringBuilder entryStr = ind.indent();
                for (Case c : cases) {
                    entryStr.append("\n{");
                    entryStr.indent()
                            .append("\nwhen: ").append(c.when)
                            .append("\nmodel: ").append(c.model);
                    entryStr.append("\n}");
                }
            }
            ind.append("\n]");
            ind.append("\nfallback: ").append(fallback);
            str.append("\n}");
        }

        /**
         * A value checked against the property for a case of the select.
         * For most properties this is just a String value. Sometimes has additional
         * properties for specialized cases.
         */
        public interface WhenValue {
            /**
             * Modifies an item so that this value is selected for the property specified
             *
             * @param property ItemModelProperty
             * @param item CommonItemStack
             * @return Updated item, or empty if the property is not supported or the value cannot be used
             */
            Optional<CommonItemStack> tryMakeMatching(ItemModelProperty property, CommonItemStack item);

            /**
             * Gets whether this condition value is true for the property and item specified
             *
             * @param property ItemModelProperty
             * @param item CommonItemStack
             * @return True if matching
             */
            boolean isMatching(ItemModelProperty property, CommonItemStack item);

            // Internal use...
            static WhenValue deserialize(JsonElement element) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    Map<String, String> dict = new LinkedHashMap<>();
                    for (String key : obj.keySet()) {
                        dict.put(key, obj.get(key).getAsString());
                    }
                    return new WhenDictValue(dict);
                } else {
                    return new WhenStringValue(element.getAsString());
                }
            }
        }

        /**
         * Single string case value. This is usually used.
         */
        public static final class WhenStringValue implements WhenValue {
            public final String value;

            public WhenStringValue(String value) {
                this.value = value;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(ItemModelProperty property, CommonItemStack item) {
                if (property instanceof ItemModelProperty.StringProperty) {
                    return ((ItemModelProperty.StringProperty) property).applyStringValue(item, value);
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public boolean isMatching(ItemModelProperty property, CommonItemStack item) {
                if (property instanceof ItemModelProperty.StringProperty) {
                    String propertyValue = ((ItemModelProperty.StringProperty) property).getStringValue(item);
                    return value.equals(propertyValue);
                } else {
                    return false;
                }
            }

            @Override
            public String toString() {
                return this.value;
            }
        }

        /**
         * A dictionary of key-value pairs for the case value. Used for the "component"
         * property.
         */
        public static final class WhenDictValue implements WhenValue {
            public final Map<String, String> dict;

            public WhenDictValue(Map<String, String> dict) {
                this.dict = dict;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(ItemModelProperty property, CommonItemStack item) {
                return Optional.empty(); //TODO: Implement?
            }

            @Override
            public boolean isMatching(ItemModelProperty property, CommonItemStack item) {
                return false; //TODO: Implement?
            }

            @Override
            public String toString() {
                return dict.toString();
            }
        }

        public static class Case implements ItemModelPredicate, IndentedStringBuilder.AppendableToString {
            protected transient ItemModelProperty property = ItemModelProperty.NONE;
            public List<WhenValue> when = Collections.emptyList(); // match-any
            public ItemModel model = MinecraftModel.NOT_SET;

            @Override
            public boolean isMatching(CommonItemStack item) {
                for (WhenValue whenValue : when) {
                    if (whenValue.isMatching(property, item)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                if (when.isEmpty()) {
                    return Optional.of(item); // Always matches I guess?
                } else {
                    return when.get(0).tryMakeMatching(property, item);
                }
            }

            @Override
            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Select.Case {");
                IndentedStringBuilder ind = str.indent();
                ind.append("\nproperty: ").append(property)
                        .append("\nwhen: [");
                ind.indent().appendLines(when);
                ind.append("\n]")
                        .append("\nmodel: ").append(model);
                str.append("\n}");
            }
        }
    }
}
