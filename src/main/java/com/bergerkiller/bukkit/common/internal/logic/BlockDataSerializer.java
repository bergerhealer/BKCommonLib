package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

/**
 * Stringifies BlockData information so that it can be saved as text.
 * Also handles the deserialization, going back from text to BlockData.
 * This uses the same syntax as the vanilla minecraft material argument supports.
 */
public abstract class BlockDataSerializer implements LazyInitializedObject, LibraryComponent {
    public static final BlockDataSerializer INSTANCE = LibraryComponentSelector.forModule(BlockDataSerializer.class)
            .runFirst(CommonBootstrap::initServer)
            .addVersionOption(null, "1.12.2", BlockDataSerializer_1_8_to_1_12_2::new)
            .addVersionOption("1.13", "1.18.2", BlockDataSerializer_1_13_to_1_18_2::new)
            .addVersionOption("1.19", null, BlockDataSerializer_1_19::new)
            .update();

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
