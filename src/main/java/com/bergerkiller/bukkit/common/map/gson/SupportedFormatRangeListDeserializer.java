package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * As per Mojang docs, turns 49, [49], [49, 61] and {min_inclusive: 49, max_inclusive: 61} into a valid
 * List of SupportedFormatRange elements. For use in parsing pack.mcmeta.
 */
class SupportedFormatRangeListDeserializer implements JsonDeserializer<List<MapResourcePack.Metadata.SupportedFormatRange>> {

    @Override
    public List<MapResourcePack.Metadata.SupportedFormatRange> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        List<JsonElement> elements;
        if (jsonElement.isJsonArray()) {
            elements = jsonElement.getAsJsonArray().asList();
        } else {
            elements = Collections.singletonList(jsonElement);
        }

        // Decode each value into a SupportedFormatRange
        List<MapResourcePack.Metadata.SupportedFormatRange> ranges = new ArrayList<>(elements.size());
        for (JsonElement element : elements) {
            if (element.isJsonObject()) {
                // min_inclusive / max_inclusive
                JsonObject obj = element.getAsJsonObject();
                JsonElement min_inclusive_el = obj.get("min_inclusive");
                JsonElement max_inclusive_el = obj.get("max_inclusive");
                if (isNumberPrimitive(min_inclusive_el) && isNumberPrimitive(max_inclusive_el)) {
                    ranges.add(MapResourcePack.Metadata.SupportedFormatRange.of(
                            min_inclusive_el.getAsInt(),
                            max_inclusive_el.getAsInt()));
                }
            } else if (isNumberPrimitive(element)) {
                ranges.add(MapResourcePack.Metadata.SupportedFormatRange.of(
                        element.getAsInt()));
            }
        }
        return Collections.unmodifiableList(ranges);
    }

    private static boolean isNumberPrimitive(JsonElement element) {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
}
