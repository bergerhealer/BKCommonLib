package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

/**
 * Represents the block and sky emission values of a Display Entity.
 */
public final class Brightness {
    private final int block, sky, packed;
    /** No light is emitted at all */
    public static final Brightness NONE = new Brightness(0, 0);
    /** Full light levels, both block and sky light, are emitted */
    public static final Brightness FULL_ALL = new Brightness(15, 15);
    /** Full block light levels are emitted */
    public static final Brightness FULL_BLOCK = new Brightness(15, 0);
    /** Full sky light levels are emitted */
    public static final Brightness FULL_SKY = new Brightness(0, 15);

    private Brightness(int block, int sky) {
        this.block = block;
        this.sky = sky;
        this.packed = (block << 4 | sky << 20);
    }

    /**
     * Gets the amount of block light level emitted with this brightness
     *
     * @return emitted block light
     */
    public int blockLight() {
        return block;
    }

    /**
     * Gets the amount of sky light level emitted with this brightness
     *
     * @return emitted sky light
     */
    public int skyLight() {
        return sky;
    }

    /**
     * Returns a new Brightness with the emitted sky light updated
     *
     * @param skyLightLevel Sky light level [0 .. 15]
     * @return New Brightness
     */
    public Brightness withSkyLight(int skyLightLevel) {
        return new Brightness(block, skyLightLevel);
    }

    /**
     * Returns a new Brightness with the emitted block light updated
     *
     * @param blockLightLevel Block light level [0 .. 15]
     * @return New Brightness
     */
    public Brightness withBlockLight(int blockLightLevel) {
        return new Brightness(blockLightLevel, sky);
    }

    /**
     * Gets the serialized, packed value of these brightness levels
     *
     * @param brightness Brightness
     * @return packed value
     */
    @ConverterMethod
    public static int pack(Brightness brightness) {
        return brightness.packed;
    }

    /**
     * Decodes brightness previously {@link #pack(Brightness) packed}
     *
     * @param packed Packed value
     * @return Decoded Brightness
     */
    @ConverterMethod
    public static Brightness unpack(int packed) {
        int j = packed >> 4 & '\uffff';
        int k = packed >> 20 & '\uffff';

        return new Brightness(j, k);
    }

    /**
     * Emits both block and sky light
     *
     * @param blockLightLevel Block light level [0 .. 15]
     * @param skyLightlevel Sky light level [0 .. 15]
     * @return Brightness
     */
    public static Brightness blockAndSkyLight(int blockLightLevel, int skyLightlevel) {
        return new Brightness(blockLightLevel, skyLightlevel);
    }

    /**
     * Emits block light
     *
     * @param blockLightLevel Block light level [0 .. 15]
     * @return Brightness
     */
    public static Brightness blockLight(int blockLightLevel) {
        return new Brightness(blockLightLevel, 0);
    }

    /**
     * Emits sky light
     *
     * @param skyLightlevel Sky light level [0 .. 15]
     * @return Brightness
     */
    public static Brightness skyLight(int skyLightlevel) {
        return new Brightness(0, skyLightlevel);
    }
}
