package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Collections;

import org.bukkit.util.Vector;
import org.junit.Test;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.config.yaml.YamlSerializer;

public class YamlTest {

    @Test
    public void testYamlPath() {
        // Check root node
        YamlPath root = new YamlPath("root");
        assertEquals("root", root.getName());
        assertEquals("root", root.toString());

        // Check child node constructed using root node
        YamlPath child = new YamlPath(root, "child");
        assertEquals("child", child.getName());
        assertEquals("root.child", child.toString());
        assertEquals(root, child.getParent());
        assertNotEquals(root, child);

        // Check a different root.child node that is created
        // The child should equal the previous root.child path
        // The parents should be equal too
        YamlPath root_and_child = new YamlPath("root.child");
        assertEquals("child", root_and_child.getName());
        assertEquals("root.child", root_and_child.toString());
        assertEquals("root", root_and_child.getParent().getName());
        assertEquals(child, root_and_child);
        assertEquals(child.hashCode(), root_and_child.hashCode());
        assertEquals(child.getParent(), root_and_child.getParent());
        assertNotEquals(root, root_and_child);

        // Test a multi-length path of different segments
        YamlPath multi_path = new YamlPath("root.child.deeper");
        assertEquals("deeper", multi_path.getName());
        assertEquals("root.child.deeper", multi_path.toString());
        assertEquals(root_and_child, multi_path.getParent());
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
                             "this is the first line"));

        // Verify proper functioning of the header option with only a single line, with a newline at the start for whitespace
        assertEquals("\n" + 
                     "# this is the first line\n" + 
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "\nthis is the first line"));

        // Verify proper functioning of the header option with multiple lines, with a newline at the start for whitespace
        assertEquals("\n" + 
                     "# this is the first line\n" + 
                     "# this is the second line\n" + 
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "\nthis is the first line\nthis is the second line"));

        // Verify proper functioning of newline gaps between two lines
        assertEquals("# this is the first line\n" + 
                     "# \n" + 
                     "# \n" + 
                     "# this is the second line\n" + 
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                             "this is the first line\n\n\nthis is the second line"));

        // Trailing newline
        assertEquals("# this is the first line\n" + 
                     "# \n" + 
                     "key: value\n",
                     serializer.serialize(Collections.singletonMap("key", "value"),
                        "this is the first line\n"));
    }
}
