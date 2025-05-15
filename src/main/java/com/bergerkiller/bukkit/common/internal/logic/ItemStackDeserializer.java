package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Function;

/**
 * Deserializes Bukkit or Paper NBT formatted ItemStack objects from a key-value map, with the added feature
 * of supporting configuration produced on mc versions newer than the one it is running on.<br>
 * <br>
 * This class also replaces ItemStack properties that were saved as raw maps with the serialized classes,
 * and double -> integer conversion, to add support for deserialization from JSON.
 */
public class ItemStackDeserializer implements Function<Map<String, Object>, ItemStack> {
    public static final ItemStackDeserializer INSTANCE = new ItemStackDeserializer();

    /**
     * Whether to enable parsing NBT into ItemStacks on Spigot server. If false, it
     * will convert it to Bukkit's format and parse that instead. This can be set to
     * false and all yaml tests can then be run to verify to-bukkit migration works.
     */
    private static final boolean ALLOW_PARSE_NBT_ON_SPIGOT = true;

    private final ItemStackDeserializerMigratorBukkit bukkitMigrator = new ItemStackDeserializerMigratorBukkit();
    private final ItemStackDeserializerMigratorNBT nbtMigrator = new ItemStackDeserializerMigratorNBT();

    /**
     * Whether to parse NBT tags into ItemStacks directly. Paper natively supports this, but for
     * Spigot we have our own datafixer migration / ItemStack parsing logic to deal with it.
     * We only want to do this if the server is version 1.21.5 or newer. Paper won't serialize
     * items in this format on 1.21.4 and before so there is no point in parsing those formats.
     * And since data components are kind of recent as well, there is no point in maintaining
     * migrations for it.<br>
     * <br>
     * Instead, at the 1.21.5 data version line we just convert the NBT directly into
     * Bukkit/Spigot style ItemMeta. We lose a bunch of information this way, but at least some
     * important details for item models will remain functional.
     */
    private final boolean canParseNBT;

    private ItemStackDeserializer() {
        // Only parse NBT on 1.21.5 and later
        this.canParseNBT = nbtMigrator.getCurrentDataVersion() >= 4325 &&
                (ALLOW_PARSE_NBT_ON_SPIGOT || CommonBootstrap.isPaperServer());
    }

    /**
     * Gets the data migrator used for Bukkit/Spigot formatted ItemStacks
     *
     * @return Bukkit Migrator
     */
    public ItemStackDeserializerMigratorBukkit getBukkitMigrator() {
        return bukkitMigrator;
    }

    /**
     * Gets the data migrator used for Paper NBT formatted ItemStacks
     *
     * @return Paper NBT Migrator
     */
    public ItemStackDeserializerMigratorNBT getNBTMigrator() {
        return nbtMigrator;
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        boolean isPaperNBTFormat = args.containsKey("schema_version");
        if (isPaperNBTFormat) {
            if (canParseNBT) {
                return nbtMigrator.apply(args);
            } else {
                args = nbtMigrator.toBukkitEncoding(args);
            }
        }

        return bukkitMigrator.apply(args);
    }
}
