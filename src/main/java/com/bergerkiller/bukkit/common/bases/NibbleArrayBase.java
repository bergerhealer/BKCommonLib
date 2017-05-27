package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.generated.net.minecraft.server.NibbleArrayHandle;
import com.bergerkiller.reflection.net.minecraft.server.NMSNibbleArray;

/**
 * Base class to work with Nibble Array implementations
 */
public class NibbleArrayBase {
    private final NibbleArrayHandle arr;

    public NibbleArrayBase(byte[] data) {
        this.arr = NibbleArrayHandle.createNew(data);
    }

    public NibbleArrayBase() {
        this.arr = NibbleArrayHandle.createNew();
    }

    /**
     * Gets the backing array, which is still referenced. Do not modify the
     * returned array! Use toArray() if data not being referenced is important.
     *
     * @return data
     */
    public byte[] getData() {
        return NMSNibbleArray.getValueArray(arr.getRaw());
    }

    /**
     * Allocates a new array and fills it with the contents of this NibbleArray
     *
     * @return nibble array data copy
     */
    public byte[] toArray() {
        return NMSNibbleArray.getArrayCopy(arr.getRaw());
    }

    /**
     * Sets a value in this nibble array
     *
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @param value to set to
     */
    public void set(int x, int y, int z, int value) {
        this.arr.set(x, y, z, value);
    }

    /**
     * Gets a value from this nibble array
     *
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @return value
     */
    public int get(int x, int y, int z) {
        return this.arr.get(x, y, z);
    }

    /**
     * Creates a new NibbleArray base type with the data contained in this
     * base<br>
     * Data is still referenced through
     *
     * @return handle
     */
    public Object toHandle() {
        return NibbleArrayHandle.T.constr_data.raw.newInstance(this.getData());
    }
}
