package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Locale;

import org.bukkit.block.BlockFace;

/**
 * An immutable set of at max 6 faces of a Block.
 * There are 64 possible sets as a result of this.
 */
public final class BlockFaceSet {
    private final int _mask;
    private final BlockFace[] _faces;

    private static final BlockFaceSet[] cache;
    static {
        cache = new BlockFaceSet[64];
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new BlockFaceSet(i);
        }
    }

    // Masks used. Note: matches order in BlockFace so ordinal() can be used
    public static final int MASK_NORTH = (1 << 0);
    public static final int MASK_EAST  = (1 << 1);
    public static final int MASK_SOUTH = (1 << 2);
    public static final int MASK_WEST  = (1 << 3);
    public static final int MASK_UP    = (1 << 4);
    public static final int MASK_DOWN  = (1 << 5);

    // Some easy to use constants
    public static final BlockFaceSet NONE = byMask(0);
    public static final BlockFaceSet ALL = byMask(MASK_NORTH | MASK_EAST | MASK_SOUTH | MASK_WEST | MASK_UP | MASK_DOWN);

    /**
     * Gets the BlockFaceSet matching the mask specified
     * 
     * @param mask
     * @return BlockFaceSet
     * @see {@link #mask()}
     */
    public static BlockFaceSet byMask(int mask) {
        return cache[mask];
    }

    /**
     * Gets the BlockFaceSet matching all faces set
     * 
     * @param faces
     * @return BlockFaceSet
     */
    public static BlockFaceSet of(BlockFace... faces) {
        int mask = 0;
        for (BlockFace face : faces) {
            mask |= (1 << face.ordinal());
        }
        return byMask(mask);
    }

    /**
     * Gets the mask for a given BlockFace
     * 
     * @param face
     * @return mask
     */
    public static int getMask(BlockFace face) {
        return 1 << face.ordinal();
    }

    private BlockFaceSet(int mask) {
        this._mask = mask;

        BlockFace[] all_faces = BlockFace.values();
        ArrayList<BlockFace> tmp = new ArrayList<BlockFace>();
        for (int i = 0; i < 6; i++) {
            if ((mask & (1 << i)) != 0) {
                tmp.add(all_faces[i]);
            }
        }
        this._faces = tmp.toArray(new BlockFace[tmp.size()]);
    }

    /**
     * Gets a 6-bit mask value representing this BlockFaceSet.
     * 
     * @return mask
     */
    public int mask() {
        return this._mask;
    }

    /**
     * Gets an array of all set BlockFace values
     * 
     * @return faces
     */
    public BlockFace[] getFaces() {
        return this._faces;
    }

    /**
     * Checks whether the given face is set in this BlockFaceSet
     * 
     * @param face
     * @return True if set
     */
    public boolean get(BlockFace face) {
        return (this._mask & getMask(face)) != 0;
    }

    /**
     * Returns a new BlockFaceSet with the specified face set
     * 
     * @param face The face to set
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet set(BlockFace face) {
        int new_mask = this._mask | getMask(face);
        return (this._mask == new_mask) ? this : byMask(new_mask);
    }

    /**
     * Returns a new BlockFaceSet with the specified face cleared
     * 
     * @param face The face to clear
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet clear(BlockFace face) {
        int new_mask = this._mask & ~getMask(face);
        return (this._mask == new_mask) ? this : byMask(new_mask);
    }

    /**
     * Returns a new BlockFaceSet with the specified face set, or cleared,
     * depending on whether set is true.
     * 
     * @param face The face to set or clear
     * @param set True to set, False to clear
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet set(BlockFace face, boolean set) {
        return setMask(getMask(face), set);
    }

    /**
     * Returns a new BlockFaceSet with the specified face mask bits set, or cleared,
     * depending on whether set is true.
     * 
     * @param bits The face mask bits to set or clear
     * @param set True to set, False to clear
     * @return BlockFaceSet with the face(s) changed
     */
    public BlockFaceSet setMask(int bits, boolean set) {
        if (((this._mask & bits) != 0) != set) {
            return byMask(this._mask ^ bits);
        } else {
            return this;
        }
    }

    /**
     * Returns a new BlockFaceSet with the NORTH face set, or cleared, depending
     * on the input parameter.
     * 
     * @param north Whether to set or clear the face
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet setNorth(boolean north) {
        return setMask(MASK_NORTH, north);
    }

    /**
     * Returns a new BlockFaceSet with the EAST face set, or cleared, depending
     * on the input parameter.
     * 
     * @param east Whether to set or clear the face
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet setEast(boolean east) {
        return setMask(MASK_EAST, east);
    }

    /**
     * Returns a new BlockFaceSet with the SOUTH face set, or cleared, depending
     * on the input parameter.
     * 
     * @param south Whether to set or clear the face
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet setSouth(boolean south) {
        return setMask(MASK_SOUTH, south);
    }

    /**
     * Returns a new BlockFaceSet with the WEST face set, or cleared, depending
     * on the input parameter.
     * 
     * @param west Whether to set or clear the face
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet setWest(boolean west) {
        return setMask(MASK_WEST, west);
    }

    /**
     * Returns a new BlockFaceSet with the UP face set, or cleared, depending
     * on the input parameter.
     * 
     * @param up Whether to set or clear the face
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet setUp(boolean up) {
        return setMask(MASK_UP, up);
    }

    /**
     * Returns a new BlockFaceSet with the DOWN face set, or cleared, depending
     * on the input parameter.
     * 
     * @param down Whether to set or clear the face
     * @return BlockFaceSet with the face changed
     */
    public BlockFaceSet setDown(boolean down) {
        return setMask(MASK_DOWN, down);
    }

    /**
     * Gets whether the NORTH BlockFace is set
     * 
     * @return True if set
     */
    public boolean north() {
        return (this._mask & MASK_NORTH) != 0;
    }

    /**
     * Gets whether the EAST BlockFace is set
     * 
     * @return True if set
     */
    public boolean east() {
        return (this._mask & MASK_EAST) != 0;
    }

    /**
     * Gets whether the SOUTH BlockFace is set
     * 
     * @return True if set
     */
    public boolean south() {
        return (this._mask & MASK_SOUTH) != 0;
    }

    /**
     * Gets whether the WEST BlockFace is set
     * 
     * @return True if set
     */
    public boolean west() {
        return (this._mask & MASK_WEST) != 0;
    }

    /**
     * Gets whether the UP BlockFace is set
     * 
     * @return True if set
     */
    public boolean up() {
        return (this._mask & MASK_UP) != 0;
    }

    /**
     * Gets whether the DOWN BlockFace is set
     * 
     * @return True if set
     */
    public boolean down() {
        return (this._mask & MASK_DOWN) != 0;
    }

    @Override
    public int hashCode() {
        return this._mask; // Might be faster than System.identityHashCode()
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj; // Immutable
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append('[');
        for (int i = 0; i < this._faces.length; i++) {
            if (i > 0) {
                str.append(',');
            }
            str.append(this._faces[i].name().toLowerCase(Locale.ENGLISH));
        }
        str.append(']');
        return str.toString();
    }
}
