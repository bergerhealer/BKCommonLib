package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DetectorRail;
import org.bukkit.material.MaterialData;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials.*;
import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getFirst;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializer;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

/*
 * Tests whether the material properties and ItemStack-related utilities are functional
 */
public class ItemMaterialTest {

    @Test
    @Ignore
    public void printModernToLegacyMaterialMapping() {
        for (Material mlegacy : MaterialUtil.getAllMaterials()) {
            if (MaterialUtil.isLegacyType(mlegacy)) {
                String legacyName = mlegacy.name().substring(7);
                Material mmodern = null;
                if ( mlegacy.isBlock()) {
                    BlockData b = BlockData.fromMaterial(mlegacy);
                    mmodern = b.getType();
                } else {
                    Object item = HandleConversion.toItemHandle(mlegacy);
                    mmodern = WrapperConversion.toMaterialFromItemHandle(item);
                }
                if (mmodern == null) {
                    // Try to find non-legacy by the exact same name LEGACY_
                    for (Material m : MaterialUtil.getAllMaterials()) {
                        if (MaterialUtil.isLegacyType(m)) {
                            continue;
                        }
                        if (MaterialUtil.isBlock(m) != mlegacy.isBlock()) {
                            continue;
                        }
                        if (m.name().equals(legacyName)) {
                            mmodern = m;
                            break;
                        }
                        if (m.name().contains(legacyName) || legacyName.contains(m.name())) {
                            mmodern = m;
                        }
                    }
                }
                System.out.println(mmodern + " = " + legacyName);
            }
        }
    }
    
    @Test
    public void testMaterialProperties() {
        // Requires Block now. Test broken.
        /*
        testProperty(MaterialUtil.ISSOLID, "ISSOLID")
            .check(Material.STONE, true)
            .check(Material.AIR, false)
            .check(Material.GLASS, false)
            .check(Material.APPLE, false)
            .check("RAIL", false)
            .checkLegacy("RAILS", false)
            .check("OAK_PLANKS", true)
            .checkLegacy("WOOD", true)
            .check(Material.IRON_SWORD, false)
            .check(getFirst("ROSE_RED", "LEGACY_RED_ROSE"), false)
            .check(getFirst("STONE_PRESSURE_PLATE", "LEGACY_STONE_PLATE"), false)
            .check(getFirst("OAK_LEAVES", "LEGACY_LEAVES"), false)
            .check(Material.SPRUCE_DOOR, false)
            .check(Material.CHEST, false)
            .check(Material.FURNACE, true)
            .check(getFirst("PISTON", "LEGACY_PISTON_BASE"), false)
            .done();
        */

        // Note: SUFFOCATES is the same as ISSOLID right now
        // Is this correct? I have not seen any case where the values are different.
        /*
        testProperty(MaterialUtil.SUFFOCATES, "SUFFOCATES")
            .checkProperty(MaterialUtil.ISSOLID, true)
            .checkOthers(false)
            .done();
        */

        testProperty(MaterialUtil.EMISSION, "EMISSION")
            .check(Material.STONE, 0)
            .check(Material.APPLE, 0)
            .check(Material.TORCH, 14)
            .check(Material.GLOWSTONE, 15)
            .check(Material.LAVA, 15)
            .check(Material.AIR, 0)
            .check(Material.BROWN_MUSHROOM, 1)
            .done();

        // No longer exists, and can not test on MC >= 1.13 because it now requires a world access
        /*
        testProperty(MaterialUtil.OPACITY, "OPACITY")
            .check(Material.AIR, 0)
            .check(Material.APPLE, 0)
            .check(getFirst("OAK_DOOR", "WOODEN_DOOR"), 0)
            .check(MaterialEx.RAIL, 0)
            .check(getFirst("OAK_LEAVES", "LEAVES"), 1)
            .check(getFirst("COBWEB", "WEB"), 1)
            .check(Material.ICE, 3)
            .check(Material.WATER, 3)
            .check(Material.STONE, 255)
            .check(Material.FURNACE, 255)
            .done();
        */

        testProperty(MaterialUtil.ISDOOR, "ISDOOR")
            .checkData(org.bukkit.material.Door.class, true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISBUTTON, "ISBUTTON")
            .checkData(org.bukkit.material.Button.class, true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISDIODE, "ISDIODE")
            .checkData(org.bukkit.material.Diode.class, true)
            .checkOthers(false)
            .done();

        Class<?> comparatorClass = CommonUtil.getClass("org.bukkit.material.Comparator");
        if (comparatorClass != null) {
            testProperty(MaterialUtil.ISCOMPARATOR, "ISCOMPARATOR")
                .checkData(comparatorClass, true)
                .checkOthers(false)
                .done();
        }

        testProperty(MaterialUtil.ISRAILS, "ISRAILS")
            .checkData(org.bukkit.material.Rails.class, true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISSIGN, "ISSIGN")
            .checkData(org.bukkit.material.Sign.class, true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISPRESSUREPLATE, "ISPRESSUREPLATE")
            .checkData(org.bukkit.material.PressurePlate.class, true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISMINECART, "ISMINECART")
            .checkName("MINECART", true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISSWORD, "ISSWORD")
            .checkName("SWORD", true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISBOOTS, "ISBOOTS")
            .checkName("BOOTS", true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISCHESTPLATE, "ISCHESTPLATE")
            .checkName("CHESTPLATE", true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISLEGGINGS, "ISLEGGINGS")
            .checkName("LEGGINGS", true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISHELMET, "ISHELMET")
            .checkName("HELMET", true)
            .checkOthers(false)
            .done();

        {
            PropertyTest<Boolean> test = testProperty(MaterialUtil.ISPOWERSOURCE, "ISPOWERSOURCE");
            test.checkNewAndLegacy("ACTIVATOR_RAIL", false) // these read power, not write
                .checkNewAndLegacy("POWERED_RAIL", false) // these read power, not write
                .checkNewAndLegacy("HOPPER", false) // these read power, not write
                .checkData(org.bukkit.material.Command.class, false) // these read power, not write
                .checkData(org.bukkit.material.PistonBaseMaterial.class, false) // these read power, not write
                .checkData(DetectorRail.class, true)
                .checkProperty(MaterialUtil.ISPRESSUREPLATE, true)
                .checkNewAndLegacy("DAYLIGHT_DETECTOR", true)
                .checkLegacy("DAYLIGHT_DETECTOR_INVERTED", true)
                .checkNewAndLegacy("TRAPPED_CHEST", true)
                .checkNewAndLegacy("REDSTONE_BLOCK", true)
                .check("LECTERN", true) // since mc 1.14
                .check("LIGHTNING_ROD", true) // since mc 1.17
                .check("SCULK_SENSOR", true) // since mc 1.17
                .check("CALIBRATED_SCULK_SENSOR", true); // since mc 1.20

            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                // TODO!
            } else {
                test.check(Material.getMaterial("DAYLIGHT_DETECTOR_INVERTED"), true)
                    .check(Material.getMaterial("DIODE_BLOCK_OFF"), true)
                    .check(Material.getMaterial("DIODE_BLOCK_ON"), true)
                    .check(Material.getMaterial("REDSTONE_COMPARATOR_ON"), true)
                    .check(Material.getMaterial("REDSTONE_COMPARATOR_OFF"), true);
            }

            if (Common.evaluateMCVersion(">=", "1.19.4")) {
                test.check(Material.getMaterial("JUKEBOX"), true)
                    .check(Material.getMaterial("LEGACY_JUKEBOX"), true);
            }

            test.check(ParseUtil.parseMaterial("OBSERVER", null), true)
                .checkData(org.bukkit.material.Redstone.class, true) // when new redstone-like types are added, this should fail
                .checkOthers(false)
                .done();
        }

        testProperty(MaterialUtil.ISFUEL, "ISFUEL")
            .check(Material.COAL, true)
            .check("OAK_PLANKS", true)
            .checkLegacy("WOOD", true)
            .check(Material.STICK, true)
            .check(Material.STONE, false)
            .check(Material.GLASS, false)
            .check(Material.APPLE, false)
            .done();

        testProperty(MaterialUtil.ISHEATABLE, "ISHEATABLE")
            .check(Material.COBBLESTONE, true)
            .check(Material.STONE, Common.evaluateMCVersion(">=", "1.14"))
            .check(Material.SAND, true)
            .check(Material.GLASS, false)
            .check(Material.DIRT, false)
            .check(Material.BAKED_POTATO, false)
            .done();
    }

    @Test
    public void testEmptyItem() {
        ItemStack item = ItemUtil.emptyItem();
        assertNotNull(item);
        assertTrue(ItemUtil.isEmpty(item));
    }

    @Test
    public void testEqualsIgnoreAmount() {
        //TODO: Make this test better!
        ItemStack item = new ItemStack(Material.IRON_DOOR);
        testEQIgnoreAmount(item);
    }

    @Test
    public void testDisplayName() {
        ItemStack item = ItemUtil.createItem(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 1);
        String old_name = ItemUtil.getDisplayName(item);
        ItemUtil.setDisplayName(item, "COOLNAME");
        assertEquals("COOLNAME", ItemUtil.getDisplayName(item));
        ItemUtil.setDisplayName(item, null);
        assertEquals(old_name, ItemUtil.getDisplayName(item));
    }

    @Test
    public void testItemCustomData() {
        CommonItemStack item = CommonItemStack.create(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 1);
        assertFalse(item.hasCustomData());
        assertTrue(item.getCustomData().isEmpty());
        assertTrue(item.getCustomDataCopy().isEmpty());

        item.updateCustomData(metadata -> {
            metadata.putValue("test", "awesome!");
        });

        assertTrue(item.hasCustomData());
        assertFalse(item.getCustomData().isEmpty());
        assertEquals("awesome!", item.getCustomData().getValue("test"));
        assertEquals("awesome!", item.getCustomDataCopy().getValue("test"));

        CommonTagCompound meta = item.getCustomDataCopy();
        meta.putValue("otherTest", "cool!");
        item.setCustomData(meta);

        // Verify this is updated too
        assertEquals("cool!", item.getCustomData().getValue("otherTest"));
        assertEquals("cool!", item.getCustomDataCopy().getValue("otherTest"));
        assertEquals("awesome!", item.getCustomData().getValue("test"));
        assertEquals("awesome!", item.getCustomDataCopy().getValue("test"));
    }

    @Test
    public void testItemFromBlockData() {
        BlockData data;
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // Get BlockData of stained glass the new way
            data = BlockData.fromMaterial(MaterialUtil.getMaterial("PURPLE_STAINED_GLASS"));
        } else {
            // Get BlockData of stained glass the old way
            data = BlockData.fromMaterialData(MaterialUtil.getMaterial("LEGACY_STAINED_GLASS"), 2);
        }

        ItemStack item = data.createItem(12);
        assertNotNull(item);
        assertEquals(12, item.getAmount());
        assertEquals(data, BlockData.fromItemStack(item));

        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            assertEquals(MaterialUtil.getMaterial("PURPLE_STAINED_GLASS"), item.getType());
        } else {
            assertEquals(MaterialUtil.getMaterial("LEGACY_STAINED_GLASS"), item.getType());
            assertEquals(2, item.getDurability());
        }
    }

    // Only works on MC 1.12.1 - generate an item variants yaml configuration
    // This configuration is used on MC < 1.12.1
    @Ignore
    @Test
    public void generateVariantsConfig() {
        FileConfiguration config = new FileConfiguration("test.yml");
        for (Material m : getAllMaterials()) {
            List<ItemStack> items = ItemUtil.getItemVariants(m);

            if (items.size() == 0) {
                // Material has no variants at all
                continue;
            }

            if (items.size() != 1 || !items.get(0).equals(new ItemStack(m))) {
                int dur_start = items.get(0).getDurability();
                int dur_end = items.get(items.size() - 1).getDurability();
                boolean validRange = true;
                Iterator<ItemStack> it = items.iterator();
                for (int dur = dur_start; dur <= dur_end; dur++) {
                    ItemStack t = new ItemStack(m, 1, (short) dur);
                    if (!it.hasNext() || !it.next().equals(t)) {
                        validRange = false;
                        break;
                    }
                }
                if (validRange) {
                    System.out.println("registerRange(\"" + m.name() + "\", " + dur_start + ", " + dur_end + ");");
                } else {
                    // 'Weird'
                    ConfigurationNode node = config.getNode(m.toString());
                    for (int i = 0; i < items.size(); i++) {
                        node.set("item" + i, items.get(i));
                    }
                    //System.out.println(node.toString());
                }
                

            }
        }
        config.save();
    }

    @Test
    public void testArrowVariants() {
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return;
        }

        // Retrieve from listing
        Material tippedArrowMat = getMaterial("TIPPED_ARROW");
        List<ItemStack> actual = ItemUtil.getItemVariants(tippedArrowMat);

        // Verify all ItemStacks are actually TIPPED_ARROW types
        for (ItemStack item : actual) {
            assertEquals(tippedArrowMat, item.getType());
        }

        // Check all are contained, order does not matter
        assertTrue("Not enough variants: " + actual.size(), actual.size() >= 38);

        // Add all items to a set. Each item should be unique.
        HashSet<ItemStack> set = new HashSet<ItemStack>(actual);
        assertEquals(actual.size(), set.size());
    }

    @Test
    public void testPotionVariants() {
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return;
        }

        // Retrieve from listing
        Material potionMat = getMaterial("POTION");
        List<ItemStack> actual = ItemUtil.getItemVariants(potionMat);

        // Verify all ItemStacks are actually POTION types
        for (ItemStack item : actual) {
            assertEquals(potionMat, item.getType());
        }

        // Check all are contained, order does not matter
        assertTrue("Not enough variants: " + actual.size(), actual.size() >= 38);

        // Add all items to a set. Each item should be unique.
        HashSet<ItemStack> set = new HashSet<ItemStack>(actual);
        assertEquals(actual.size(), set.size());
    }

    @Test
    public void testLegacyWoolVariants() {
        // Only if server < 1.13 is this relevant
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return;
        }

        // Test using White Whool
        // All 16 wool colors should be returned here
        Material woolMat = getLegacyMaterial("WOOL");
        List<ItemStack> expected = new ArrayList<ItemStack>();
        for (int dur = 0; dur < 16; dur++) {
            expected.add(new ItemStack(woolMat, 1, (short) dur));
        }

        // Retrieve from listing
        List<ItemStack> actual = ItemUtil.getItemVariants(woolMat);

        // Check all are contained, order does not matter
        assertEquals(expected.size(), actual.size());
        for (ItemStack expectedItem : expected) {
            boolean contained = false;
            for (ItemStack actualItem : actual) {
                if (actualItem.getType() == expectedItem.getType() && actualItem.getDurability() == expectedItem.getDurability()) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                System.out.println("Actual: ");
                for (ItemStack actualItem : actual) {
                    System.out.println("- " + actualItem.toString());
                }
                fail("Item was not found: " + expectedItem);
            }
        }
    }

    @Test
    public void testItemTypes() {
        // Get a full listing of all valid item types
        List<Material> itemTypes = ItemUtil.getItemTypes();

        // Perform some basic tests on the list to validate correctness
        assertTrue(itemTypes.contains(getFirst("OAK_PLANKS", "LEGACY_WOOD")));
        assertTrue(itemTypes.contains(Material.DIAMOND));
        assertTrue(itemTypes.contains(Material.DIAMOND_PICKAXE));
        assertTrue(itemTypes.contains(Material.POTION));
        assertTrue(itemTypes.contains(Material.ARMOR_STAND));
        assertFalse(itemTypes.contains(getFirst("NETHER_PORTAL", "LEGACY_PORTAL")));
        assertFalse(itemTypes.contains(getFirst("END_PORTAL", "LEGACY_ENDER_PORTAL")));
    }

    @Test
    public void testItemParser() {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // Can also test 1.13 and later material names
            // Data value specified, should match red wool, both legacy and new
            testItemParser("WOOL:RED", new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 14).createItem(1),
                    ItemUtil.createItem(getMaterial("RED_WOOL"), 1),
            }, new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 10).createItem(1),
                    BlockData.fromMaterialData(getMaterial("LEGACY_STONE"), 0).createItem(1),
            });
            // Data value set to 'any', which should guarantee parsing to any type of wool
            testItemParser("WOOL:", new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 14).createItem(1),
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 10).createItem(1),
                    ItemUtil.createItem(getMaterial("RED_WOOL"), 1)
            }, new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_STONE"), 0).createItem(1),
            });
            // OAK_SLAB since 1.13
            testItemParser("OAK_SLAB", new ItemStack[] {
                    ItemUtil.createItem(getMaterial("OAK_SLAB"), 1)
            }, new ItemStack[] {
                    ItemUtil.createItem(getMaterial("AIR"), 1)
            });
        } else {
            // Only legacy material names work
            // Data value specified, should match red wool only
            testItemParser("WOOL:RED", new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 14).createItem(1),
            }, new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 10).createItem(1),
                    BlockData.fromMaterialData(getMaterial("LEGACY_STONE"), 0).createItem(1),
            });
            // Data value set to 'any', which should guarantee parsing to any type of wool
            testItemParser("WOOL:", new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 14).createItem(1),
                    BlockData.fromMaterialData(getMaterial("LEGACY_WOOL"), 10).createItem(1),
            }, new ItemStack[] {
                    BlockData.fromMaterialData(getMaterial("LEGACY_STONE"), 0).createItem(1),
            });
        }
    }

    @Test
    public void testToItemHandleConversion() {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            for (Material m : MaterialsByName.getAllByName("LEGACY_MINECART", "MINECART")) {
                assertNotNull(m);
                Object item = HandleConversion.toItemHandle(m);
                assertNotNull("Material " + m + " resulted in null item", item);
            }
        }
    }

    @Test
    public void testToBlockHandleConversion() {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            for (Material m : MaterialsByName.getAllByName("LEGACY_STONE", "STONE")) {
                assertNotNull(m);
                Object block = HandleConversion.toBlockHandle(m);
                assertNotNull("Material " + m + " resulted in null block", block);
            }
        }
    }

    @Test
    public void testItemStackDeserializationVersion() {
        int curr = CraftMagicNumbersHandle.getDataVersion();
        if (curr > ItemStackDeserializer.INSTANCE.getMaxSupportedDataVersion()) {
            fail("ItemStackDeserializer needs to support the new Data Version (" + curr + ")");
        }
    }

    private static void testItemParser(String fullname, ItemStack[] items_yes, ItemStack[] items_no) {
        ItemParser parser = ItemParser.parse(fullname);
        for (ItemStack item : items_yes) {
            if (!parser.match(item)) {
                fail("Item " + item + " should match " + parser + " but it did not!");
            }
        }
        for (ItemStack item : items_no) {
            if (parser.match(item)) {
                fail("Item " + item + " should not match " + parser + " but it did!");
            }
        }
    }

    private static void testEQIgnoreAmount(ItemStack item) {
        ItemStack a = ItemUtil.cloneItem(item);
        ItemStack b = ItemUtil.cloneItem(item);
        a.setAmount(1);
        b.setAmount(2);
        assertTrue(ItemUtil.equalsIgnoreAmount(a, b));
    }

    private static <T> PropertyTest<T> testProperty(MaterialProperty<T> prop, String name) {
        return new PropertyTest<T>(prop, name);
    }

    private static class PropertyTest<T> {
        private final MaterialProperty<T> prop;
        private final String name;
        private boolean has_error = false;
        private HashSet<Material> not_handled = new HashSet<Material>();

        public PropertyTest(MaterialProperty<T> prop, String name) {
            this.prop = prop;
            this.name = name;
            this.has_error = false;
            this.not_handled.addAll(Arrays.asList(getAllMaterials()));
        }

        public PropertyTest<T> checkProperty(MaterialProperty<Boolean> match, T value) {
            for (Material m : remaining()) {
                if (match.get(m)) {
                    check(m, value);
                }
            }
            return this;
        }

        public PropertyTest<T> checkName(String name, T value) {
            for (Material m : remaining()) {
                if (m.toString().contains(name)) {
                    check(m, value);
                }
            }
            return this;
        }

        public PropertyTest<T> checkData(Class<?> materialData, T value) {
            for (Material m : remaining()) {
               MaterialData data = BlockData.fromMaterial(m).newMaterialData();
               if (materialData.isInstance(data)) {
                   check(m, value);
               }
            }
            return this;
        }

        public PropertyTest<T> checkOthers(T value) {
            for (Material m : remaining()) {
                check(m, value);
            }
            return this;
        }

        public HashSet<Material> remaining() {
            return new HashSet<Material>(not_handled);
        }

        public PropertyTest<T> checkNewAndLegacy(String name, T value) {
            check(name, value);
            checkLegacy(name, value);
            return this;
        }

        public PropertyTest<T> checkLegacy(String name, T value) {
            return check(getLegacyMaterial(name), value);
        }

        public PropertyTest<T> check(String name, T value) {
            return check(getMaterial(name), value);
        }

        public PropertyTest<T> check(Material m, T value) {
            if (m == null) {
                return this; // ignore
            }
            T real = prop.get(m);
            not_handled.remove(m);
            if (!real.equals(value)) {
                if (!has_error) {
                    has_error = true;
                    Logging.LOGGER.severe("Mismatch for Material property '" + name + "':");
                }
                Logging.LOGGER.severe("  " + name + "[" + m.toString() + "] == " + real.toString() + ", " + value.toString() + " expected");
            }
            return this;
        }

        public void done() {
            if (has_error) {
                Logging.LOGGER.severe("Property " + name + " is invalid. It has the following mapping:");

                TreeMap<Object, ArrayList<String>> mapping = new TreeMap<Object, ArrayList<String>>();
                for (Material m : getAllMaterials()) {
                    Object val = prop.get(m);
                    ArrayList<String> list = mapping.get(val);
                    if (list == null) {
                        list = new ArrayList<String>();
                        mapping.put(val, list);
                    }
                    list.add(m.toString());
                }
                for (Map.Entry<Object, ArrayList<String>> entry : mapping.entrySet()) {
                    Logging.LOGGER.severe("VALUES " + name + " == " + entry.getKey() + ":");

                    ArrayList<String> list = entry.getValue();
                    int nn = 5;
                    while (list.size() >= nn) {
                        ArrayList<String> part = new ArrayList<String>();
                        for (int i = 0; i < nn; i++) {
                            part.add(list.get(0));
                            list.remove(0);
                        }
                        Logging.LOGGER.severe("  " + StringUtil.join(" ", part));
                    }
                    Logging.LOGGER.severe("  " + StringUtil.join(" ", list));
                }

                throw new RuntimeException("Property " + name + " is invalid and needs fixing!");
            }
        }
    }
}
