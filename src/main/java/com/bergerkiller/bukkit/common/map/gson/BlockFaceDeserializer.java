package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.Type;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class BlockFaceDeserializer implements JsonDeserializer<BlockFace> {
    @Override
    public BlockFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ParseUtil.parseEnum(BlockFace.class, jsonElement.getAsString(), null);
    }
}
