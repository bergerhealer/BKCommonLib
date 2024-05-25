package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.inventory.ItemParserMetaRule;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import static com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName.getMaterial;
import static org.junit.Assert.*;

/**
 * Tests the {@link ItemParser} logic
 */
public class ItemParserTest {

    @Test
    public void testItemParserMetaRuleDamage() {
        CommonItemStack baseItem = CommonItemStack.create(MaterialUtil.getFirst("DIAMOND_SWORD", "LEGACY_DIAMOND_SWORD"), 1);

        testItemParser("DIAMOND_SWORD$damage>=12",
                /* Yes */
                new ItemStack[] {
                        baseItem.clone().setDamage(15).toBukkit(),
                        baseItem.clone().setDamage(200).toBukkit()
                },

                /* No */
                new ItemStack[] {
                        baseItem.clone().toBukkit(),
                        baseItem.clone().setDamage(2).toBukkit(),
                        baseItem.clone().setDamage(0).toBukkit()
                });
    }

    @Test
    public void testItemParserMetaRuleNBT() {
        CommonItemStack baseItem = CommonItemStack.create(MaterialUtil.getFirst("DIAMOND_SWORD", "LEGACY_DIAMOND_SWORD"), 1);

        testItemParser("DIAMOND_SWORD$cool>=12",
                /* Yes */
                new ItemStack[] {
                        baseItem.clone().updateCustomData(tag -> tag.putValue("cool", 15)).toBukkit(),
                        baseItem.clone().updateCustomData(tag -> tag.putValue("cool", 15.0)).toBukkit()
                },

                /* No */
                new ItemStack[] {
                        baseItem.clone().toBukkit(),
                        baseItem.clone().updateCustomData(tag -> tag.putValue("cool", 2)).toBukkit(),
                        baseItem.clone().updateCustomData(tag -> tag.putValue("cool", 2.0)).toBukkit(),
                });
    }

    @Test
    public void testMetaRuleTextOperators() {
        assertTrue(ItemParserMetaRule.Operator.LESS_THAN.compare("a", "z"));
        assertFalse(ItemParserMetaRule.Operator.LESS_THAN.compare("a", "a"));
        assertFalse(ItemParserMetaRule.Operator.LESS_THAN.compare("z", "a"));

        assertTrue(ItemParserMetaRule.Operator.GREATER_THAN.compare("z", "a"));
        assertFalse(ItemParserMetaRule.Operator.GREATER_THAN.compare("a", "z"));
        assertFalse(ItemParserMetaRule.Operator.GREATER_THAN.compare("z", "z"));

        assertTrue(ItemParserMetaRule.Operator.LESS_EQUAL_THAN.compare("a", "z"));
        assertTrue(ItemParserMetaRule.Operator.LESS_EQUAL_THAN.compare("z", "z"));
        assertFalse(ItemParserMetaRule.Operator.LESS_EQUAL_THAN.compare("z", "a"));

        assertTrue(ItemParserMetaRule.Operator.GREATER_EQUAL_THAN.compare("z", "a"));
        assertFalse(ItemParserMetaRule.Operator.GREATER_EQUAL_THAN.compare("a", "z"));
        assertTrue(ItemParserMetaRule.Operator.GREATER_EQUAL_THAN.compare("z", "z"));

        assertFalse(ItemParserMetaRule.Operator.EQUAL.compare("a", "z"));
        assertFalse(ItemParserMetaRule.Operator.EQUAL.compare("z", "a"));
        assertTrue(ItemParserMetaRule.Operator.EQUAL.compare("a", "a"));
        assertTrue(ItemParserMetaRule.Operator.EQUAL.compare("z", "z"));

        assertTrue(ItemParserMetaRule.Operator.NOT_EQUAL.compare("a", "z"));
        assertTrue(ItemParserMetaRule.Operator.NOT_EQUAL.compare("z", "a"));
        assertFalse(ItemParserMetaRule.Operator.NOT_EQUAL.compare("a", "a"));
        assertFalse(ItemParserMetaRule.Operator.NOT_EQUAL.compare("z", "z"));
    }

    @Test
    public void testMetaRuleNumberOperators() {
        assertTrue(ItemParserMetaRule.Operator.LESS_THAN.compare(0, 15));
        assertFalse(ItemParserMetaRule.Operator.LESS_THAN.compare(15, 15));
        assertFalse(ItemParserMetaRule.Operator.LESS_THAN.compare(15, 0));

        assertTrue(ItemParserMetaRule.Operator.GREATER_THAN.compare(15, 0));
        assertFalse(ItemParserMetaRule.Operator.GREATER_THAN.compare(0, 15));
        assertFalse(ItemParserMetaRule.Operator.GREATER_THAN.compare(15, 15));

        assertTrue(ItemParserMetaRule.Operator.LESS_EQUAL_THAN.compare(0, 15));
        assertTrue(ItemParserMetaRule.Operator.LESS_EQUAL_THAN.compare(15, 15));
        assertFalse(ItemParserMetaRule.Operator.LESS_EQUAL_THAN.compare(15, 0));

        assertTrue(ItemParserMetaRule.Operator.GREATER_EQUAL_THAN.compare(15, 0));
        assertFalse(ItemParserMetaRule.Operator.GREATER_EQUAL_THAN.compare(0, 15));
        assertTrue(ItemParserMetaRule.Operator.GREATER_EQUAL_THAN.compare(15, 15));

        assertFalse(ItemParserMetaRule.Operator.EQUAL.compare(0, 1));
        assertFalse(ItemParserMetaRule.Operator.EQUAL.compare(1, 0));
        assertTrue(ItemParserMetaRule.Operator.EQUAL.compare(0, 0));
        assertTrue(ItemParserMetaRule.Operator.EQUAL.compare(10, 10));

        assertTrue(ItemParserMetaRule.Operator.NOT_EQUAL.compare(0, 1));
        assertTrue(ItemParserMetaRule.Operator.NOT_EQUAL.compare(1, 0));
        assertFalse(ItemParserMetaRule.Operator.NOT_EQUAL.compare(0, 0));
        assertFalse(ItemParserMetaRule.Operator.NOT_EQUAL.compare(10, 10));
    }

    @Test
    public void testItemParserBasic() {
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
}
