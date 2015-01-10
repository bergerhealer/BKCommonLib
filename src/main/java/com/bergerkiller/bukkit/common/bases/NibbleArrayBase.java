package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.reflection.classes.NibbleArrayRef;

import net.minecraft.server.v1_8_R1.NibbleArray;

/**
 * Base class to work with Nibble Array implementations
 */
public class NibbleArrayBase extends NibbleArray {

    @Deprecated //TODO: Needs somework
    public NibbleArrayBase(byte[] data, int dataBits) {
        //super(data, dataBits);
    }
    
    public NibbleArrayBase(byte[] data) {
        super(data);
    }
    
    @Deprecated //TODO: Needs somework
    public NibbleArrayBase (int size, int dataBits) {
        //super(size, dataBits);
    }

    /**
     * Gets the amount of bits a single value is stored as
     *
     * @return data element bit count
     */
    public int getBitCount() {
        return NibbleArrayRef.bitCount.get(this);
    }

    /**
     * Gets the backing array, which is still referenced. Do not modify the
     * returned array! Use toArray() if data not being referenced is important.
     *
     * @return data
     */
    public byte[] getData() {
        return NibbleArrayRef.getValueArray(this);
    }

    /**
     * Allocates a new array and fills it with the contents of this NibbleArray
     *
     * @return nibble array data copy
     */
    public byte[] toArray() {
        return NibbleArrayRef.getArrayCopy(this);
    }

    /**
     * @deprecated use {@link #set(int, int, int, int) set(x, y, z, value)}
     * instead
     */
    @Override
    @Deprecated
    public void a(int i, int j, int k, int l) {
        this.set(i, j, k, l);
    }

    /**
     * @deprecated use {@link #get(int, int, int) get(x, y, z)} instead
     */
    @Override
    @Deprecated
    public int a(int i, int j, int k) {
        return this.get(i, j, k);
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
        super.a(x, y, z, value);
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
        return super.a(x, y, z);
    }

    /**
     * Creates a new NibbleArray base type with the data contained in this
     * base<br>
     * Data is still referenced through
     *
     * @return handle
     */
    public Object toHandle() {
        return new NibbleArray(this.getData());
    }
}
