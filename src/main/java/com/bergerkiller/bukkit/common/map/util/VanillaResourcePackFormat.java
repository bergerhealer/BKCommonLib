package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.mountiplex.logic.TextValueSequence;

/**
 * Lists all resource pack <b>pack_format</b> values by the Minecraft version since which it was
 * introduced. See: <a href="https://minecraft.wiki/w/Pack_format">Minecraft wiki: pack_format</a>
 */
public final class VanillaResourcePackFormat {
    private final TextValueSequence minMCVersion;
    private final int pack_format;

    private static final VanillaResourcePackFormat[] VANILLA_PACK_FORMATS = new VanillaResourcePackFormat[]{
            create("1.6.1", 1),
            create("1.9", 2),
            create("1.11", 3),
            create("1.13", 4),
            create("1.15", 5),
            create("1.16.2", 6),
            create("1.17", 7),
            create("1.18", 8),
            create("1.19", 9),
            create("1.19.3", 12),
            create("1.19.4", 13),
            create("1.20", 15),
            create("1.20.2", 18),
            create("1.20.3", 22),
            create("1.20.5", 32),
            create("1.21", 34),
            create("1.21.2", 42),
            create("1.21.4", 46),
    };

    public static int getLatestPackFormat() {
        return VANILLA_PACK_FORMATS[VANILLA_PACK_FORMATS.length - 1].pack_format;
    }

    public static int getPackFormat(String mcVersion) {
        TextValueSequence mcVersionParsed = TextValueSequence.parse(mcVersion);
        for (int i = VANILLA_PACK_FORMATS.length-1; i >=0; --i) {
            VanillaResourcePackFormat format = VANILLA_PACK_FORMATS[i];
            if (TextValueSequence.evaluate(mcVersionParsed, ">=", format.minMCVersion)) {
                return format.pack_format;
            }
        }
        return 1;
    }

    private static VanillaResourcePackFormat create(String minMCVersion, int pack_format) {
        return new VanillaResourcePackFormat(minMCVersion, pack_format);
    }

    private VanillaResourcePackFormat(String minMCVersion, int pack_format) {
        this.minMCVersion = TextValueSequence.parse(minMCVersion);
        this.pack_format = pack_format;
    }
}
