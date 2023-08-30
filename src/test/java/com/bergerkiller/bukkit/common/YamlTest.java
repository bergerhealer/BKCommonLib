package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bergerkiller.bukkit.common.config.yaml.YamlListNode;
import org.bukkit.GameMode;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;
import org.junit.Ignore;
import org.junit.Test;
import org.yaml.snakeyaml.error.YAMLException;

import com.bergerkiller.bukkit.common.config.yaml.YamlDeserializer;
import com.bergerkiller.bukkit.common.config.yaml.YamlNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.config.yaml.YamlSerializer;
import com.bergerkiller.bukkit.common.io.AsyncTextWriter;
import com.bergerkiller.mountiplex.MountiplexUtil;

public class YamlTest {

    @Test
    public void testYamlCopyIntoArrayToNode() {
        YamlNode rootOne = new YamlNode();
        YamlNode childOne = rootOne.getNode("child");
        childOne.set("array.1", "one");
        childOne.set("array.2", "two");
        childOne.set("array.3", "three");
        childOne.set("array.4", "four");
        childOne.set("array.5", "five");

        YamlNode rootTwo = new YamlNode();
        YamlNode childTwo = rootTwo.getNode("child");
        childTwo.set("array", Arrays.asList(6, 7, 8));

        // Copy rootTwo into rootOne. It should turn from a node into a list
        rootOne.setTo(rootTwo);

        // Verify
        assertTrue(rootOne.get("child.array") instanceof YamlListNode);
        assertEquals(Arrays.asList(6, 7, 8), rootOne.getList("child.array"));
    }

    @Test
    public void testYamlCopyIntoNodeToArray() {
        YamlNode rootOne = new YamlNode();
        YamlNode childOne = rootOne.getNode("child");
        childOne.set("array", Arrays.asList(1, 2, 3, 4, 5));

        YamlNode rootTwo = new YamlNode();
        YamlNode childTwo = rootTwo.getNode("child");
        childTwo.set("array.6", "six");
        childTwo.set("array.7", "seven");
        childTwo.set("array.8", "eight");

        // Copy rootTwo into rootOne. It should turn from a node into a list
        rootOne.setTo(rootTwo);

        // Verify
        assertFalse(rootOne.get("child.array") instanceof YamlListNode);
        assertEquals(3, rootOne.getNode("child.array").getKeys().size());
        assertEquals("six", rootOne.get("child.array.6"));
        assertEquals("seven", rootOne.get("child.array.7"));
        assertEquals("eight", rootOne.get("child.array.8"));
    }

    @Test
    public void testYamlCopyIntoArray() {
        YamlNode rootOne = new YamlNode();
        YamlNode childOne = rootOne.getNode("child");
        childOne.set("array", Arrays.asList(1, 2, 3, 4, 5));

        YamlNode rootTwo = new YamlNode();
        YamlNode childTwo = rootTwo.getNode("child");
        childTwo.set("array", Arrays.asList(6, 7, 8));

        // Copy rootTwo into rootOne. It should remove 4/5 elements.
        rootOne.setTo(rootTwo);

        // Check
        assertEquals(Arrays.asList(6, 7, 8), rootOne.getList("child.array"));
    }

    @Test
    public void testSpecialStringValues() {
        List<String> specialValues = Arrays.asList(
                "",
                "inf", "Inf", "INF",
                "nan", "NaN", "NAN",
                "null", "Null", "NULL",
                "y", "Y", "yes", "Yes", "YES",
                "n", "N", "no", "No", "NO",
                "on", "On", "ON",
                "off", "Off", "OFF",
                "true", "True", "TRUE",
                "false", "False", "FALSE",
                "0", "-1", "1e-5", "1.0005", "1_000_000");
        for (String specialValue : specialValues) {
            YamlNode root = new YamlNode();
            root.set("field", specialValue);
            Object value = root.get("field");
            assertEquals(String.class, value.getClass());
            assertEquals(specialValue, value);

            YamlNode a = new YamlNode();
            a.loadFromString(root.toString());
            value = a.get("field");
            assertEquals("Type failure for '" + specialValue + "'", String.class, value.getClass());
            assertEquals(specialValue, value);
        }
    }

    @Test
    public void testYamlNodeAtRelativeYamlPath() {
        YamlNode root = new YamlNode();
        YamlNode child = root.getNode("one");
        YamlNode subchild = child.getNode("two");
        subchild.set("three", "value");

        // ROOT
        assertTrue(root.isNode(YamlPath.ROOT));
        assertEquals(root, root.getNode(YamlPath.ROOT));
        // Level one
        assertTrue(root.isNode(YamlPath.create("one")));
        assertEquals(child, root.getNode(YamlPath.create("one")));
        // Level two
        assertTrue(root.isNode(YamlPath.create("one.two")));
        assertEquals(subchild, root.getNode(YamlPath.create("one.two")));
        // Level two from one
        assertTrue(child.isNode(YamlPath.create("two")));
        assertEquals(subchild, child.getNode(YamlPath.create("two")));
    }

    public static enum TestEnum {
        VALUE_ONE,
        VALUE_TWO_WITH_SUBCLASS {
            @Override
            public void method() {
                System.out.println("Overrided!");
            }
        },
        VALUE_THREE;

        public void method() {
        }
    }

    @Test
    public void testYamlEnumValues() {
        // Verify that serializing an enum value works correctly
        // The stock yaml has a bug in it that it cannot serialize an enum
        // value that is a subclass correctly.
        YamlNode root = new YamlNode();
        root.set("test", TestEnum.VALUE_ONE);
        assertEquals("test: VALUE_ONE\n", root.toString());
        root.set("test", TestEnum.VALUE_TWO_WITH_SUBCLASS);
        assertEquals("test: VALUE_TWO_WITH_SUBCLASS\n", root.toString());
        root.set("test", TestEnum.VALUE_THREE);
        assertEquals("test: VALUE_THREE\n", root.toString());
    }

    @Test
    public void testYamlAssignRetrieveList() {
        YamlNode root = new YamlNode();
        YamlNode child = root.getNode("child");
        List<Object> items;

        // Assign a list of items twice, overwriting it the second time
        // This causes the logic of updating an existing list to fire
        for (int i = 0; i < 2; i++) {
            items = new ArrayList<Object>();
            items.add("item1");
            items.add("item2");
            items.add("item3");
            child.set("items", items);
        }

        // Verify the list
        items = child.getList("items");
        assertEquals(3, items.size());
        assertEquals("item1", items.get(0));
        assertEquals("item2", items.get(1));
        assertEquals("item3", items.get(2));

        // Update the list with different items, but less items, do this twice again
        for (int i = 0; i < 2; i++) {
            items = new ArrayList<Object>();
            items.add("item4");
            items.add("item5");
            child.set("items", items);
        }

        // Verify the list
        items = child.getList("items");
        assertEquals(2, items.size());
        assertEquals("item4", items.get(0));
        assertEquals("item5", items.get(1));

        // Update the list with different items, but more items, do this twice again
        for (int i = 0; i < 2; i++) {
            items = new ArrayList<Object>();
            items.add("item6");
            items.add("item7");
            items.add("item8");
            items.add("item9");
            child.set("items", items);
        }

        // Verify the list
        items = child.getList("items");
        assertEquals(4, items.size());
        assertEquals("item6", items.get(0));
        assertEquals("item7", items.get(1));
        assertEquals("item8", items.get(2));
        assertEquals("item9", items.get(3));
    }

    @Test
    public void testYamlListAddRemove() {
        // Create a list and add a single child node to it
        YamlNode root = new YamlNode();
        List<YamlNode> list = root.getNodeList("list");
        assertEquals(0, list.size());
        assertFalse(root.contains("list"));
        YamlNode child = new YamlNode();
        child.set("key", "value");
        list.add(child);
        assertEquals(1, list.size());
        assertTrue(root.contains("list"));
        assertEquals(child, list.get(0));
        root.setNodeList("list", list);

        // Empty list and then remove the list from root
        list.clear();
        assertEquals(0, list.size());
        root.remove("list");
        assertFalse(root.contains("list"));

        // Adding an entry again should still work and re-adds the list
        child = new YamlNode();
        child.set("key2", "value2");
        list.add(child);
        root.setNodeList("list", list);
        assertTrue(root.contains("list"));
        assertEquals("value2", root.get("list.0.key2"));
    }

    @Test
    public void testYamlReassignNode() {
        // Prepare an old root from which we will be assigning a child
        YamlNode root_old = new YamlNode();
        YamlNode child_old = root_old.getNode("oldNode");
        child_old.set("key1", "value1");
        child_old.set("key2", 12);
        child_old.set("key3.sub1", "sub1");
        child_old.set("key3.sub2", 22);

        // Create a new root with a different structure that will be replaced
        YamlNode root_new = new YamlNode();
        YamlNode child_new = root_new.getNode("newNode");
        child_new.set("key1", "gone");
        child_new.set("newkey", -2);
        child_new.set("different.sub", 55);

        // Re-assign
        root_new.set("newNode", child_old);

        // The previous value, stored at child_new, should now be a detached root node
        // The original data should still be there
        assertFalse(child_new.hasParent());
        assertEquals("gone", child_new.get("key1"));
        assertEquals(55, child_new.get("different.sub"));

        // Right now we expect both root_old and root_new to store the same YAML structure
        // The original data at newNode should be gone entirely
        // The child we assigned (child_old) should now refer to what is stored in root_new
        // Changes to that child should not cause changes to the original node in root_old
        child_old = root_old.getNode("oldNode");
        child_new = root_new.getNode("newNode");
        for (YamlNode child : Arrays.asList(child_old, child_new)) {
            assertEquals("value1", child.get("key1"));
            assertEquals(12, child.get("key2"));
            assertEquals("sub1", child.get("key3.sub1"));
            assertEquals(22, child.get("key3.sub2"));
            assertFalse(child.contains("newkey"));
            assertFalse(child.contains("different"));
            assertFalse(child.contains("different.sub"));
        }

        // Make changes to both child_old and child_new. They should both work and not corrupt each other.
        child_old.set("key2", 101);
        child_new.set("key2", 102);
        assertEquals(101, child_old.get("key2"));
        assertEquals(102, child_new.get("key2"));
        assertFalse(child_old.getNode("key3") == child_new.getNode("key3"));
        child_old.set("key3.sub2", 103);
        child_new.set("key3.sub2", 104);
        assertEquals(103, child_old.get("key3.sub2"));
        assertEquals(104, child_new.get("key3.sub2"));
    }

    @Test
    public void testYamlNodeSetNodeList() {
        YamlNode root = new YamlNode();
        YamlNode n1 = new YamlNode();
        n1.set("key1", "item1value1");
        n1.set("key2", "item1value2");
        YamlNode n2 = new YamlNode();
        n2.set("key1", "item2value1");
        n2.set("key2", "item2value2");
        YamlNode n3 = new YamlNode();
        n3.set("key1", "item3value1");
        n3.set("key2", "item3value2");
        root.setNodeList("list", Arrays.asList(n1, n2, n3));

        assertEquals("item1value1", root.get("list[0].key1"));
        assertEquals("item1value2", root.get("list[0].key2"));
        assertEquals("item2value1", root.get("list[1].key1"));
        assertEquals("item2value2", root.get("list[1].key2"));
        assertEquals("item3value1", root.get("list[2].key1"));
        assertEquals("item3value2", root.get("list[2].key2"));
    }

    @Test
    public void testYamlNodeGetNodeList() {
        YamlNode root = new YamlNode();
        root.set("list.1.key1", "item1value1");
        root.set("list.1.key2", "item1value2");
        root.set("list.2.key1", "item2value1");
        root.set("list.2.key2", "item2value2");
        root.set("list.3.key1", "item3value1");
        root.set("list.3.key2", "item3value2");
        List<YamlNode> nodes = root.getNodeList("list");
        assertEquals(3, nodes.size());
        assertEquals("item1value1", nodes.get(0).get("key1"));
        assertEquals("item1value2", nodes.get(0).get("key2"));
        assertEquals("item2value1", nodes.get(1).get("key1"));
        assertEquals("item2value2", nodes.get(1).get("key2"));
        assertEquals("item3value1", nodes.get(2).get("key1"));
        assertEquals("item3value2", nodes.get(2).get("key2"));
    }

    @Test
    public void testYamlNodeIndexedList() {
        YamlNode root = new YamlNode();
        root.set("list.1", "value1");
        root.set("list.2", "value2");
        root.set("list.4", "value4");
        root.set("list.3", "value3");
        List<String> values = root.getList("list", String.class);
        assertEquals(4, values.size());
        assertEquals("value1", values.get(0));
        assertEquals("value2", values.get(1));
        assertEquals("value3", values.get(2));
        assertEquals("value4", values.get(3));
        assertEquals("value1", root.get("list.1"));
        assertEquals("value2", root.get("list.2"));
        assertEquals("value3", root.get("list.3"));
        assertEquals("value4", root.get("list.4"));

        // Add a value, from then on the list should maintain 0-upward indices
        values.add("value5");
        assertEquals(5, values.size());
        assertEquals("value1", root.get("list.0"));
        assertEquals("value2", root.get("list.1"));
        assertEquals("value3", root.get("list.2"));
        assertEquals("value4", root.get("list.3"));
        assertEquals("value5", root.get("list.4"));
        values.remove("value2");
        assertEquals(4, values.size());
        assertEquals("value1", root.get("list.0"));
        assertEquals("value3", root.get("list.1"));
        assertEquals("value4", root.get("list.2"));
        assertEquals("value5", root.get("list.3"));
    }

    @Test
    public void testYamlNodeConvertedValues() {
        YamlNode root = new YamlNode();
        root.set("key1", 12);
        root.set("key2", "13");
        root.set("key3", "invalid");
        root.set("key4", Long.valueOf(14));
        Map<String, Integer> values = root.getValues(Integer.class);
        assertEquals(3, root.getValues().size());
        assertEquals(3, values.size());
        assertEquals(Integer.valueOf(12), values.get("key1"));
        assertEquals(Integer.valueOf(13), values.get("key2"));
        assertNull(values.get("key3"));
        assertEquals(Integer.valueOf(14), values.get("key4"));
    }

    @Test
    public void testYamlNodeValues() {
        YamlNode root = new YamlNode();
        root.set("key1", "value1");
        root.set("key2", "value2");
        root.set("key3.key1", "subkey1");;
        root.set("key3.key2", "subkey2");;
        Map<String, Object> values = root.getValues();
        assertEquals(3, values.size());

        assertTrue(values.containsKey("key1"));
        assertTrue(values.containsKey("key2"));
        assertTrue(values.containsKey("key3"));
        assertFalse(values.containsKey("key4"));

        assertTrue(values.containsValue("value1"));
        assertTrue(values.containsValue("value2"));
        assertTrue(values.containsValue(root.getNode("key3")));
        assertFalse(values.containsValue("value3"));

        assertEquals("value1", values.get("key1"));
        assertEquals("value2", values.get("key2"));
        assertEquals(root.getNode("key3"), values.get("key3"));
        assertNull(values.get("key4"));

        Map.Entry<String, Object> e;
        Iterator<Map.Entry<String, Object>> entriesIter = values.entrySet().iterator();
        assertTrue(entriesIter.hasNext());
        e = entriesIter.next();
        assertEquals("key1", e.getKey());
        assertEquals("value1", e.getValue());
        e = entriesIter.next();
        assertEquals("key2", e.getKey());
        assertEquals("value2", e.getValue());
        e = entriesIter.next();
        assertEquals("key3", e.getKey());
        assertEquals(root.getNode("key3"), e.getValue());
        assertFalse(entriesIter.hasNext());
    }

    @Test
    public void testYamlNodeKeys() {
        YamlNode root = new YamlNode();
        root.set("key1", "value1");
        root.set("key2", "value2");
        root.set("key3.key1", "subkey1");;
        root.set("key3.key2", "subkey2");;
        Set<String> keys = root.getKeys();
        assertEquals(3, keys.size());

        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertTrue(keys.contains("key3"));
        assertFalse(keys.contains("key4"));

        Iterator<String> iter = keys.iterator();
        assertTrue(iter.hasNext());
        assertEquals("key1", iter.next());
        assertEquals("key2", iter.next());
        assertEquals("key3", iter.next());
        assertFalse(iter.hasNext());

        assertTrue(keys.remove("key2"));
        assertFalse(keys.contains("key2"));
        assertFalse(root.contains("key2"));
    }

    @Test
    public void testYamlNodeLoadFromString() {
        // Test the (recursive) loading functionality, replacing
        // of original contents and applying of headers.
        YamlNode root = new YamlNode();
        root.set("child.old", 12);
        root.set("child.sub.deeper", "deep");
        root.getNode("child").loadFromString(
                "new_key: new_value\n" +
                "\n" +
                "# Header of sub\n" +
                "sub:\n" +
                "  # Header of sub_new_key\n" +
                "  sub_new_key: sub_new_value\n");

        // Verify state
        assertFalse(root.contains("child.old"));
        assertEquals("new_value", root.get("child.new_key"));
        assertEquals("sub_new_value", root.get("child.sub.sub_new_key"));
        assertEquals("\nHeader of sub", root.getHeader("child.sub"));
        assertEquals("Header of sub_new_key", root.getHeader("child.sub.sub_new_key"));
    }

    @Test
    public void testYamlNodeList() {
        YamlNode root = new YamlNode();
        List<Object> list;

        // Add 3 values and verify
        list = root.getList("list");
        list.add("Value1");
        list.add(12);
        list.add("Value3");
        list = root.getList("list");
        assertEquals(3, list.size());
        assertEquals("Value1", list.get(0));
        assertEquals(Integer.valueOf(12), list.get(1));
        assertEquals("Value3", list.get(2));
        assertEquals("Value1", root.get("list[0]"));
        assertEquals(Integer.valueOf(12), root.get("list[1]"));
        assertEquals("Value3", root.get("list[2]"));
        assertTrue(root.contains("list[0]"));
        assertTrue(root.contains("list[1]"));
        assertTrue(root.contains("list[2]"));

        // Remove value in the middle, verify all is good
        list.remove(1);
        list = root.getList("list");
        assertEquals(2, list.size());
        assertEquals("Value1", list.get(0));
        assertEquals("Value3", list.get(1));
        assertEquals("Value1", root.get("list[0]"));
        assertEquals("Value3", root.get("list[1]"));
        assertFalse(root.contains("list[2]"));

        // Insert value in the middle, verify all is good
        list.add(0, "Value0");
        list = root.getList("list");
        assertEquals(3, list.size());
        assertEquals("Value0", list.get(0));
        assertEquals("Value1", list.get(1));
        assertEquals("Value3", list.get(2));
        assertEquals("Value0", root.get("list[0]"));
        assertEquals("Value1", root.get("list[1]"));
        assertEquals("Value3", root.get("list[2]"));

        // Add a complex node tree
        YamlNode node = new YamlNode();
        node.set("key", "value");
        node.set("nested.key1", "value1");
        node.set("nested.key2", "value2");
        list.add(node);
        assertEquals(4, list.size());
        assertEquals(node, list.get(list.size()-1));
        assertEquals("value", root.get("list[3].key"));
        assertEquals("value1", root.get("list[3].nested.key1"));
        assertEquals("value2", root.get("list[3].nested.key2"));
        assertTrue(root.contains("list[3].key"));
        assertTrue(root.contains("list[3].nested.key1"));
        assertTrue(root.contains("list[3].nested.key2"));
        assertEquals("value1", root.getNode("list[3].nested").get("key1"));
        assertEquals("value2", root.getNode("list[3].nested").get("key2"));
    }

    @Test
    public void testYamlNodeCloneChild() {
        // Create a somewhat complex tree of nodes
        YamlNode root = new YamlNode();
        root.set("child.subchild.name", "value");
        root.set("child.field", 12);
        root.set("child.other", 55);
        root.setHeader("child.field", "Header of field");
        root.setHeader("child", "Header of child");

        // Clone child
        YamlNode child_clone = root.getNode("child").clone();
        assertFalse(child_clone.hasParent());
        assertNull(child_clone.getParent());

        // Make changes to the original, verify the clone child is all good
        root.set("child.field", 22);
        root.set("child.other", 22);
        assertEquals("value", child_clone.get("subchild.name"));
        assertEquals(12, child_clone.get("field"));
        assertEquals(55, child_clone.get("other"));
        assertEquals("Header of field", child_clone.getHeader("field"));
        assertEquals("Header of child", child_clone.getHeader());

        // Make changes to the clone, verify the clone is all good
        child_clone.set("field", 10);
        assertEquals(22, root.get("child.field"));

        // Extra check: 'child' node should not be equal
        assertNotEquals(root.getNode("child"), child_clone);
    }

    @Test
    public void testYamlNodeClone() {
        // Create a somewhat complex tree of nodes
        YamlNode root = new YamlNode();
        root.set("child.subchild.name", "value");
        root.set("child.field", 12);
        root.set("child.other", 55);
        root.setHeader("child.field", "Header of field");
        root.setHeader("child", "Header of child");

        // Clone it
        YamlNode root_clone = root.clone();
        assertFalse(root_clone.hasParent());
        assertNull(root_clone.getParent());

        // Make changes to the original, verify the clone is all good
        root.set("child.field", 22);
        root.set("child.other", 22);
        assertEquals("value", root_clone.get("child.subchild.name"));
        assertEquals(12, root_clone.get("child.field"));
        assertEquals(55, root_clone.get("child.other"));
        assertEquals("Header of field", root_clone.getHeader("child.field"));
        assertEquals("Header of child", root_clone.getHeader("child"));

        // Make changes to the clone, verify the clone is all good
        root_clone.set("child.field", 10);
        assertEquals(22, root.get("child.field"));

        // Extra check: 'child' node should not be equal
        assertNotEquals(root.getNode("child"), root_clone.getNode("child"));
    }

    @Test
    public void testYamlNodeToString() {
        // Test various trees and changes to them
        YamlNode root = new YamlNode();
        YamlNode child = root.getNode("subnode");
        child.getNode("node").set("hello", 12);
        assertEquals("node:\n" +
                     "  hello: 12\n",
                     child.toString());

        // Test vector in subnode
        child.getNode("node").set("hello", 15);
        child.getNode("node").set("wooo", new Vector(1,2,3));
        child.set("other", "hey");
        assertEquals("node:\n" +
                     "  hello: 15\n" +
                     "  wooo:\n" +
                     "    ==: Vector\n" +
                     "    x: 1.0\n" +
                     "    y: 2.0\n" +
                     "    z: 3.0\n" +
                     "other: hey\n",
                     child.toString());

        // Test headers, should prompt yaml regeneration
        root.setHeader("This is the root header");
        child.setHeader("\nThis is the child header");
        child.getNode("node").setHeader("This is the header of node");
        assertEquals("#> This is the root header\n" +
                     "\n" +
                     "# This is the child header\n" +
                     "subnode:\n" +
                      "  # This is the header of node\n" +
                      "  node:\n" +
                      "    hello: 15\n" +
                      "    wooo:\n" +
                      "      ==: Vector\n" +
                      "      x: 1.0\n" +
                      "      y: 2.0\n" +
                      "      z: 3.0\n" +
                      "  other: hey\n",
                      root.toString());

        // Change value to a different value, but with the same length
        child.set("node.hello", 12);
        assertEquals("#> This is the root header\n" +
                     "\n" +
                     "# This is the child header\n" +
                     "subnode:\n" +
                     "  # This is the header of node\n" +
                     "  node:\n" +
                     "    hello: 12\n" +
                     "    wooo:\n" +
                     "      ==: Vector\n" +
                     "      x: 1.0\n" +
                     "      y: 2.0\n" +
                     "      z: 3.0\n" +
                     "  other: hey\n",
                     root.toString());

        // Add a node with the 'any' key, which should be written as such
        // Add a node with name '*test' which should be escaped
        child.set("*.key", 12);
        child.set("*.default", true);
        child.set("*test.key1", 32);
        child.set("*test.key2", "value5");
        assertEquals("#> This is the root header\n" +
                     "\n" +
                     "# This is the child header\n" +
                     "subnode:\n" +
                     "  # This is the header of node\n" +
                     "  node:\n" +
                     "    hello: 12\n" +
                     "    wooo:\n" +
                     "      ==: Vector\n" +
                     "      x: 1.0\n" +
                     "      y: 2.0\n" +
                     "      z: 3.0\n" +
                     "  other: hey\n" +
                     "  *:\n" +
                     "    key: 12\n" +
                     "    default: true\n" +
                     "  '*test':\n" +
                     "    key1: 32\n" +
                     "    key2: value5\n",
                     root.toString());
    }

    @Test
    public void testYamlSimpleListToString() {
        YamlNode root = new YamlNode();
        List<Object> list = root.getList("simpleList");
        list.add("Value1");
        list.add("Value2");
        list.add("Value3");

        assertEquals("simpleList:\n" +
                     "  - Value1\n" +
                     "  - Value2\n" +
                     "  - Value3\n",
                     root.toString());
    }

    @Test
    public void testYamlListOfNodesToString() {
        YamlNode root = new YamlNode();
        List<Object> list = root.getList("simpleList");

        YamlNode node1 = new YamlNode();
        node1.set("node1key1", "node1value1");
        node1.set("node1key2", "node1value2");
        list.add(node1);

        YamlNode node2 = new YamlNode();
        node2.set("node2key1", "node2value1");
        node2.set("node2key2", "node2value2");
        list.add(node2);

        YamlNode node3 = new YamlNode();
        node3.set("node3key1", "node3value1");
        node3.set("node3key2", "node3value2");
        list.add(node3);

        assertEquals("simpleList:\n" + 
                     "  - node1key1: node1value1\n" + 
                     "    node1key2: node1value2\n" + 
                     "  - node2key1: node2value1\n" + 
                     "    node2key2: node2value2\n" + 
                     "  - node3key1: node3value1\n" + 
                     "    node3key2: node3value2\n",
                     root.toString());
    }

    @Test
    public void testYamlNestedListToString() {
        YamlNode root = new YamlNode();
        root.set("simpleList", Arrays.asList(
                "value1",
                Arrays.asList("nested1value1", "nested1value2"),
                "value2",
                Arrays.asList(Arrays.asList("nested2deeper1value1", "nested2deeper1value2"), "nested2value2"),
                Arrays.asList("nested3value1", "nested3value2")));

        assertEquals("simpleList:\n" + 
                     "  - value1\n" + 
                     "  - - nested1value1\n" + 
                     "    - nested1value2\n" + 
                     "  - value2\n" + 
                     "  - - - nested2deeper1value1\n" + 
                     "      - nested2deeper1value2\n" + 
                     "    - nested2value2\n" + 
                     "  - - nested3value1\n" + 
                     "    - nested3value2\n",
                     root.toString());
    }

    @Test
    public void testYamlNestedListNodesTostring() {
        YamlNode root = new YamlNode();
        YamlNode node = new YamlNode();
        node.set("key", "value");
        node.set("num", 12);
        node.set("child.key", 22);
        node.set("child.num", -2);
        root.set("simpleList", Arrays.asList(
                "value1",
                node.clone(),
                "value2",
                Arrays.asList(node.clone(), "value3", "value4")));

        assertEquals("simpleList:\n" + 
                     "  - value1\n" + 
                     "  - key: value\n" + 
                     "    num: 12\n" + 
                     "    child:\n" + 
                     "      key: 22\n" + 
                     "      num: -2\n" + 
                     "  - value2\n" + 
                     "  - - key: value\n" + 
                     "      num: 12\n" + 
                     "      child:\n" + 
                     "        key: 22\n" + 
                     "        num: -2\n" + 
                     "    - value3\n" + 
                     "    - value4\n",
                     root.toString());
    }

    @Test
    public void testYamlEmptyNodeToString() {
        YamlNode root = new YamlNode();
        root.set("simpleNode", new YamlNode());
        root.set("simpleNodeNested", Arrays.asList(new YamlNode()));

        assertEquals("simpleNode: {}\n" + 
                     "simpleNodeNested:\n" + 
                     "  - {}\n",
                     root.toString());
    }

    @Test
    public void testYamlEmptyListToString() {
        YamlNode root = new YamlNode();
        root.set("simpleList", Collections.emptyList());
        root.set("simpleListNested", Arrays.asList(Collections.emptyList()));

        assertEquals("simpleList: []\n" + 
                     "simpleListNested:\n" + 
                     "  - []\n",
                     root.toString());
    }

    @Test
    public void testYamlEnumToString() {
        YamlNode root = new YamlNode();
        root.set("permissionTrue", PermissionDefault.TRUE);
        root.set("permissionFalse", PermissionDefault.FALSE);
        root.set("permissionOp", PermissionDefault.OP);
        root.set("gameMode", GameMode.CREATIVE);

        assertEquals("permissionTrue: true\n" +
                     "permissionFalse: false\n" +
                     "permissionOp: op\n" +
                     "gameMode: CREATIVE\n",
                     root.toString());
    }

    @Test
    public void testYamlChildNodeToString() {
        // Checks that doing toString() on a child node uses proper indentation
        // Also verifies that doing root toString() after is not corrupted by this
        YamlNode root = new YamlNode();
        root.set("child.subchild.key", "value1");
        root.set("child.subchild.node.key", "value2");
        root.set("child.subchild.node.num", 23);

        assertEquals("key: value1\n" +
                     "node:\n" +
                     "  key: value2\n" +
                     "  num: 23\n",
                     root.getNode("child.subchild").toString());

        assertEquals("child:\n" +
                     "  subchild:\n" +
                     "    key: value1\n" +
                     "    node:\n" +
                     "      key: value2\n" +
                     "      num: 23\n",
                     root.toString());

        assertEquals("key: value1\n" +
                     "node:\n" +
                     "  key: value2\n" +
                     "  num: 23\n",
                     root.getNode("child.subchild").toString());
    }

    @Test
    public void testYamlNodeValue() {
        YamlNode root = new YamlNode();

        // Set to store a new value, verify it has been set
        // The functions get(), getValues() and getKeys() should all work
        assertNull(root.get("key"));
        root.set("key", "value");
        assertTrue(root.contains("key"));
        assertEquals("value", root.get("key"));
        assertEquals(1, root.getKeys().size());
        assertEquals("key", root.getKeys().iterator().next());
        assertEquals(1, root.getValues().size());
        assertEquals("value", root.getValues().values().iterator().next());

        // Change value
        // The functions get(), getValues() and getKeys() should all work
        root.set("key", "new_value");
        assertTrue(root.contains("key"));
        assertEquals("new_value", root.get("key"));
        assertEquals(1, root.getKeys().size());
        assertEquals("key", root.getKeys().iterator().next());
        assertEquals(1, root.getValues().size());
        assertEquals("new_value", root.getValues().values().iterator().next());

        // Remove the value
        root.remove("key");
        assertFalse(root.contains("key"));
        assertNull(root.get("key"));
        assertEquals(0, root.getValues().size());
        assertEquals(0, root.getKeys().size());

        // Sets the value if absent, succeeds the first time, fails second time
        assertTrue(root.setIfAbsent("key", "InitialValue"));
        assertFalse(root.setIfAbsent("key", "Changed"));
        assertEquals("InitialValue", root.get("key"));
    }

    @Test
    public void testYamlNodeValueDeep() {
        YamlNode root = new YamlNode();

        // Set to store a new value, verify it has been set
        // The functions get(), getValues() and getKeys() should all work
        assertNull(root.get("key.deeper"));
        root.set("key.deeper", "value");
        assertTrue(root.contains("key.deeper"));
        assertEquals("value", root.get("key.deeper"));
        assertEquals(1, root.getKeys().size());
        assertEquals("key", root.getKeys().iterator().next());
        assertEquals(1, root.getValues().size());
        assertTrue(root.getValues().values().iterator().next() instanceof YamlNode);

        // Change value
        // The functions get(), getValues() and getKeys() should all work
        root.set("key.deeper", "new_value");
        assertTrue(root.contains("key.deeper"));
        assertEquals("new_value", root.get("key.deeper"));
        assertEquals(1, root.getKeys().size());
        assertEquals("key", root.getKeys().iterator().next());
        assertEquals(1, root.getValues().size());
        assertTrue(root.getValues().values().iterator().next() instanceof YamlNode);

        // Remove the key, which also removes deeper
        root.remove("key");
        assertFalse(root.contains("key.deeper"));
        assertFalse(root.contains("key"));
        assertNull(root.get("key.deeper"));
        assertNull(root.get("key"));
        assertEquals(0, root.getValues().size());
        assertEquals(0, root.getKeys().size());
    }

    @Test
    public void testYamlDeserializer() {
        // Deserialize a String which tests the following properties:
        // - Nodes, subnodes, lists and lists inside lists
        // - Using the key '*' for a node ('any' in permissions)
        // - Color code characters with ampersand
        // - Double ampersand escaping
        // - Headers, with and without newline prefix
        // - There's a tab used for a full indent position, both at pos=0 and with spaces left of it
        // - A numeric key is used, which node setValues() should be able to deal with
        YamlDeserializer.Output output = YamlDeserializer.INSTANCE.deserialize(
                "#> Main file header\n" +
                "\n" +
                "# Header of hello\n" +
                "hello:\n" +
                "  # Header of cool\n" +
                "  cool:\n" +
                "\t\t# Header of text\n" +
                "\n" +
                "    # With a line gap in-between\n" +
                "  \ttext: '&cColored text&&ampersand'\n" +
                "\n" +
                "    value: -5\n" +
                "    123: 55\n" +
                "  *:\n" +
                "    - value1\n" +
                "    - value2\n" +
                "    - - value3\n" +
                "      - value4\n");

        // Verification
        verifyDeserializeResults(output);
    }

    @Test
    public void testYamlDeserializerOddIndent() {
        // Check that an odd indent (3 spaces) also works
        YamlDeserializer.Output output = YamlDeserializer.INSTANCE.deserialize(
                "#> Main file header\n" +
                "\n" +
                "# Header of hello\n" +
                "hello:\n" +
                "   # Header of cool\n" +
                "   cool:\n" +
                "\t\t# Header of text\n" +
                "\n" +
                "      # With a line gap in-between\n" +
                "   \ttext: '&cColored text&&ampersand'\n" +
                "\n" +
                "      value: -5\n" +
                "      123:  55\n" +
                "   *:\n" +
                "      - value1\n" +
                "      - value2\n" +
                "      -  - value3\n" +
                "         - value4\n");

        // Verification
        verifyDeserializeResults(output);
    }

    @Test
    public void testYamlDeserializeFailure() {
        // Checks that when the YAML deserializer fails to deserialize something it does not fail
        // all future deserialization attempts. This is forced by inputting a very large null-padded
        // string, which should fill any temporary buffers with invalid data.

        // Deserialize some invalid YAML
        try {
            StringBuilder ss = new StringBuilder();
            ss.append("# This is a header\n" +
                    "\n" +
                    "key:\n" +
                    "  subkey: value");
            for (int n = 0; n < 100000; n++) {
                ss.append("\0\0\0\0");
            }

            YamlDeserializer.INSTANCE.deserialize(ss.toString());

            fail("Should not succeed in deserializing");
        } catch (YAMLException ex) {
        }

        // Deserialize some valid YAML
        YamlDeserializer.Output output = YamlDeserializer.INSTANCE.deserialize(
                "#> Main file header\n" +
                "\n" +
                "# Header of hello\n" +
                "hello:\n" +
                "  # Header of cool\n" +
                "  cool:\n" +
                "\t\t# Header of text\n" +
                "\n" +
                "    # With a line gap in-between\n" +
                "  \ttext: '&cColored text&&ampersand'\n" +
                "\n" +
                "    value: -5\n" +
                "    123: 55\n" +
                "  *:\n" +
                "    - value1\n" +
                "    - value2\n" +
                "    - - value3\n" +
                "      - value4\n");

        // Verification
        verifyDeserializeResults(output);
    }

    private void verifyDeserializeResults(YamlDeserializer.Output output) {
        // Check all values
        Map<?, ?> hello = (Map<?, ?>) output.root.get("hello");
        Map<?, ?> cool = (Map<?, ?>) hello.get("cool");
        List<?> anyList = (List<?>) hello.get("*");
        assertEquals("§cColored text&ampersand", (String) cool.get("text"));
        assertEquals(Integer.valueOf(-5), (Integer) cool.get("value"));
        assertEquals(Integer.valueOf(55), (Integer) cool.get(Integer.valueOf(123)));
        assertEquals(3, anyList.size());
        assertEquals("value1", (String) anyList.get(0));
        assertEquals("value2", (String) anyList.get(1));
        List<?> anyListSubList = (List<?>) anyList.get(2);
        assertEquals(2, anyListSubList.size());
        assertEquals("value3", (String) anyListSubList.get(0));
        assertEquals("value4", (String) anyListSubList.get(1));

        // Check all headers
        assertEquals("Main file header", output.headers.get(YamlPath.create("")));
        assertEquals("\nHeader of hello", output.headers.get(YamlPath.create("hello")));
        assertEquals("Header of cool", output.headers.get(YamlPath.create("hello.cool")));
        assertEquals("Header of text\n\nWith a line gap in-between", output.headers.get(YamlPath.create("hello.cool.text")));
        assertEquals("\n", output.headers.get(YamlPath.create("hello.cool.value")));

        // Serialize into a YamlNode
        YamlNode root = new YamlNode();
        root.loadDeserializerOutput(output);

        // Verify YamlNode values are initialized according to what is in the output
        assertEquals("§cColored text&ampersand", root.get("hello.cool.text"));
        assertEquals(Integer.valueOf(-5), root.get("hello.cool.value"));
        assertEquals(Integer.valueOf(55), root.get("hello.cool.123"));
        assertEquals("value1", root.get("hello.*[0]"));
        assertEquals("value2", root.get("hello.*[1]"));
        assertEquals("value3", root.get("hello.*[2][0]"));
        assertEquals("value4", root.get("hello.*[2][1]"));

        // Verify YamlNode headers are initialized according to what is in the output
        assertEquals("Main file header", root.getHeader());
        assertEquals("\nHeader of hello", root.getHeader("hello"));
        assertEquals("Header of cool", root.getHeader("hello.cool"));
        assertEquals("Header of text\n\nWith a line gap in-between", root.getHeader("hello.cool.text"));
        assertEquals("\n", root.getHeader("hello.cool.value"));
    }

    @Test
    public void testYamlSerializerWithIndent() {
        YamlSerializer serializer = YamlSerializer.INSTANCE;
        assertEquals("hello, world!\n", serializer.serialize("hello, world!", "", 1));
        assertEquals("hello, world!\n", serializer.serialize("hello, world!", "", 1));
        assertEquals("  hello, world!\n", serializer.serialize("hello, world!", "", 2));
        assertEquals("    hello, world!\n", serializer.serialize("hello, world!", "", 3));
        assertEquals("      hello, world!\n", serializer.serialize("hello, world!", "", 4));
    }

    @Test
    public void testYamlSerializer() {
        // Verify that the Serializer is able to serialize basic values and perform escaping rules
        YamlSerializer serializer = YamlSerializer.INSTANCE;
        assertEquals("hello, world!\n", serializer.serialize("hello, world!"));
        assertEquals("23\n", serializer.serialize(23));
        assertEquals("key: value\n", serializer.serialize(Collections.singletonMap("key", "value")));
        assertEquals("key: 12\n", serializer.serialize(Collections.singletonMap("key", 12)));
        assertEquals("'1': '*'\n", serializer.serialize(Collections.singletonMap("1", "*")));
        assertEquals("1: value\n", serializer.serialize(Collections.singletonMap("1", "value")));
        assertEquals("vector:\n" +
                "  ==: Vector\n" +
                "  x: 1.0\n" +
                "  y: 2.0\n" +
                "  z: 3.0\n",
                serializer.serialize(Collections.singletonMap("vector", new Vector(1, 2, 3))));

        // Verify proper functioning of the header option with only a single line
        assertEquals("# this is the first line\n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "this is the first line", 1));

        // Verify proper functioning of the header option with only a single line, with a newline at the start for whitespace
        assertEquals("\n" +
                     "# this is the first line\n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "\nthis is the first line", 1));

        // Verify proper functioning of the header option with multiple lines, with a newline at the start for whitespace
        assertEquals("\n" + 
                     "# this is the first line\n" +
                     "# this is the second line\n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "\nthis is the first line\nthis is the second line", 1));

        // Verify proper functioning of newline gaps between two lines
        assertEquals("# this is the first line\n" +
                     "# \n" +
                     "# \n" +
                     "# this is the second line\n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "this is the first line\n\n\nthis is the second line", 1));

        // Trailing newline
        assertEquals("# this is the first line\n" +
                     "# \n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                        "this is the first line\n", 1));

        // Just a newline character
        assertEquals("\n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"), "\n", 1));

        // Newlines at the beginning and end
        assertEquals("\n" +
                     "# Header\n" +
                     "# \n" +
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"), "\nHeader\n", 1));

        // Multiline string values
        assertEquals("key: |-\n" +
                     "  this is\n" +
                     "  a multiline\n" +
                     "  string value\n",
                     serializer.serialize(Collections.singletonMap("key", "this is\na multiline\nstring value"), "", 1));

        // Multiline string key
        assertEquals("? |-\n" +
                     "  this is\n" +
                     "  a multiline\n" +
                     "  string key\n" +
                     ": value\n",
                     serializer.serialize(Collections.singletonMap("this is\na multiline\nstring key", "value"), "", 1));
    }

    @Test
    public void testYamlSerializerIndent() {
        // Verify that the Serializer uses correct indentation based on the input
        // Indent level 0 and 1 are the same, with the only difference being that
        // at indent level 0 a #> header instead of # is used.
        // Indent levels 2 and up should show a visible indentation.
        YamlSerializer serializer = YamlSerializer.INSTANCE;

        // Simple text value
        assertEquals("hello, world!\n", serializer.serialize("hello, world!", "", 0));
        assertEquals("hello, world!\n", serializer.serialize("hello, world!", "", 1));
        assertEquals("  hello, world!\n", serializer.serialize("hello, world!", "", 2));
        assertEquals("    hello, world!\n", serializer.serialize("hello, world!", "", 3));

        // Key: value pair
        assertEquals("key: 12\n", serializer.serialize(Collections.singletonMap("key", 12), "", 0));
        assertEquals("key: 12\n", serializer.serialize(Collections.singletonMap("key", 12), "", 1));
        assertEquals("  key: 12\n", serializer.serialize(Collections.singletonMap("key", 12), "", 2));
        assertEquals("    key: 12\n", serializer.serialize(Collections.singletonMap("key", 12), "", 3));

        // Multi-line value
        assertEquals("==: Vector\n" +
                     "x: 1.0\n" +
                     "y: 2.0\n" +
                     "z: 3.0\n",
                     serializer.serialize(new Vector(1, 2, 3), "", 0));
        assertEquals("==: Vector\n" +
                     "x: 1.0\n" +
                     "y: 2.0\n" +
                     "z: 3.0\n",
                     serializer.serialize(new Vector(1, 2, 3), "", 1));
        assertEquals("  ==: Vector\n" +
                     "  x: 1.0\n" +
                     "  y: 2.0\n" +
                     "  z: 3.0\n",
                     serializer.serialize(new Vector(1, 2, 3), "", 2));
        assertEquals("    ==: Vector\n" +
                     "    x: 1.0\n" +
                     "    y: 2.0\n" +
                     "    z: 3.0\n",
                     serializer.serialize(new Vector(1, 2, 3), "", 3));

        // Multi-line key: value pair
        assertEquals("vector:\n" +
                     "  ==: Vector\n" +
                     "  x: 1.0\n" +
                     "  y: 2.0\n" +
                     "  z: 3.0\n",
                     serializer.serialize(Collections.singletonMap("vector", new Vector(1, 2, 3)), "", 0));
        assertEquals("vector:\n" +
                     "  ==: Vector\n" +
                     "  x: 1.0\n" +
                     "  y: 2.0\n" +
                     "  z: 3.0\n",
                     serializer.serialize(Collections.singletonMap("vector", new Vector(1, 2, 3)), "", 1));
        assertEquals("  vector:\n" +
                     "    ==: Vector\n" +
                     "    x: 1.0\n" +
                     "    y: 2.0\n" +
                     "    z: 3.0\n",
                     serializer.serialize(Collections.singletonMap("vector", new Vector(1, 2, 3)), "", 2));
        assertEquals("    vector:\n" +
                     "      ==: Vector\n" +
                     "      x: 1.0\n" +
                     "      y: 2.0\n" +
                     "      z: 3.0\n",
                     serializer.serialize(Collections.singletonMap("vector", new Vector(1, 2, 3)), "", 3));

        // Multiline string values
        assertEquals("key: |-\n" +
                     "  this is\n" +
                     "  a multiline\n" +
                     "  string value\n",
                     serializer.serialize(Collections.singletonMap("key", "this is\na multiline\nstring value"), "", 0));
        assertEquals("key: |-\n" +
                     "  this is\n" +
                     "  a multiline\n" +
                     "  string value\n",
                     serializer.serialize(Collections.singletonMap("key", "this is\na multiline\nstring value"), "", 1));
        assertEquals("  key: |-\n" +
                     "    this is\n" +
                     "    a multiline\n" +
                     "    string value\n",
                     serializer.serialize(Collections.singletonMap("key", "this is\na multiline\nstring value"), "", 2));
        assertEquals("    key: |-\n" +
                     "      this is\n" +
                     "      a multiline\n" +
                     "      string value\n",
                     serializer.serialize(Collections.singletonMap("key", "this is\na multiline\nstring value"), "", 3));

        // Simple value with header
        assertEquals("#> Simple header\n" +
                     "key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header", 0));
        assertEquals("# Simple header\n" +
                     "key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header", 1));
        assertEquals("  # Simple header\n" +
                     "  key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header", 2));
        assertEquals("    # Simple header\n" +
                     "    key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header", 3));

        // Simple value with multiline header
        assertEquals("#> Simple header\n" +
                     "#> With two lines\n" +
                     "key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header\nWith two lines", 0));
        assertEquals("# Simple header\n" +
                     "# With two lines\n" +
                     "key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header\nWith two lines", 1));
        assertEquals("  # Simple header\n" +
                     "  # With two lines\n" +
                     "  key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header\nWith two lines", 2));
        assertEquals("    # Simple header\n" +
                     "    # With two lines\n" +
                     "    key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "Simple header\nWith two lines", 3));

        // Simple value with a header that has the first 2 lines empty (whitespace)
        // We expect the newlines to not be indented, but that the #-prefixed headers are
        assertEquals("\n" +
                     "\n" +
                     "#> Simple header\n" +
                     "key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "\n\nSimple header", 0));
        assertEquals("\n" +
                     "\n" +
                     "# Simple header\n" +
                     "key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "\n\nSimple header", 1));
        assertEquals("\n" +
                     "\n" +
                     "  # Simple header\n" +
                     "  key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "\n\nSimple header", 2));
        assertEquals("\n" +
                     "\n" +
                     "    # Simple header\n" +
                     "    key: 12\n",
                     serializer.serialize(Collections.singletonMap("key", 12), "\n\nSimple header", 3));
    }

    // Used by saveToFileAsync()
    @Ignore
    @Test
    public void stressTestAsyncTextWriter() {
        StringBuilder testData = new StringBuilder();
        int numLines = 10000;
        for (int i = 0; i < numLines; i++) {
            testData.append("123456789\n");
        }
        String testString = testData.toString();

        java.io.File testFile = new java.io.File("test_async_writer.dat");
        int n = 0;
        int k = 0;
        while (true) {
            if (++n > 1000) {
                System.out.println("RUN #" + ++k);
            }
            try {
                AsyncTextWriter.write(testFile, testString).get();
            } catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }

            List<String> lines;
            try {
                lines = Files.readAllLines(testFile.toPath());
            } catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
            assertEquals(numLines, lines.size());
        }
    }

    @Test
    public void testYamlPathJoin() {
        // Simple case
        assertEquals(YamlPath.create("one.two.three.four"), YamlPath.join(
                YamlPath.create("one.two"),
                YamlPath.create("three.four")));

        // Single words
        assertEquals(YamlPath.create("one.two"), YamlPath.join(
                YamlPath.create("one"),
                YamlPath.create("two")));

        // With ROOT
        assertEquals(YamlPath.create("one.two"), YamlPath.join(
                YamlPath.create("one.two"), YamlPath.ROOT));
        assertEquals(YamlPath.create("one.two"), YamlPath.join(
                YamlPath.ROOT, YamlPath.create("one.two")));
    }

    @Test
    public void testYamlPathMakeRelative() {
        // Simple case
        assertEquals(YamlPath.create("three.four"),
                YamlPath.create("one.two.three.four")
                        .makeRelative(YamlPath.create("one.two")));

        // Root result
        assertEquals(YamlPath.ROOT, YamlPath.create("one.two")
                .makeRelative(YamlPath.create("one.two")));

        // Single word result
        assertEquals(YamlPath.create("three"),
                YamlPath.create("one.two.three")
                        .makeRelative(YamlPath.create("one.two")));

        // Invalid / null result
        assertEquals(null, YamlPath.create("one.two.three")
                .makeRelative(YamlPath.create("six.two")));
        assertEquals(null, YamlPath.create("one.two.three")
                .makeRelative(YamlPath.create("one.seven")));

        // Just in case, also check relative list indices
        assertEquals(YamlPath.create("three.four[4]"),
                YamlPath.create("one.two[2].three.four[4]")
                        .makeRelative(YamlPath.create("one.two[2]")));
    }

    @Test
    public void testYamlPath() {
        // Check root node
        YamlPath root = YamlPath.create("root");
        assertEquals("root", root.name());
        assertEquals("root", root.toString());

        // Check child node constructed using root node
        YamlPath child = root.child("child");
        assertEquals("child", child.name());
        assertEquals("root.child", child.toString());
        assertEquals(root, child.parent());
        assertNotEquals(root, child);

        // Check child node constructed using yaml path
        YamlPath child_using_yaml_path = child.child(YamlPath.create("subnode.sub"));
        assertEquals("sub", child_using_yaml_path.name());
        assertEquals("root.child.subnode.sub", child_using_yaml_path.toString());

        // Check a different root.child node that is created
        // The child should equal the previous root.child path
        // The parents should be equal too
        YamlPath root_and_child = YamlPath.create("root.child");
        assertEquals("child", root_and_child.name());
        assertEquals("root.child", root_and_child.toString());
        assertEquals("root", root_and_child.parent().name());
        assertEquals(child, root_and_child);
        assertEquals(child.hashCode(), root_and_child.hashCode());
        assertEquals(child.parent(), root_and_child.parent());
        assertNotEquals(root, root_and_child);

        // Test a multi-length path of different segments
        YamlPath multi_path = YamlPath.create("root.child.deeper");
        assertEquals("deeper", multi_path.name());
        assertEquals("root.child.deeper", multi_path.toString());
        assertEquals(root_and_child, multi_path.parent());
    }

    @Test
    public void testYamlListPath() {
        // Test a path with a list element inside
        YamlPath list_path = YamlPath.create("root.child[12]");
        assertEquals("12", list_path.name());
        assertEquals("root.child", list_path.parent().toString());
        assertFalse(list_path.parent().isListElement());
        assertEquals(-1, list_path.parent().listIndex());
        assertEquals("root.child[12]", list_path.toString());
        assertTrue(list_path.isListElement());
        assertEquals(12, list_path.listIndex());

        // Create a child of this path, which is a normal node
        YamlPath list_path_child = list_path.child("name");
        assertEquals("name", list_path_child.name());
        assertEquals("root.child[12].name", list_path_child.toString());

        // List child of a list child (if this is even possible in yaml)
        YamlPath list_path_array_child = list_path.child("[16]");
        assertEquals("16", list_path_array_child.name());
        assertEquals("root.child[12][16]", list_path_array_child.toString());
        assertTrue(list_path_array_child.isListElement());
        assertTrue(list_path_array_child.parent().isListElement());
        assertFalse(list_path_array_child.parent().parent().isListElement());
        assertFalse(list_path_array_child.parent().parent().parent().isListElement());
        assertEquals(16, list_path_array_child.listIndex());

        // Creating a deeply nested list child from String
        YamlPath deep_list_path = YamlPath.create("root.child[12][13].sub[15].name");
        assertEquals("root.child[12][13].sub[15].name", deep_list_path.toString());
        assertEquals("name", deep_list_path.name());
        assertEquals(15, deep_list_path.parent().listIndex());
        assertEquals("sub", deep_list_path.parent().parent().name());
        assertEquals(13, deep_list_path.parent().parent().parent().listIndex());
        assertEquals(12, deep_list_path.parent().parent().parent().parent().listIndex());
        assertEquals("child", deep_list_path.parent().parent().parent().parent().parent().name());
        assertEquals("root", deep_list_path.parent().parent().parent().parent().parent().parent().name());

        // Test index constructor
        YamlPath root_child = YamlPath.create("root.child");
        YamlPath list_path_by_name = root_child.child("[22]");
        YamlPath list_path_by_index = root_child.listChild(22);
        assertEquals(list_path_by_name, list_path_by_index);
        assertEquals(list_path_by_name.name(), list_path_by_index.name());
        assertEquals(list_path_by_name.hashCode(), list_path_by_index.hashCode());
        assertTrue(list_path_by_index.isListElement());
        assertEquals(22, list_path_by_index.listIndex());
    }

    @Test
    public void testYamlAssignNodeNameChange() {
        YamlNode root = new YamlNode();
        YamlNode child = new YamlNode();
        child.set("key", "value");
        assertEquals("", child.getName());
        assertEquals("", root.getName());

        // Store under 'child', should bind it to root and set name
        root.set("child", child);
        assertEquals("child", child.getName());
        assertEquals("", root.getName());

        // Also store under 'new_child'. Should clone the original
        // 'child' node and have child name be 'new_child' instead.
        root.set("new_child", child);
        assertEquals("new_child", child.getName());
        assertEquals("", root.getName());
        assertEquals("child", root.getNode("child").getName());
        assertEquals("new_child", root.getNode("new_child").getName());

        // Removing child should not affect new_child
        root.remove("child");
        assertEquals("new_child", child.getName());
        assertEquals("", root.getName());

        // Removing new_child should change name to empty
        root.remove("new_child");
        assertEquals("", child.getName());
        assertEquals("", root.getName());

        // Assign to new root
        YamlNode new_root = new YamlNode();
        new_root.set("new_root_child", child);
        assertEquals("new_root_child", child.getName());
    }

    @Test
    public void testYamlCloneInto() {
        YamlNode target = new YamlNode();
        target.set("a", "a");
        target.set("b", "b");
        target.set("c.a", "ca");
        target.set("c.b", "cb");
        target.set("c.c.a", "cca");

        YamlNode source = new YamlNode();
        source.set("a", "a2"); // Updates a field
        source.set("d", "d"); // Adds a field
        source.set("c.c.a", "cca2"); // Updates a deep field

        source.cloneInto(target);

        assertEquals("a2", target.get("a"));
        assertEquals("b", target.get("b"));
        assertEquals("ca", target.get("c.a"));
        assertEquals("cb", target.get("c.b"));
        assertEquals("cca2", target.get("c.c.a"));
        assertEquals("d", target.get("d"));
    }

    @Test
    public void testYamlCloneIntoFilter() {
        YamlNode target = new YamlNode();
        target.set("a", "a");
        target.set("b", "b");
        target.set("c", "c");

        YamlNode source = new YamlNode();
        source.set("b", "bb");
        source.set("d", "dd");

        // We expect:
        // - 'a' should stay the same
        // - 'b' should be updated to bb
        // - 'c' should be unchanged (is not in source)
        // - 'd' should NOT be added
        source.cloneIntoExcept(target, Arrays.asList("a", "d"));

        assertEquals("a", target.get("a"));
        assertEquals("bb", target.get("b"));
        assertEquals("c", target.get("c"));
        assertFalse(target.contains("d"));
    }

    @Test
    public void testYamlSetTo() {
        YamlNode target = new YamlNode();
        target.set("a", "Ta");
        target.set("b", "Tb");
        target.set("c.a", "Tca");
        target.set("c.b", "Tcb");
        target.set("c.a.a", "Tcaa");

        YamlNode source = new YamlNode();
        source.set("a", "Sa");
        source.set("c.a", "Sca");
        source.set("c.b.a", "Scb");

        target.setTo(source);

        // Check exactly the same yaml
        assertEquals(source.toString(), target.toString());

        // Check changing source doesn't change target
        source.set("c.a", "Sca2");
        assertEquals("Sca", target.get("c.a"));
    }

    @Test
    public void testYamlSetToFilter() {
        YamlNode target = new YamlNode();
        target.set("a", "a");
        target.set("b", "b");
        target.set("c", "c");

        YamlNode source = new YamlNode();
        source.set("b", "bb");
        source.set("d", "dd");

        // We expect:
        // - 'a' should stay the same
        // - 'b' should be updated to bb
        // - 'c' should be removed
        // - 'd' should NOT be added
        target.setToExcept(source, Arrays.asList("a", "d"));

        assertEquals("a", target.get("a"));
        assertEquals("bb", target.get("b"));
        assertFalse(target.contains("c"));
        assertFalse(target.contains("d"));
    }

    @Test
    public void testYamlCloneIntoRelativePaths() {
        YamlNode target = new YamlNode();
        YamlNode target_child = target.getNode("a");
        target_child.set("key", "value");
        target_child.set("ignore", "value2");

        YamlNode source = new YamlNode();
        YamlNode source_child = source.getNode("a");
        source_child.set("key", "Tvalue");
        source_child.set("ignore", "Tvalue2");

        source_child.cloneInto(target_child, path -> {
            assertTrue(path.toString().equals("key") || path.toString().equals("ignore"));
            return path.toString().equals("key");
        });

        assertEquals("Tvalue", target.get("a.key"));
        assertEquals("value2", target.get("a.ignore"));
    }
}
