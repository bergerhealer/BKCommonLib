package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.gson.types.ResourcePackDescription;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.leangen.geantyref.TypeToken;
import org.bukkit.block.BlockFace;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Manages the deserialization of resource pack contents. Mostly used for
 * with GSON to deserialize JSON file contents.
 */
public final class MapResourcePackDeserializer {
    public final Gson gson;

    public static MapResourcePackDeserializer create() {
        return new MapResourcePackDeserializer();
    }

    @SuppressWarnings("rawtypes")
    private MapResourcePackDeserializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Vector3.class, new Vector3Deserializer());
        gsonBuilder.registerTypeAdapter(BlockFace.class, new BlockFaceDeserializer());
        gsonBuilder.registerTypeAdapter(BlockModelState.VariantList.class, new VariantListDeserializer());
        gsonBuilder.registerTypeAdapter(BlockModelState.Condition.class, new ConditionalDeserializer());
        gsonBuilder.registerTypeAdapter(List.class, new NonNullListDeserializer());
        gsonBuilder.registerTypeAdapter(MapResourcePack.PackVersion.class, new PackVersionDeserializer());
        gsonBuilder.registerTypeAdapter(new TypeToken<List<MapResourcePack.PackVersionRange>>() {}.getType(),
                new PackVersionRangeListDeserializer());
        gsonBuilder.registerTypeAdapter(ResourcePackDescription.class, new ResourcePackDescription.Deserializer());

        ItemModel.registerDeserializers(gsonBuilder);
        this.gson = gsonBuilder.create();
    }

    public <T> T readGsonObject(Class<T> objectType, InputStream inputStream, String optPath) {
        if (inputStream == null) {
            return null;
        }
        try {
            try {
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                T result = this.gson.fromJson(reader, objectType);
                if (result == null) {
                    String s = (optPath == null) ? "" : (" at " + optPath);
                    throw new IOException("Failed to parse JSON for " + objectType.getSimpleName() + s);
                }
                return result;
            } finally {
                inputStream.close();
            }
        } catch (JsonSyntaxException ex) {
            String s = (optPath == null) ? "" : (" at " + optPath);
            String msg = ex.getMessage();
            msg = StringUtil.trimStart(msg, "com.bergerkiller.bukkit.common.dep.gson.stream.MalformedJsonException: ");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to parse GSON for " + objectType.getSimpleName() +
                    s + ": " + msg);
        } catch (IOException ex) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Unhandled IO Exception", ex);
        }
        return null;
    }

    /**
     * Attempts to parse a JSON value into an integer. Can parse numbers and string versions
     * of these.
     *
     * @param jsonElement JSON Element
     * @return Parsed integer value if successful, or an empty optional otherwise
     */
    public static Optional<Integer> tryParseAsInt(JsonElement jsonElement) {
        try {
            return Optional.of(jsonElement.getAsInt());
        } catch (UnsupportedOperationException | NumberFormatException | IllegalStateException ex) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to parse a JSON value into a double. Can parse numbers and string versions
     * of these.
     *
     * @param jsonElement JSON Element
     * @return Parsed double value if successful, or an empty optional otherwise
     */
    public static Optional<Double> tryParseAsDouble(JsonElement jsonElement) {
        try {
            return Optional.of(jsonElement.getAsDouble());
        } catch (UnsupportedOperationException | NumberFormatException | IllegalStateException ex) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to parse a JSON value into a boolean. Can parse numbers (0 and 1), booleans
     * (true and false), string versions of these and single-element arrays.
     *
     * @param jsonElement JSON Element
     * @return Parsed boolean value if successful, or an empty optional otherwise
     */
    public static Optional<Boolean> tryParseAsBoolean(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return Optional.empty();
        }

        // Unwrap single-element arrays
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            if (array.size() == 1) {
                jsonElement = array.get(0);
            } else {
                return Optional.empty();
            }
        }

        // Parse strings, booleans or numbers into a boolean
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return Optional.of(jsonElement.getAsBoolean());
            } else if (primitive.isNumber()) {
                return Optional.of(jsonElement.getAsInt() != 0);
            } else if (primitive.isString()) {
                String value = jsonElement.getAsString().toLowerCase();
                switch (value) {
                    case "1":
                    case "true":
                        return Optional.of(Boolean.TRUE);
                    case "0":
                    case "false":
                        return Optional.of(Boolean.FALSE);
                    default:
                        return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }
}
