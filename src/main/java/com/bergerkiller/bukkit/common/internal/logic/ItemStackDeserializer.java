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

    private final ItemStackDeserializerMigratorBukkit bukkitMigrator = new ItemStackDeserializerMigratorBukkit();
    private final ItemStackDeserializerMigratorPaperNBT paperNBTMigrator = new ItemStackDeserializerMigratorPaperNBT();
    private final boolean mustTransformPaperNBT;

    private ItemStackDeserializer() {
        this.mustTransformPaperNBT = (bukkitMigrator.getCurrentDataVersion() < 4325 || !CommonBootstrap.isPaperServer());
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
    public ItemStackDeserializerMigratorPaperNBT getPaperNBTMigrator() {
        return paperNBTMigrator;
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        boolean isPaperNBTFormat = args.containsKey("schema_version");

        // If data version is >= 1.21.5 AND it uses paper's NBT compound encoding,
        // AND this server is not a paper server / version before 1.21.5, then
        // undo this compound encoding and change it back to Spigot's encoding strategy.
        if (isPaperNBTFormat && mustTransformPaperNBT) {
            args = paperNBTMigrator.toBukkitEncoding(args);
            isPaperNBTFormat = false;
        }

        if (isPaperNBTFormat) {
            return paperNBTMigrator.apply(args);
        } else {
            return bukkitMigrator.apply(args);
        }
    }
}
