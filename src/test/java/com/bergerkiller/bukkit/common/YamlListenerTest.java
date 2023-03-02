package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener;
import com.bergerkiller.bukkit.common.config.yaml.YamlNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;

/**
 * Tests that listeners are called in all possible situations that they should
 */
public class YamlListenerTest {
    private final Listener listener = new Listener();

    @Test
    public void testYamlListenerOnSetToRemoveField() {
        YamlNode root = new YamlNode();
        YamlNode node = root.getNode("node");
        node.set("one", "valueA");
        node.set("two", "valueB");

        root.addChangeListener(listener);
        listener.reset();

        // Set only one, leave out two. The removal of two should be notified
        // as a change to 'node's children
        YamlNode repl = new YamlNode();
        repl.set("one", "valueA");
        node.setTo(repl);

        listener.testOne("node");
    }

    @Test
    public void testYamlListenerOnSetToOverwriteDeep() {
        YamlNode root = new YamlNode();
        YamlNode sub = root.getNode("sub");
        YamlNode node = sub.getNode("node");
        node.set("one", "valueA");
        node.set("two", "valueB");
        node.set("three", "valueC");

        root.addChangeListener(listener);
        listener.reset();

        // Set to will change value of 'one', remove 'three' and create 'four'
        // The changed value and the created value should be notified
        // The removal should not be, as that's part of notifying the parent children changed
        YamlNode subrepl = new YamlNode();
        YamlNode repl = subrepl.getNode("node");
        repl.set("one", "valueD");
        repl.set("two", "valueB");
        repl.set("four", "valueE");
        sub.setTo(subrepl);

        listener.testMany("sub.node.one", "sub.node.four", "sub.node");
    }

    @Test
    public void testYamlListenerOnSetToOverwrite() {
        YamlNode root = new YamlNode();
        YamlNode node = root.getNode("node");
        node.set("one", "valueA");
        node.set("two", "valueB");
        node.set("three", "valueC");

        root.addChangeListener(listener);
        listener.reset();

        // Set to will change value of 'one', remove 'three' and create 'four'
        // The changed value and the created value should be notified
        // The removal should not be, as that's part of notifying the parent children changed
        YamlNode repl = new YamlNode();
        repl.set("one", "valueD");
        repl.set("two", "valueB");
        repl.set("four", "valueE");
        node.setTo(repl);

        listener.testMany("node.one", "node.four", "node");
    }

    @Test
    public void testYamlListenerOnValue() {
        YamlNode root = new YamlNode();
        root.set("key", "value");
        root.addChangeListener("key", listener);
        listener.reset();

        // Changing value should trigger an event
        root.set("key", "new_value");
        listener.testOne("key");

        // Changing to the same value should not trigger an event
        root.set("key", "new_value");
        listener.testNone();

        // Changing at a different key should not trigger an event
        root.set("other", "somevalue");
        listener.testNone();
    }

    @Test
    public void testYamlListenerOnNodeEarly() {
        YamlNode root = new YamlNode();
        root.addChangeListener(listener);
        listener.reset();

        // Creating a new key should fire two events
        // We expect one that changes the parent (new node) and
        // then of the key itself.
        root.set("key", "value");
        listener.testMany("", "key");

        // Not changing the value should not fire an event
        root.set("key", "value");
        listener.testNone();

        // Changing the value should fire the event
        root.set("key", "new_value");
        listener.testOne("key");
    }

    @Test
    public void testYamlListenerOnNodeLate() {
        YamlNode root = new YamlNode();
        root.set("key", "initial");

        root.addChangeListener(listener);
        listener.reset();

        // Key already exists, so we should see only one change event
        root.set("key", "value");
        listener.testOne("key");

        // Not changing the value should not fire an event
        root.set("key", "value");
        listener.testNone();

        // Changing the value should fire the event
        root.set("key", "new_value");
        listener.testOne("key");
    }

    @Test
    public void testYamlListenerRecursiveEarly() {
        YamlNode root = new YamlNode();
        root.addChangeListener(listener);
        listener.reset();

        // Two events, one for the root that gets a new child, and one
        // for the child itself.
        YamlNode child = root.getNode("child");
        listener.testMany("", "child");

        // Fires two events. One because the children of 'child' changed,
        // and one for the new key.
        child.set("key", "value");
        listener.testMany("child", "child.key");

        child.set("key", "new_value");
        listener.testOne("child.key");

        // Fires two events, because the child gets a new child added
        // And an event is created for the newly created node.
        YamlNode childOfChild = child.getNode("subchild");
        listener.testMany("child", "child.subchild");

        childOfChild.set("key2", "value");
        listener.testMany("child.subchild", "child.subchild.key2");
        childOfChild.set("key2", "new_value");
        listener.testOne("child.subchild.key2");
    }

    /*
     * When a key is removed from a node
     */
    @Test
    public void testListenerRemoveKey() {
        YamlNode root = new YamlNode();
        root.set("a", "a");
        root.set("b.c", "bc");
        root.set("b.d", "bd");
        root.addChangeListener(listener);
        listener.reset();

        root.remove("a");
        listener.testOne("");

        root.remove("b.c");
        listener.testOne("b");
        root.remove("b.d");
        listener.testOne("b");
        root.remove("b");
        listener.testOne("");
    }

    @Test
    public void testListenerNodeClear() {
        YamlNode root = new YamlNode();
        root.set("a", "a");
        root.set("b.c", "bc");
        root.set("b.d", "bd");
        root.addChangeListener(listener);
        listener.reset();

        root.clear();
        listener.testOne("");
    }

    /*
     * Track changes of a list node
     */
    @Test
    public void testYamlListenerListChanges() {
        YamlNode root = new YamlNode();
        root.addChangeListener(listener);
        listener.reset();

        root.set("list", new String[0]);
        listener.testMany("", "list");

        List<String> list = root.getList("list", String.class);
        listener.testNone();

        list.add("value1");
        listener.testMany("list", "list[0]");

        list.add("value2");
        listener.testMany("list", "list[1]");

        list.set(0, "value1alter");
        listener.testOne("list[0]");

        list.set(1, "value2alter");
        listener.testOne("list[1]");

        list.remove(0);
        listener.testOne("list");

        list.add(0, "value1new");
        listener.testMany("list", "list[0]");

        list.clear();
        listener.testOne("list");

        list.addAll(Arrays.asList("a", "b", "c"));
        listener.testMany("list", "list[0]", "list", "list[1]", "list", "list[2]");

        list.removeAll(Arrays.asList("a", "b", "c"));
        listener.testOne("list");
    }

    /*
     * Verify changes to an external node fire the listener
     */
    @Test
    public void testAddNodeChanges() {
        YamlNode root = new YamlNode();
        root.addChangeListener(listener);
        listener.reset();

        YamlNode newNode = new YamlNode();
        newNode.set("key", "value");
        YamlNode child = newNode.getNode("childnode");
        child.set("cc", "dd");

        root.set("child", newNode);
        listener.testMany("", "child");

        newNode.set("key", "new_value");
        listener.testOne("child.key");

        child.set("cc", "ee");
        listener.testOne("child.childnode.cc");

        child.set("kk", "ll");
        listener.testMany("child.childnode", "child.childnode.kk");
    }

    /*
     * Tests that a listener added to a node is removed
     * once added to a new (root) node
     */
    @Test
    public void testListenerFromOldNode() {
        YamlNode root = new YamlNode();

        YamlNode newNode = new YamlNode();
        newNode.set("key", "value");
        YamlNode child = newNode.getNode("childnode");
        child.set("cc", "dd");
        newNode.addChangeListener(listener);
        listener.reset();

        root.set("child", newNode);
        listener.testNone();

        newNode.set("key", "new_value");
        listener.testNone();

        child.set("cc", "ee");
        listener.testNone();

        child.set("kk", "ll");
        listener.testNone();
    }

    /*
     * Tests that a cloned node does not fire events for the
     * original node's listener. But changing the original node
     * should still fire events.
     */
    @Test
    public void testListenerWhenCloned() {
        YamlNode root = new YamlNode();
        YamlNode newNode = root.getNode("child");
        newNode.set("key", "value");
        YamlNode child = newNode.getNode("childnode");
        child.set("cc", "dd");
        newNode.addChangeListener(listener);
        listener.reset();

        YamlNode clone = root.clone();
        listener.testNone();

        // No changes when done on clone
        clone.set("child.key", "new_value");
        listener.testNone();
        clone.set("child.childnode.cc", "ee");
        listener.testNone();
        clone.set("child.childnode.kk", "ll");
        listener.testNone();

        // Changes when done on the original node
        newNode.set("key", "new_value");
        listener.testOne("child.key");
        child.set("cc", "ee");
        listener.testOne("child.childnode.cc");
        child.set("kk", "ll");
        listener.testMany("child.childnode", "child.childnode.kk");
    }

    /*
     * Tests change events fire when the header is changed
     */
    @Test
    public void testListenerHeaders() {
        YamlNode root = new YamlNode();
        root.addChangeListener(listener);
        listener.reset();

        root.setHeader("This is the header of the root");
        listener.testOne("");

        root.setHeader("key", "This is header of key");
        listener.testMany("", "key");
        root.addHeader("key", "Extra line on key");
        listener.testOne("key");

        YamlNode child = root.getNode("child");
        child.set("a", "b");
        listener.reset();
        child.setHeader("Header of child");
        listener.testOne("child");
    }

    /*
     * Tests what happens when adding a node to a node list
     */
    @Test
    public void testListenerNodeList() {
        YamlNode root = new YamlNode();
        root.addChangeListener(listener);
        listener.reset();

        List<YamlNode> nodes = root.getNodeList("children");
        listener.testNone();

        // Note: first time it adds the 'children' node/list to the root
        // This is why it fires an event for ""
        // it fires 'children' twice, once for creating an empty list,
        // another for changing the list to add one node to it.
        YamlNode newChildA = new YamlNode();
        newChildA.set("a", "A");
        newChildA.set("b", "B");
        nodes.add(newChildA);
        listener.testMany("", "children", "children", "children.0");

        // No event for "" this time, children is already created
        YamlNode newChildB = new YamlNode();
        newChildB.set("c", "C");
        newChildB.set("d", "D");
        nodes.add(newChildB);
        listener.testMany("children", "children.1");

        // Updating a child
        newChildA.set("a", "AA");
        listener.testOne("children.0.a");
        newChildB.set("e", "E");
        listener.testMany("children.1", "children.1.e");

        // Removing a value
        nodes.remove(1);
        listener.testOne("children");

        // Clearing
        nodes.clear();
        listener.testOne("children");
    }

    // Helper listener used for the tests
    public static class Listener implements YamlChangeListener {
        public final List<YamlPath> received = new ArrayList<YamlPath>();
        private boolean logStack = false;

        public void reset() {
            received.clear();
        }

        public void setLogStack(boolean log) {
            logStack = log;
        }

        @Override
        public void onNodeChanged(YamlPath path) {
            received.add(path);
            if (logStack) {
                System.err.println("Got onNodeChanged for " + path);
                Thread.dumpStack();
            }
        }

        public void testNone() {
            if (!received.isEmpty()) {
                fail("Expected no events, but got " + received.size() + " events ( [0]=" + received.get(0) + " )");
            }
        }

        public void testOne(String expectedPath) {
            if (received.isEmpty()) {
                fail("Expected an event for '" + expectedPath + "', but got none");
            } else if (received.size() > 1) {
                for (YamlPath recv : received) {
                    System.err.println("- event: " + recv);
                }
                fail("Expected one event for '" + expectedPath + "', but got " + received.size());
            } else if (!received.get(0).toString().equals(expectedPath)) {
                fail("Expected event for '" + expectedPath + "', but got one for '" +
                        received.get(0).toString() + "' instead");
            } else {
                received.clear();
            }
        }

        public void testMany(String... paths) {
            if (paths.length == 0) {
                testNone();
            } else if (paths.length == 1) {
                testOne(paths[0]);
            } else {
                // Check
                if (received.isEmpty()) {
                    fail("Expected " + paths.length + " events, but got none");
                } else if (received.size() != paths.length) {
                    for (YamlPath recv : received) {
                        System.err.println("- event: " + recv);
                    }
                    fail("Expected " + paths.length + " events, but got " + received.size());
                } else {
                    for (int i = 0; i < paths.length; i++) {
                        if (!received.get(i).toString().equals(paths[i])) {
                            fail("Expected [" + i + "] event for '" + paths[i] + "', but got one for '" +
                                    received.get(i).toString() + "' instead");
                        }
                    }
                    received.clear();
                }
            }
        }
    }
}
