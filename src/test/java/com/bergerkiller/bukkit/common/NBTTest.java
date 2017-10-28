package com.bergerkiller.bukkit.common;

import org.bukkit.block.BlockFace;
import org.bukkit.permissions.PermissionDefault;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.UUID;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

public class NBTTest {

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
