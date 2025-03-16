package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bukkit.block.BlockFace;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
}
