package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bergerkiller.bukkit.common.inventory.CommonItemMaterials;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;

/**
 * Tests BasicConfiguration YAML stuff
 */
public class ConfigTest {
    static {
        CommonBootstrap.initServer();
    }

    /**
     * Same as the test for YamlNode but done again with ConfigurationNode because
     * there is an instanceof hell. We really want to make sure it's working...
     */
    @Test
    public void testArrayInNodeList() {
        ConfigurationNode root = new ConfigurationNode();
        List<ConfigurationNode> nodes = root.getNodeList("nodes", false);
        {
            ConfigurationNode arrayOne = new ConfigurationNode();
            arrayOne.set("array", Arrays.asList(1, 2, 3));
            nodes.add(arrayOne);
        }
        {
            ConfigurationNode arrayTwo = new ConfigurationNode();
            List<ConfigurationNode> arrayTwoNodes = arrayTwo.getNodeList("nodes", false);
            {
                ConfigurationNode deepNode = new ConfigurationNode();
                deepNode.set("key", "value1");
                deepNode.set("other", 12);
                arrayTwoNodes.add(deepNode);
            }
            {
                ConfigurationNode deepNode = new ConfigurationNode();
                deepNode.set("key", "value2");
                deepNode.set("other", 21);
                arrayTwoNodes.add(deepNode);
            }
            nodes.add(arrayTwo);
        }
        {
            // Array in array in array in array...
            ConfigurationNode arrayThree = new ConfigurationNode();
            arrayThree.set("deepArray", Arrays.asList(
                    Arrays.asList(
                            Arrays.asList(1, 2, 3),
                            Arrays.asList(4, 5, 6),
                            Arrays.asList(7, 8, 9)
                    ),
                    Arrays.asList(
                            Arrays.asList("a", "b"),
                            Arrays.asList("c", "d")
                    )
            ));
            nodes.add(arrayThree);
        }

        String serialized = root.toString();

        assertEquals("" +
                        "nodes:\n" +
                        "  - array:\n" +
                        "      - 1\n" +
                        "      - 2\n" +
                        "      - 3\n" +
                        "  - nodes:\n" +
                        "      - key: value1\n" +
                        "        other: 12\n" +
                        "      - key: value2\n" +
                        "        other: 21\n" +
                        "  - deepArray:\n" +
                        "      - - - 1\n" +
                        "          - 2\n" +
                        "          - 3\n" +
                        "        - - 4\n" +
                        "          - 5\n" +
                        "          - 6\n" +
                        "        - - 7\n" +
                        "          - 8\n" +
                        "          - 9\n" +
                        "      - - - a\n" +
                        "          - b\n" +
                        "        - - c\n" +
                        "          - d\n",
                serialized);

        // Because this YAML is so horrendous also verify it can be parsed again the same way,
        // and that the output is equal to what we put in before
        {
            ConfigurationNode parsedRoot = new ConfigurationNode();
            parsedRoot.loadFromString(serialized);
            assertEquals(root, parsedRoot);

            // Just an extra check, in case equals() is bugging out...
            assertEquals("b", parsedRoot.get("nodes[2].deepArray[1][0][1]"));
        }
    }

    @Test
    public void testYAMLLoadSave() {
        // Don't run <= 1.8.9
        if (Common.evaluateMCVersion("<", "1.9")) {
            return;
        }

        FileConfiguration config = new FileConfiguration("test.yml");
        ItemStack testItem = CommonItemStack.create(CommonItemMaterials.FILLED_MAP, 1)
                .setFilledMapId(5)
                .setCustomNameMessage("") // Works around an annoying bukkit bug...
                .toBukkit();
        //ItemStack testItem = new ItemStack(Material.GRASS_BLOCK);

        config.load();
        config.set("hello", "world");
        config.set("number", 12);
        config.set("anEnum", GameMode.CREATIVE);
        config.set("item", testItem.clone());

        assertEquals("world", config.get("hello", String.class));
        assertEquals(Integer.valueOf(12), config.get("number", Integer.class));
        assertEquals(GameMode.CREATIVE, config.get("anEnum", GameMode.class));
        assertEquals(testItem, config.get("item", ItemStack.class));

        // Add node by path directly
        ConfigurationNode node = config.getNode("node");
        node.set("key", "value");
        assertEquals("value", node.get("key", String.class));
        assertEquals("value", config.get("node.key", String.class));

        // Create a new node and then set it
        ConfigurationNode lazyNode = new ConfigurationNode();
        lazyNode.set("key", "value");
        assertEquals("value", lazyNode.get("key", String.class));
        config.set("lazyNode", lazyNode);
        assertEquals("value", config.get("lazyNode.key", String.class));
        assertEquals("value", config.getNode("lazyNode").get("key", String.class));

        // Create a list of nodes
        List<ConfigurationNode> nodeList = new ArrayList<ConfigurationNode>();
        nodeList.add(new ConfigurationNode());
        nodeList.add(new ConfigurationNode());
        nodeList.get(0).set("node1Key", "value1");
        nodeList.get(1).set("node2Key", "value2");
        nodeList.get(1).getNode("subNode").set("subNodeKey", "value3");
        config.setNodeList("nodeList", nodeList);

        // Verify (2 nodes)
        checkNodeListA(config);

        // Getting the list again and altering it by inserting an entry in the middle
        nodeList = config.getNodeList("nodeList");
        nodeList.add(1, new ConfigurationNode());
        nodeList.get(1).set("node3Key", "value4");
        config.setNodeList("nodeList", nodeList);

        // Verify again (3 nodes)
        checkNodeListB(config);

        config.save();

        config = new FileConfiguration("test.yml");
        config.load();
        assertEquals("world", config.get("hello", String.class));
        assertEquals(Integer.valueOf(12), config.get("number", Integer.class));
        assertEquals(GameMode.CREATIVE, config.get("anEnum", GameMode.class));
        assertEquals("value", config.get("node.key", String.class));
        assertEquals("value", config.getNode("node").get("key", String.class));
        assertEquals("value", config.get("lazyNode.key", String.class));
        assertEquals("value", config.getNode("lazyNode").get("key", String.class));
        assertEquals(testItem, config.get("item", ItemStack.class));

        // Verify
        checkNodeListB(config);

        // Cleanup
        new File("test.yml").delete();
    }

    private void checkNodeListA(FileConfiguration config) {
        List<ConfigurationNode> nodeList = config.getNodeList("nodeList");
        assertEquals(2, nodeList.size());
        assertEquals("0", nodeList.get(0).getName());
        assertEquals("1", nodeList.get(1).getName());
        assertEquals("value1", nodeList.get(0).get("node1Key", String.class));
        assertEquals("value2", nodeList.get(1).get("node2Key", String.class));
        assertEquals("value3", nodeList.get(1).getNode("subNode").get("subNodeKey", String.class));
    }

    private void checkNodeListB(FileConfiguration config) {
        List<ConfigurationNode> nodeList = config.getNodeList("nodeList");
        assertEquals(3, nodeList.size());
        assertEquals("0", nodeList.get(0).getName());
        assertEquals("1", nodeList.get(1).getName());
        assertEquals("2", nodeList.get(2).getName());
        assertEquals("value1", nodeList.get(0).get("node1Key", String.class));
        assertEquals("value4", nodeList.get(1).get("node3Key", String.class));
        assertEquals("value2", nodeList.get(2).get("node2Key", String.class));
        assertEquals("value3", nodeList.get(2).getNode("subNode").get("subNodeKey", String.class));
    }
}
