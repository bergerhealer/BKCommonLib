package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Collections;

import org.bukkit.util.Vector;
import org.junit.Test;

import com.bergerkiller.bukkit.common.config.yaml.YamlNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.config.yaml.YamlSerializer;

public class YamlTest {

    /*
    @Test
    public void testYamlEntryTree() {
        // Create the entries
        YamlEntry root = new YamlEntry();
        root.setName("root");
        root.setHeader("Header of root");
        YamlEntry child1 = new YamlEntry();
        child1.setName("child1");
        child1.setValue("child1value");
        child1.setHeader("Header of child 1");
        YamlEntry child2 = new YamlEntry();
        child2.setName("child2");
        child2.setValue("child2value");
        child2.setHeader("\nHeader of child 2 with newline in front");
        YamlEntry child3 = new YamlEntry();
        child3.setName("child3");
        child3.setValue("child3value");

        // Link them together
        YamlNode rootNode = new YamlNode();
        rootNode.firstChild = child1;
        root.setValue(rootNode);
        child1.setNextSibling(child2);
        child2.setNextSibling(child3);

        // Get yaml
        //System.out.println(root.getYaml());
    }

    @Test
    public void testYamlEntry() {
        YamlEntry entry = new YamlEntry();

        // Verify correct workings of name, value and header properties
        entry.setName("the_name");
        assertEquals("the_name", entry.getName());
        entry.setValue("hello, world!");
        assertEquals("hello, world!", entry.getValue());
        assertEquals("", entry.getHeader());
        entry.setHeader("This is a header\nConsisting of two lines");
        assertEquals("This is a header\nConsisting of two lines", entry.getHeader());

        // Verify that yaml() produces the correct expected output (prefix the header)
        assertEquals("# This is a header\n" + 
                "# Consisting of two lines\n" + 
                "the_name: hello, world!\n",
                entry.getYaml());

        // Change the value, check yaml changes accordingly
        entry.setValue(Integer.valueOf(78));
        assertEquals("# This is a header\n" + 
                "# Consisting of two lines\n" + 
                "the_name: 78\n",
                entry.getYaml());

        // Change the name, check yaml changes accordingly
        entry.setName("new_name");
        assertEquals("# This is a header\n" + 
                "# Consisting of two lines\n" + 
                "new_name: 78\n",
                entry.getYaml());

        // Change the header, check yaml changes accordingly
        entry.setHeader("This is a new header");
        assertEquals("# This is a new header\n" + 
                "new_name: 78\n",
                entry.getYaml());

        // Change value to an empty list. Check this works.
        entry.setValue(Collections.emptyList());
        assertEquals("# This is a new header\n" +
                     "new_name: []\n",
                     entry.getYaml());

        // Change value to a List of strings. Check this works.
        entry.setValue(Arrays.asList("item1", "item2", "item3"));
        System.out.println(entry.getYaml());
    }
    */

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

        // Make changes to the original, verify the clone it all good
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
    public void testYamlSerializer() {
        // Verify that the Serializer is able to serialize basic values and perform escaping rules
        // For the most part we trust SnakeYaml will do the correct things
        YamlSerializer serializer = YamlSerializer.INSTANCE;
        assertEquals("hello, world!\n", serializer.serialize("hello, world!"));
        assertEquals("23\n", serializer.serialize(23));
        assertEquals("key: value\n", serializer.serialize(Collections.singletonMap("key", "value")));
        assertEquals("'1': '*'\n", serializer.serialize(Collections.singletonMap("1", "*")));
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
        assertEquals("[12]", list_path.name());
        assertEquals("root.child", list_path.parent().toString());
        assertFalse(list_path.parent().isList());
        assertEquals(-1, list_path.parent().listIndex());
        assertEquals("root.child[12]", list_path.toString());
        assertTrue(list_path.isList());
        assertEquals(12, list_path.listIndex());

        // Create a child of this path, which is a normal node
        YamlPath list_path_child = list_path.child("name");
        assertEquals("name", list_path_child.name());
        assertEquals("root.child[12].name", list_path_child.toString());

        // List child of a list child (if this is even possible in yaml)
        YamlPath list_path_array_child = list_path.child("[16]");
        assertEquals("[16]", list_path_array_child.name());
        assertEquals("root.child[12][16]", list_path_array_child.toString());
        assertTrue(list_path_array_child.isList());
        assertTrue(list_path_array_child.parent().isList());
        assertFalse(list_path_array_child.parent().parent().isList());
        assertFalse(list_path_array_child.parent().parent().parent().isList());
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
        assertTrue(list_path_by_index.isList());
        assertEquals(22, list_path_by_index.listIndex());
    }
}
