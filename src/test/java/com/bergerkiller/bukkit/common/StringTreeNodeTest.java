package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.StringTreeNode;

public class StringTreeNodeTest {

    @Test
    public void testValue() {
        StringTreeNode node = new StringTreeNode();
        assertEquals("", node.getValue());

        node.setValue("hello");
        assertEquals("hello", node.getValue());
        assertTrue("hello".contentEquals(node.getValueSequence()));

        node.setValue("hey");
        assertEquals("hey", node.getValue());
        assertTrue("hey".contentEquals(node.getValueSequence()));

        node.setValueSequence("helloworld");
        assertEquals("helloworld", node.getValue());
        assertTrue("helloworld".contentEquals(node.getValueSequence()));
    }

    @Test
    public void testToStringRootOnly() {
        StringTreeNode node = new StringTreeNode();
        assertEquals("", node.toString());

        node.setValue("hello");
        assertEquals("hello", node.toString());

        node.setValue("hey");
        assertEquals("hey", node.toString());
    }

    @Test
    public void testToString() {
        // Create a tree:
        // root
        //   aaa
        //   bbb
        //     ddd
        //   ccc
        StringTreeNode root = new StringTreeNode();
        root.setValue("root");
        StringTreeNode child_aaa = root.add();
        child_aaa.setValue("aaa");
        StringTreeNode child_bbb = root.add();
        child_bbb.setValue("bbb");
        StringTreeNode child_ccc = root.add();
        child_ccc.setValue("ccc");
        StringTreeNode child_ddd = child_bbb.add();
        child_ddd.setValue("ddd");

        // Verify the entire tree and the nodes stringify correctly
        assertEquals("rootaaabbbdddccc", root.toString());
        assertEquals("aaa", child_aaa.toString());
        assertEquals("bbbddd", child_bbb.toString());
        assertEquals("ccc", child_ccc.toString());
        assertEquals("ddd", child_ddd.toString());

        // Make changes to root without changing length, toString() should update
        root.setValue("wood");
        assertEquals("woodaaabbbdddccc", root.toString());

        // Make changes to one node without changing length, toString() should update
        child_aaa.setValue("AAA");
        assertEquals("woodAAAbbbdddccc", root.toString());
        assertEquals("AAA", child_aaa.toString());

        // Make changes to one node and change the length, toString() should update
        child_ddd.setValue("DDddDD");
        assertEquals("woodAAAbbbDDddDDccc", root.toString());
        assertEquals("bbbDDddDD", child_bbb.toString());
        assertEquals("DDddDD", child_ddd.toString());

        // Make changes to one node but call toString() in reverse order
        // This will cause a re-initialization of the buffer from the child called
        child_aaa.setValue("aaa");
        assertEquals("aaa", child_aaa.toString());
        assertEquals("woodaaabbbDDddDDccc", root.toString());

        // Perform toString() again without making any changes
        assertEquals("woodaaabbbDDddDDccc", root.toString());
    }

    @Test
    public void testToStringModifyTree() {
        // Create a tree:
        // root
        //   aaa
        //   bbb
        //     ddd
        //   ccc
        StringTreeNode root = new StringTreeNode();
        root.setValue("root");
        StringTreeNode child_aaa = root.add();
        child_aaa.setValue("aaa");
        StringTreeNode child_bbb = root.add();
        child_bbb.setValue("bbb");
        StringTreeNode child_ccc = root.add();
        child_ccc.setValue("ccc");
        StringTreeNode child_ddd = child_bbb.add();
        child_ddd.setValue("ddd");
        assertEquals("rootaaabbbdddccc", root.toString());

        // Remove aaa node in the middle of the tree
        child_aaa.remove();
        assertEquals("rootbbbdddccc", root.toString());
        assertEquals("aaa", child_aaa.toString());

        // Modifying the aaa node should no longer cause changes in root
        child_aaa.setValue("AAAAAA");
        assertEquals("rootbbbdddccc", root.toString());
        assertEquals("AAAAAA", child_aaa.toString());

        // Remove bbb node, which also removes ddd
        child_bbb.remove();
        assertEquals("rootccc", root.toString());
        assertEquals("bbbddd", child_bbb.toString());

        // Insert a new node with a value before ccc
        StringTreeNode child_eee = root.insert(0, new StringTreeNode("eee"));
        assertEquals("rooteeeccc", root.toString());

        // Change the value of the new node
        child_eee.setValue("EEE");
        assertEquals("rootEEEccc", root.toString());

        // Re-add bbb node at a new position
        root.insert(1, child_bbb);
        assertEquals("rootEEEbbbdddccc", root.toString());
    }

    @Test
    public void testClone() {
        // Create a tree:
        // root
        //   aaa
        //   bbb
        StringTreeNode root = new StringTreeNode();
        root.setValue("root");
        StringTreeNode child_aaa = root.add();
        child_aaa.setValue("aaa");
        StringTreeNode child_bbb = root.add();
        child_bbb.setValue("bbb");
        assertEquals("rootaaabbb", root.toString());

        // Clone the entire tree
        StringTreeNode clone_root = root.clone();
        StringTreeNode clone_child_aaa = clone_root.get(0);
        StringTreeNode clone_child_bbb = clone_root.get(1);

        // Verify cloned tree has the same values
        assertEquals("root", clone_root.getValue());
        assertEquals("aaa", clone_child_aaa.getValue());
        assertEquals("bbb", clone_child_bbb.getValue());
        assertEquals("rootaaabbb", clone_root.toString());

        // Make changes to the clone, the original root should not change
        clone_root.setValue("ROOT");
        clone_child_aaa.setValue("AAA");
        clone_child_bbb.setValue("BBB");
        assertEquals("ROOTAAABBB", clone_root.toString());
        assertEquals("rootaaabbb", root.toString());
    }
}
