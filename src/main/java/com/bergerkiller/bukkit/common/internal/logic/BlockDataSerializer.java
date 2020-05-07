package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Stringifies BlockData information so that it can be saved as text.
 * Also handles the deserialization, going back from text to BlockData.
 * This uses the same syntax as the vanilla minecraft material argument supports.
 */
public abstract class BlockDataSerializer {
    public static final BlockDataSerializer INSTANCE;

    static {
        CommonBootstrap.initServer();
        if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
            INSTANCE = new BlockDataSerializer_1_13();
        } else {
            INSTANCE = new BlockDataSerializer_1_8_to_1_12_2();
        }
    }

    /**
     * Serializes BlockData to a String
     * 
     * @param blockData
     * @return Stringified BlockData
     */
    public abstract String serialize(BlockData blockData);

    /**
     * Deserializes BlockData from a String.
     * Returns null if the input could not be parsed.
     * 
     * @param text Input text to parse (output of {@link #serialize(BlockData)}")
     * @return Deserialized BlockData, null if input could not be parsed
     */
    public abstract BlockData deserialize(String text);
}
