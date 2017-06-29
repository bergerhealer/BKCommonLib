package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.junit.Test;
import static org.junit.Assert.*;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/*
 * Tests whether the material properties and ItemStack-related utilities are functional
 */
public class ItemMaterialTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testBlockDataLookup() {
        for (Material material : Material.values()) {
            if (!material.isBlock()) continue;
            
            BlockData data = BlockData.fromTypeId(material.getId());
            assertEquals(material, data.getType());
        }
    }

    @Test
    public void testMaterialProperties() {
        testProperty(MaterialUtil.ISSOLID, "ISSOLID")
            .check(Material.STONE, true)
            .check(Material.AIR, false)
            .check(Material.GLASS, false)
            .check(Material.APPLE, false)
            .check(Material.RAILS, false)
            .check(Material.WOOD, true)
            .check(Material.IRON_SWORD, false)
            .check(Material.RED_ROSE, false)
            .check(Material.STONE_PLATE, false)
            .check(Material.LEAVES, false)
            .check(Material.SPRUCE_DOOR, false)
            .check(Material.CHEST, false)
            .check(Material.FURNACE, true)
            .check(Material.PISTON_BASE, false)
            .done();

        // Note: SUFFOCATES is the same as ISSOLID right now
        // Is this correct? I have not seen any case where the values are different.
        testProperty(MaterialUtil.SUFFOCATES, "SUFFOCATES")
            .checkProperty(MaterialUtil.ISSOLID, true)
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.EMISSION, "EMISSION")
            .check(Material.STONE, 0)
            .check(Material.APPLE, 0)
            .check(Material.REDSTONE_LAMP_ON, 15)
            .check(Material.TORCH, 14)
            .check(Material.GLOWSTONE, 15)
            .check(Material.LAVA, 15)
            .check(Material.AIR, 0)
            .check(Material.BROWN_MUSHROOM, 1)
            .check(Material.REDSTONE_TORCH_ON, 7)
            .done();

        testProperty(MaterialUtil.OPACITY, "OPACITY")
            .check(Material.AIR, 0)
            .check(Material.APPLE, 0)
            .check(Material.WOODEN_DOOR, 0)
            .check(Material.RAILS, 0)
            .check(Material.LEAVES, 1)
            .check(Material.WEB, 1)
            .check(Material.ICE, 3)
            .check(Material.WATER, 3)
            .check(Material.STONE, 255)
            .check(Material.FURNACE, 255)
            .done();

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

        testProperty(MaterialUtil.ISPOWERSOURCE, "ISPOWERSOURCE")
            .check(Material.ACTIVATOR_RAIL, false) // these read power, not write
            .check(Material.POWERED_RAIL, false) // these read power, not write
            .check(Material.HOPPER, false) // these read power, not write
            .checkData(org.bukkit.material.Command.class, false) // these read power, not write
            .checkData(org.bukkit.material.PistonBaseMaterial.class, false) // these read power, not write
            .checkProperty(MaterialUtil.ISPRESSUREPLATE, true)
            .check(Material.DAYLIGHT_DETECTOR, true)
            .check(Material.DAYLIGHT_DETECTOR_INVERTED, true)
            .check(Material.DETECTOR_RAIL, true)
            .check(Material.TRAPPED_CHEST, true)
            .check(Material.REDSTONE_BLOCK, true)
            .check(Material.DIODE_BLOCK_OFF, true)
            .check(Material.DIODE_BLOCK_ON, true)
            .check(Material.REDSTONE_COMPARATOR_ON, true)
            .check(Material.REDSTONE_COMPARATOR_OFF, true)
            .checkData(org.bukkit.material.Redstone.class, true) // when new redstone-like types are added, this should fail
            .checkOthers(false)
            .done();

        testProperty(MaterialUtil.ISFUEL, "ISFUEL")
            .check(Material.COAL, true)
            .check(Material.WOOD, true)
            .check(Material.STICK, true)
            .check(Material.STONE, false)
            .check(Material.GLASS, false)
            .check(Material.APPLE, false)
            .done();

        testProperty(MaterialUtil.ISHEATABLE, "ISHEATABLE")
            .check(Material.COBBLESTONE, true)
            .check(Material.STONE, false)
            .check(Material.SAND, true)
            .check(Material.GLASS, false)
            .check(Material.DIRT, false)
            .check(Material.RAW_BEEF, true)
            .check(Material.RAW_FISH, true)
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
        ItemStack item = ItemUtil.createItem(Material.WOOD, 1);
        String old_name = ItemUtil.getDisplayName(item);
        ItemUtil.setDisplayName(item, "COOLNAME");
        assertEquals("COOLNAME", ItemUtil.getDisplayName(item));
        ItemUtil.setDisplayName(item, null);
        assertEquals(old_name, ItemUtil.getDisplayName(item));
    }

    @Test
    public void testItemTag() {
        ItemStack item = ItemUtil.createItem(Material.WOOD, 1);
        assertNull(ItemUtil.getMetaTag(item));
        CommonTagCompound tag = ItemUtil.getMetaTag(item, true);
        assertNotNull(tag);
        tag.putValue("test", "awesome!");
        tag = ItemUtil.getMetaTag(item);
        assertNotNull(tag);
        assertTrue(tag.containsKey("test"));
        assertEquals("awesome!", tag.getValue("test"));
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
            this.not_handled.addAll(Arrays.asList(Material.values()));
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

        public PropertyTest<T> check(Material m, T value) {
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
                for (Material m : Material.values()) {
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
