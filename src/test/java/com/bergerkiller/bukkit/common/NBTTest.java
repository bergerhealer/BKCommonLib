package com.bergerkiller.bukkit.common;

import org.bukkit.block.BlockFace;
import org.bukkit.permissions.PermissionDefault;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.UUID;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.server.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.server.NBTTagListHandle;

public class NBTTest {

    @Test
    public void testNBTCreateTagForData() {
        CommonTag tag;

        tag = CommonTag.createForData("String");
        assertTrue(NBTBaseHandle.NBTTagStringHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals("String", tag.getData());

        tag = CommonTag.createForData((byte) 12);
        assertTrue(NBTBaseHandle.NBTTagByteHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals((byte) 12, tag.getData());

        tag = CommonTag.createForData((short) 12);
        assertTrue(NBTBaseHandle.NBTTagShortHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals((short) 12, tag.getData());

        tag = CommonTag.createForData(12);
        assertTrue(NBTBaseHandle.NBTTagIntHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals(12, tag.getData());

        tag = CommonTag.createForData(12L);
        assertTrue(NBTBaseHandle.NBTTagLongHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals(12L, tag.getData());

        tag = CommonTag.createForData(12F);
        assertTrue(NBTBaseHandle.NBTTagFloatHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals(12F, tag.getData());

        tag = CommonTag.createForData(12D);
        assertTrue(NBTBaseHandle.NBTTagDoubleHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals(12D, tag.getData());

        tag = CommonTag.createForData(new byte[] { 12, 13, 14 });
        assertTrue(NBTBaseHandle.NBTTagByteArrayHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertTrue(Arrays.equals(new byte[] { 12, 13, 14 }, (byte[]) tag.getData()));

        tag = CommonTag.createForData(new int[] { 12, 13, 14 });
        assertTrue(NBTBaseHandle.NBTTagIntArrayHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertTrue(Arrays.equals(new int[] { 12, 13, 14 }, (int[]) tag.getData()));

        if (NBTBaseHandle.NBTTagLongArrayHandle.T.isAvailable()) {
            tag = CommonTag.createForData(new long[] { 12, 13, 14 });
            assertTrue(NBTBaseHandle.NBTTagLongArrayHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
            assertTrue(Arrays.equals(new long[] { 12, 13, 14 }, (long[]) tag.getData()));
        }

        // List with 3 values. Verify the second value is NBTTagInt with value 13
        tag = CommonTag.createForData(Arrays.asList(12, 13, 14));
        assertTrue(tag instanceof CommonTagList);
        assertTrue(NBTTagListHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        CommonTagList tag_list = (CommonTagList) tag;
        assertEquals(3, tag_list.size());
        tag = tag_list.get(1);
        assertTrue(NBTBaseHandle.NBTTagIntHandle.class.isAssignableFrom(tag.getBackingHandle().getClass()));
        assertEquals(13, tag.getData());
    }

    @Test
    public void testNBTCompoundWithList() {
        CommonTagCompound compound = new CommonTagCompound();
        CommonTagList list = compound.createList("key");
        list.addValue("Value1");
        list.addValue("Value2");
        assertEquals(2, list.size());
        assertEquals("Value1", list.getValue(0));
        assertEquals("Value2", list.getValue(1));

        list = compound.get("key", CommonTagList.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("Value1", list.getValue(0));
        assertEquals("Value2", list.getValue(1));
    }

    @Test
    public void testNBTCompoundGetPut() {
        CommonTagCompound compound = new CommonTagCompound();
        testGetPutRemove(compound, "integerType", 12);
        testGetPutRemove(compound, "longType", 15L);
        testGetPutRemove(compound, "doubleType", 12D);
        testGetPutRemove(compound, "floatType", 12F);
        testGetPutRemove(compound, "byteType", (byte) 12);
        testGetPutRemove(compound, "stringType", "hello, world!");
        testGetPutRemove(compound, "blockPos", new BlockLocation("World2", 4, 6, 8));
        testGetPutRemove(compound, "intVector3Pos", new IntVector3(20, 44, 66));
        testGetPutRemove(compound, "random", UUID.randomUUID());
        testGetPutRemove(compound, "face", BlockFace.EAST);
        testGetPutRemove(compound, "perm", PermissionDefault.OP);
        testGetPutRemove(compound, "boolT", Boolean.TRUE);
        testGetPutRemove(compound, "boolF", Boolean.FALSE);
    }

    private void testGetPutRemove(CommonTagCompound compound, String key, Object value) {
        compound.putValue(key, value);
        Object getValue = compound.getValue(key, value.getClass());
        assertNotNull(getValue);
        assertEquals(getValue.getClass(), value.getClass());
        assertEquals(value, getValue);
        Object removedValue = compound.removeValue(key, value.getClass());
        assertNotNull(removedValue);
        assertEquals(removedValue.getClass(), value.getClass());
        assertEquals(value, removedValue);
        assertNull(compound.getValue(key, value.getClass()));
    }
}
