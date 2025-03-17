package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used on Minecraft 1.21.4 and later to represent the "item model" graph. This sets up
 * the conditions for showing particular models for items.<br>
 * <br>
 * The ItemModel represents a single node of this graph.
 */
public abstract class ItemModel implements IndentedStringBuilder.AppendableToString {

    @Override
    public final String toString() {
        return IndentedStringBuilder.toString(this);
    }

    public static class ItemModelDeserializer implements JsonDeserializer<ItemModel> {
        private final Map<String, JsonDeserializer<? extends ItemModel>> byName = new HashMap<>();

        public ItemModelDeserializer() {
            registerDeserializerToType("model", MinecraftModel.class);
            registerDeserializerToType("condition", Condition.class);
            registerDeserializerToType("range_dispatch", RangeDispatch.class);
            registerDeserializerToType("select", Select.class);
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
            condition.property = ItemModelProperty.get(obj.get("property").getAsString(), obj);
            condition.on_true = jsonDeserializationContext.deserialize(obj.get("on_true"), ItemModel.class);
            condition.on_false = jsonDeserializationContext.deserialize(obj.get("on_false"), ItemModel.class);
            return condition;
        });

        gsonBuilder.registerTypeAdapter(RangeDispatch.class, (JsonDeserializer<RangeDispatch>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            RangeDispatch dispatch = new RangeDispatch();

            dispatch.property = ItemModelProperty.get(obj.get("property").getAsString(), obj);

            dispatch.fallback = obj.has("fallback")
                    ? jsonDeserializationContext.deserialize(obj.get("fallback"), ItemModel.class)
                    : MinecraftModel.NOT_SET;

            if (obj.has("entries")) {
                JsonArray arr = obj.get("entries").getAsJsonArray();
                int size = arr.size();
                dispatch.entries = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    dispatch.entries.add(jsonDeserializationContext.deserialize(arr.get(i), RangeDispatch.Entry.class));
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
                    ArrayList<String> whenArrayValues = new ArrayList<>(whenCount);
                    for (int i = 0; i < whenCount; i++) {
                        whenArrayValues.add(whenArray.get(i).getAsString());
                    }
                    c.when = Collections.unmodifiableList(whenArrayValues);
                } else {
                    c.when = Collections.singletonList(whenElement.getAsString());
                }
            } else {
                c.when = Collections.emptyList();
            }

            c.model = jsonDeserializationContext.deserialize(obj.get("model"), ItemModel.class);

            return c;
        });

        gsonBuilder.registerTypeAdapter(Select.class, (JsonDeserializer<Select>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Select select = new Select();

            select.property = ItemModelProperty.get(obj.get("property").getAsString(), obj);

            select.fallback = obj.has("fallback")
                    ? jsonDeserializationContext.deserialize(obj.get("fallback"), ItemModel.class)
                    : MinecraftModel.NOT_SET;

            if (obj.has("cases")) {
                JsonArray arr = obj.get("cases").getAsJsonArray();
                int size = arr.size();
                select.cases = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    select.cases.add(jsonDeserializationContext.deserialize(arr.get(i), Select.Case.class));
                }
                select.cases = Collections.unmodifiableList(select.cases);
            } else {
                select.cases = Collections.emptyList();
            }

            return select;
        });
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
    }

    /**
     * The root document of the item model json file
     */
    public static class Root {
        public ItemModel model;
        public boolean hand_animation_on_swap = true;

        @Override
        public String toString() {
            return model.toString();
        }
    }

    public static class MinecraftModel extends ItemModel {
        /** Placeholder model value when a ModelInfo field is not set, such as with range_dispatch fallback */
        public static final MinecraftModel NOT_SET = new MinecraftModel();
        static {
            NOT_SET.model = "generic:missing";
        }

        public String model;

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Model { name: " + model + " }");
        }
    }

    public static class Condition extends ItemModel {
        public ItemModelProperty property;
        public ItemModel on_true;
        public ItemModel on_false;

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Condition {");
            str.indent(2)
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

        public static class Entry {
            public double threshold;
            public ItemModel model;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Range Dispatch {");
            IndentedStringBuilder ind = str.indent(2);
            ind.append("\nproperty: ").append(property);
            ind.append("\nentries: [");
            {
                IndentedStringBuilder entryStr = ind.indent(2);
                for (Entry e : entries) {
                    entryStr.append("\n{");
                    entryStr.indent(2)
                            .append("\nthreshold: ").append(e.threshold)
                            .append("\nmodel: ").append(e.model);
                    entryStr.append("\n}");
                }
            }
            ind.append("\n]");
            ind.append("\nfallback: ").append(fallback);
            str.append("\n}");
        }
    }

    public static class Composite extends ItemModel {
        public List<Model> models;

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Composite {}");
        }
    }

    public static class Select extends ItemModel {
        public ItemModelProperty property;
        public List<Case> cases;
        public ItemModel fallback;

        public static class Case {
            public List<String> when; // singleton list for one value
            public ItemModel model;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Select {");
            IndentedStringBuilder ind = str.indent(2);
            ind.append("\nproperty: ").append(property);
            ind.append("\ncases: [");
            {
                IndentedStringBuilder entryStr = ind.indent(2);
                for (Case c : cases) {
                    entryStr.append("\n{");
                    entryStr.indent(2)
                            .append("\nwhen: ").append(c.when)
                            .append("\nmodel: ").append(c.model);
                    entryStr.append("\n}");
                }
            }
            ind.append("\n]");
            ind.append("\nfallback: ").append(fallback);
            str.append("\n}");
        }
    }
}
