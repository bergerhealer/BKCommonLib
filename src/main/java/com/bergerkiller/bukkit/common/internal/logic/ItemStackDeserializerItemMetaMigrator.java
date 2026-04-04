package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.PlayerProfile_1_8_to_1_18;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.google.common.collect.MapMaker;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Handles the migration of ItemMeta before it is further deserialized by the
 * built-in deserializer.
 */
public class ItemStackDeserializerItemMetaMigrator extends ItemStackDeserializerUtils implements Function<Map<String, Object>, ItemMeta> {
    private final ItemStackDeserializerMigratorBukkit itemStackMigrator;
    private final List<Migrator> migrators = new ArrayList<>();

    private static final boolean IS_ENTITY_TAG_META_SUPPORTED = CommonBootstrap.evaluateMCVersion(">=", "1.16.2");

    /**
     * Since Minecraft 1.20.5 decoding a player profile into a skull item is broken when decoding
     * legacy "internal" encoded metadata. The unique id information is lost among other things.
     * For this version, the data is migrated to a newer data version before deserializing.
     */
    private static final boolean IS_LEGACY_SKULL_DECODING_BUSTED = CommonBootstrap.evaluateMCVersion(">=", "1.20.5");

    /**
     * Before Minecraft 1.16 the game profile was encoded to NBT with the user uuid encoded as a String tag.
     * After, it was an IntArray tag with 4 elements.
     */
    private static final boolean IS_SKULL_PROFILE_ID_STRING = CommonBootstrap.evaluateMCVersion("<", "1.16");

    /**
     * Since Minecraft 1.16 the game profile user unique id was encoded as an int[] for the first time.
     * A server bug existed between Minecraft 1.16 and 1.16.3 where the previous uuid string was incorrectly
     * decoded, and so the game profile user uuid got mangled.
     * We add an extra logic to re-encode the uuid string as the correct 4 ints to stop this bug from happening.
     */
    private static final boolean IS_SKULL_PROFILE_STRING_ID_MANGLED = CommonBootstrap.evaluateMCVersion(">=", "1.16") && CommonBootstrap.evaluateMCVersion("<=", "1.16.3");

    public ItemStackDeserializerItemMetaMigrator(ItemStackDeserializerMigratorBukkit itemStackMigrator) {
        this.itemStackMigrator = itemStackMigrator;

        // Register all migrators to run before actual deserialization occurs

        // If meta-type is ENTITY_TAG but this is not supported, switch it UNSPECIFIC to still allow it to work
        // This preserves the original NBT but just falls back to the default CraftMetaItem type
        if (!IS_ENTITY_TAG_META_SUPPORTED) {
            migrators.add((mapping, metaType) -> {
                if ("ENTITY_TAG".equals(metaType)) {
                    mapping.put("meta-type", "UNSPECIFIC");
                }
            });
        }

        // If meta-type is SKULL and a skull-owner is set that is a PlayerProfile, but this isn't supported yet
        if (!CommonCapabilities.HAS_BUKKIT_PLAYER_PROFILE) {
            migrators.add((mapping, metaType) -> {
                if (!"SKULL".equals(metaType)) {
                    return;
                }

                Object skullOwnerRaw = mapping.get("skull-owner");
                if (!(skullOwnerRaw instanceof PlayerProfile_1_8_to_1_18)) {
                    return;
                }

                Map<String, Object> skullOwnerMeta = ((PlayerProfile_1_8_to_1_18) skullOwnerRaw).meta;

                /*
                Convert:
                {
                  ===PlayerProfile
                  uniqueId=04049c90-d3e9-4621-9caf-0000aaa58540,
                  name=HeadDatabase,
                  properties=[
                    {
                      name=textures,
                      value=eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0=
                    }
                  ]
                }

                To:
                TagCompound: 1 entries {
                  SkullProfile = TagCompound: 3 entries {
                    Id = int[]: [67411088, -739686879, -1666252800, -1431993024]
                    Properties = TagCompound: 1 entries {
                      textures = TagList: 1 entries [
                        TagCompound: 1 entries {
                          Value = String: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0=
                        }
                      ]
                    }
                    Name = String: HeadDatabase
                  }
                }

                And encode as "internal" string string for the ItemMeta metadata.
                The NBT Name property is put as the new skull-owner value.
                 */

                CommonTagCompound skullProfileNbt = new CommonTagCompound();

                // uniqueId
                {
                    String uniqueIdStr = LogicUtil.tryCast(skullOwnerMeta.get("uniqueId"), String.class);
                    if (uniqueIdStr != null) {
                        try {
                            UUID uuid = UUID.fromString(uniqueIdStr);
                            long most = uuid.getMostSignificantBits();
                            long least = uuid.getLeastSignificantBits();
                            skullProfileNbt.putValue("Id", new int[] {
                                    (int) (most >> 32),
                                    (int) (most & 0xFFFFFFFFL),
                                    (int) (least >> 32),
                                    (int) (least & 0xFFFFFFFFL)
                            });
                        } catch (IllegalArgumentException ex) {
                            // Invalid UUID string, ignore
                        }
                    }
                }

                // name
                {
                    String name = LogicUtil.tryCast(skullOwnerMeta.get("name"), String.class);
                    if (name != null) {
                        skullProfileNbt.putValue("Name", name);
                        mapping.put("skull-owner", name);
                    } else {
                        mapping.remove("skull-owner");
                    }
                }

                // properties
                {
                    Object propertiesRaw = skullOwnerMeta.get("properties");
                    if (propertiesRaw instanceof List) {
                        List<?> propertiesList = (List<?>) propertiesRaw;
                        CommonTagCompound propertiesNbt = new CommonTagCompound();
                        for (Object propertyObj : propertiesList) {
                            if (propertyObj instanceof Map) {
                                Map<String, Object> propertyMap = (Map<String, Object>) propertyObj;
                                String propName = LogicUtil.tryCast(propertyMap.get("name"), String.class);
                                if (propName != null) {
                                    Object propValue = propertyMap.get("value");
                                    if (propValue instanceof String) {
                                        CommonTagCompound propNbt = new CommonTagCompound();
                                        propNbt.putValue("Value", (String) propValue);

                                        CommonTagList propValues = propertiesNbt.get(propName, CommonTagList.class);
                                        if (propValues != null) {
                                            propValues.add(propNbt);
                                        } else {
                                            propValues = new CommonTagList();
                                            propValues.add(propNbt);
                                            propertiesNbt.put(propName, propValues);
                                        }
                                    }
                                }
                            }
                        }
                        skullProfileNbt.put("Properties", propertiesNbt);
                    }
                }

                CommonTagCompound internalNBT = new CommonTagCompound();
                internalNBT.put("SkullProfile", skullProfileNbt);
                String internalStr = internalNBT.toBase64String();

                mapping.put("v", 2860); // Mark this as 1.18 encoded
                mapping.put("internal", internalStr);
            });
        }

        // If meta-type is SKULL and data version is pre-1.18.1 (uses legacy skull owner format), then migrate
        // it to the newer PlayerProfile format on 1.20.5. Because there, decoding the legacy "internal"
        // string is busted. Basically the same logic as above but in reverse.
        if (IS_LEGACY_SKULL_DECODING_BUSTED) {
            migrators.add((mapping, metaType) -> {
                if (!"SKULL".equals(metaType)) {
                    return;
                }

                // Skull owner must either be a String or not set at all
                Object skullOwnerRaw = mapping.get("skull-owner");
                if (skullOwnerRaw != null && !(skullOwnerRaw instanceof String)) {
                    return;
                }

                CommonTagCompound internalNbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(mapping.get("internal"), String.class));
                if (internalNbt == null) {
                    return; // No internal data, cannot run migration
                }

                /*
                Convert:
                TagCompound: 1 entries {
                  SkullProfile = TagCompound: 3 entries {
                    Id = int[]: [67411088, -739686879, -1666252800, -1431993024]
                    Properties = TagCompound: 1 entries {
                      textures = TagList: 1 entries [
                        TagCompound: 1 entries {
                          Value = String: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0=
                        }
                      ]
                    }
                    Name = String: HeadDatabase
                  }
                }

                To:
                {
                  ===PlayerProfile
                  uniqueId=04049c90-d3e9-4621-9caf-0000aaa58540,
                  name=HeadDatabase,
                  properties=[
                    {
                      name=textures,
                      value=eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0=
                    }
                  ]
                }

                And then pass that yaml to the standard PlayerProfile deserializer before storing it as the new skull-owner value
                 */

                CommonTagCompound skullProfileNbt = internalNbt.get("SkullProfile", CommonTagCompound.class);
                if (skullProfileNbt == null) {
                    return; // No profile stored
                }

                Map<String, Object> skullOwnerMeta = new java.util.HashMap<>();

                // uniqueId
                {
                    Object idValue = skullProfileNbt.getValue("Id");
                    if (idValue instanceof String) {
                        skullOwnerMeta.put("uniqueId", idValue);
                    } else if (idValue instanceof int[] && ((int[]) idValue).length == 4) {
                        int[] idInts = (int[]) idValue;
                        long most = ((long) idInts[0] << 32) | (idInts[1] & 0xFFFFFFFFL);
                        long least = ((long) idInts[2] << 32) | (idInts[3] & 0xFFFFFFFFL);
                        UUID uuid = new UUID(most, least);
                        skullOwnerMeta.put("uniqueId", uuid.toString());
                    }
                }

                // name
                {
                    String name = skullProfileNbt.getValue("Name", String.class);
                    if (name != null) {
                        skullOwnerMeta.put("name", name);
                    }
                }

                // properties
                {
                    CommonTagCompound propertiesNbt = skullProfileNbt.get("Properties", CommonTagCompound.class);
                    if (propertiesNbt != null) {
                        List<Map<String, Object>> propertiesList = new ArrayList<>();
                        for (String propName : propertiesNbt.keySet()) {
                            CommonTagList propValues = propertiesNbt.get(propName, CommonTagList.class);
                            if (propValues != null) {
                                for (int i = 0; i < propValues.size(); i++) {
                                    CommonTagCompound propNbt = propValues.getValue(i, CommonTagCompound.class);
                                    if (propNbt != null) {
                                        String propValue = propNbt.getValue("Value", String.class);
                                        if (propValue != null) {
                                            Map<String, Object> propertyMap = new java.util.HashMap<>();
                                            propertyMap.put("name", propName);
                                            propertyMap.put("value", propValue);
                                            propertiesList.add(propertyMap);
                                        }
                                    }
                                }
                            }
                        }
                        skullOwnerMeta.put("properties", propertiesList);
                    }
                }

                // Deserialize into skull owner and store it
                mapping.put("skull-owner", deserializeSkullOwner(skullOwnerMeta));
                mapping.remove("internal");
            });
        }

        // If only String tag for game profile id is supported, migrate an Int Array tag to a String tag for skull owner meta
        // Technically the older code also supported int[] but it corrupted the unique ID so we cannot use it.
        if (IS_SKULL_PROFILE_ID_STRING) {
            migrators.add((mapping, metaType) -> {
                if (!"SKULL".equals(metaType)) {
                    return;
                }

                CommonTagCompound nbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(mapping.get("internal"), String.class));
                if (nbt != null) {
                    CommonTagCompound skullProfileNbt = nbt.get("SkullProfile", CommonTagCompound.class);
                    if (skullProfileNbt != null) {
                        Object idRaw = skullProfileNbt.getValue("Id");
                        if (idRaw instanceof int[]) {
                            int[] idInts = (int[]) idRaw;
                            if (idInts.length == 4) {
                                long most = ((long) idInts[0] << 32) | (idInts[1] & 0xFFFFFFFFL);
                                long least = ((long) idInts[2] << 32) | (idInts[3] & 0xFFFFFFFFL);
                                UUID uuid = new UUID(most, least);
                                skullProfileNbt.putValue("Id", uuid.toString());
                                mapping.put("internal", nbt.toBase64String());
                                System.out.println("STORED AS ID STRING: " + nbt);
                            }
                        }
                    }
                }
            });
        }

        // Rewrite the UUID String as the correct int[] format to prevent the mangled UUID bug on 1.16
        if (IS_SKULL_PROFILE_STRING_ID_MANGLED) {
            migrators.add((mapping, metaType) -> {
                if (!"SKULL".equals(metaType)) {
                    return;
                }

                CommonTagCompound nbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(mapping.get("internal"), String.class));
                if (nbt == null) {
                    return;
                }

                CommonTagCompound skullProfileNbt = nbt.get("SkullProfile", CommonTagCompound.class);
                if (skullProfileNbt == null) {
                    return;
                }

                Object idRaw = skullProfileNbt.getValue("Id");
                if (idRaw instanceof String) {
                    try {
                        UUID uuid = UUID.fromString((String) idRaw);
                        long most = uuid.getMostSignificantBits();
                        long least = uuid.getLeastSignificantBits();
                        skullProfileNbt.putValue("Id", new int[] {
                                (int) (most >> 32),
                                (int) (most & 0xFFFFFFFFL),
                                (int) (least >> 32),
                                (int) (least & 0xFFFFFFFFL)
                        });
                        mapping.put("internal", nbt.toBase64String());
                    } catch (IllegalArgumentException ex) { /* ignore */ }
                }
            });
        }
    }

    /** Caches the input args to ItemMeta deserialize(), mapped to the object it produced */
    private final Map<ItemMeta, Map<String, Object>> itemMetaToArgs = new MapMaker().weakKeys().concurrencyLevel(4).makeMap();

    /**
     * ItemMeta de-serializer that migrates double to integer where required for GSON decoding.<br>
     * <br>
     * On versions before 1.13 it also stores some original Map contents that created an
     * ItemMeta so that it can be restored when deserializing the ItemStack later. This migrates
     * the "Damage" value to the ItemStack's "damage" field.
     *
     * @param mapping Mapping
     */
    @Override
    public ItemMeta apply(Map<String, Object> mapping) {
        // Migrate double -> integer, this is required for loading items from GSON

        // Custom model data can be of the format of a dict when it stores the different types of fields.
        // Or it can be a single number, which translates to just a float[] {number}
        // Fix this format. This same logic is also used for the Paper NBT migrator
        // On old versions of the server that does not support custom model data, decode only the floats as a number
        {
            Object value = mapping.get("custom-model-data");
            if (value instanceof Number) {
                mapping.put("custom-model-data", ((Number) value).intValue());
            } else if (value instanceof Map) {
                Map<String, Object> cmdValues = (Map<String, Object>) value;
                if (CraftItemStackHandle.T.deserializeCustomModelData.isAvailable()) {
                    mapping.put("custom-model-data", deserializeCustomModelData(cmdValues));
                } else {
                    Object rawFloats = cmdValues.get("floats");
                    if (rawFloats instanceof List) {
                        List<?> floats = (List<?>) rawFloats;
                        if (!floats.isEmpty() && floats.get(0) instanceof Number) {
                            mapping.put("custom-model-data", ((Number) floats.get(0)).intValue());
                        } else {
                            mapping.remove("custom-model-data");
                        }
                    } else {
                        mapping.remove("custom-model-data");
                    }
                }
            }
        }

        // Ensure raw fields are properly decoded before decoding the actual ItemMeta itself
        // This is important when deserializing from json
        convertNumberToIntegerInMap(mapping, "repair-cost");
        convertNumberToIntegerInMap(mapping, "Damage");
        convertNumberToIntegerInMap(mapping, "max-damage");
        convertNumberToIntegerInMap(mapping, "max-stack-size");
        convertNumberToIntegerInMap(mapping, "generation");
        convertNumberToIntegerInMap(mapping, "power");
        convertNumberToIntegerInMap(mapping, "map-id");
        convertNumberToIntegerInMap(mapping, "fish-variant");
        convertNumberToIntegerInMapValues(mapping, "enchants");
        replaceMapInMap(mapping, "color", ItemStackDeserializerItemMetaMigrator::deserializeColor);
        replaceMapInMap(mapping, "display-map-color", ItemStackDeserializerItemMetaMigrator::deserializeColor);
        replaceMapInMap(mapping, "custom-color", ItemStackDeserializerItemMetaMigrator::deserializeColor);
        replaceMapInMap(mapping, "firework-effect", ItemStackDeserializerItemMetaMigrator::deserializeFireworkEffect);
        replaceMapInMap(mapping, "skull-owner", ItemStackDeserializerItemMetaMigrator::deserializeSkullOwner);
        replaceListOfMapsInMap(mapping, "firework-effects", ItemStackDeserializerItemMetaMigrator::deserializeFireworkEffect);
        replaceListOfMapsInMap(mapping, "patterns", org.bukkit.block.banner.Pattern::new);
        replaceListOfMapsInMap(mapping, "charged-projectiles", itemStackMigrator);
        replaceListOfMapsInMap(mapping, "custom-effects", potionEffect -> {
            convertNumberToIntegerInMap(potionEffect, "amplifier");
            convertNumberToIntegerInMap(potionEffect, "duration");
            return new org.bukkit.potion.PotionEffect(potionEffect);
        });

        String metaType = LogicUtil.tryCast(mapping.get("meta-type"), String.class);

        for (Migrator migrator : migrators) {
            migrator.migrate(mapping, metaType);
        }

        return applyWithoutFixes(mapping);
    }

    public ItemMeta applyWithoutFixes(Map<String, Object> mapping) {
        ItemMeta meta = CraftItemStackHandle.deserializeItemMeta(mapping);
        itemMetaToArgs.put(meta, mapping);
        return meta;
    }

    /**
     * Gets the original (cleaned up) arguments used for deserializing an ItemMeta object.
     * These arguments are kept around for as long the ItemMeta is not garbage-collected,
     * or {@link #cleanupArgsUsedForMeta(ItemMeta)} is called.
     *
     * @param meta ItemMeta
     * @return Map args, or null if the ItemMeta was not deserialized by this deserializer
     */
    public Map<String, Object> getArgsUsedForMeta(ItemMeta meta) {
        return itemMetaToArgs.get(meta);
    }

    /**
     * Cleans up the args mapped to an ItemMeta instance, so that this data can be garbage
     * collected earlier.
     *
     * @param meta ItemMeta
     */
    public void cleanupArgsUsedForMeta(ItemMeta meta) {
        itemMetaToArgs.remove(meta);
    }

    public interface Migrator {
        void migrate(Map<String, Object> args, String metaType);
    }
}
