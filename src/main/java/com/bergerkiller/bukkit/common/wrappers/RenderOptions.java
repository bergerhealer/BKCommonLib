package com.bergerkiller.bukkit.common.wrappers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.utils.WorldUtil;

/**
 * Stores information used at render time when drawing blocks.
 * Render hints such as shape and orientation are stored using this type.
 * This type is safe to use in HashMaps for efficient look-ups.
 */
public abstract class RenderOptions implements Map<String, String>, Cloneable {
    protected String optionsToken;
    protected Map<String, String> optionsMap;

    public RenderOptions() {
        this.optionsToken = "";
        this.optionsMap = null; // lazy load
    }

    public RenderOptions(String optionsToken) {
        this.optionsToken = optionsToken;
        this.optionsMap = null; // lazy load
    }

    public RenderOptions(Map<String, String> optionsMap) {
        this.optionsMap = optionsMap;
        this.optionsToken = null; // lazy load
    }

    private final Map<String, String> map(boolean write) {
        if (this.optionsMap == null) {
            if (this.optionsToken.isEmpty()) {
                // Shortcut
                if (write) {
                    this.optionsMap = new HashMap<String, String>(1);
                    this.optionsToken = null;
                } else {
                    return Collections.emptyMap();
                }
            } else {
                // If the element is a single String token, parse it as key=value pairs separated by ,
                this.optionsMap = new HashMap<String, String>(2);
                int index = 0;
                do {
                    // Find next pair (key=value)
                    String pair;
                    int endIndex = this.optionsToken.indexOf(',', index);
                    if (endIndex == -1) {
                        pair = this.optionsToken.substring(index);
                        index = -1;
                    } else {
                        pair = this.optionsToken.substring(index, endIndex);
                        index = endIndex + 1;
                    }

                    // Decode pair and store in map
                    int pairSep = pair.indexOf('=');
                    if (pairSep != -1) {
                        this.optionsMap.put(pair.substring(0, pairSep), pair.substring(pairSep + 1));
                    } else {
                        this.optionsMap.put(pair, "");
                    }
                } while (index != -1);

                // When writing reset the token so it is re-generated later
                if (write) {
                    this.optionsToken = null;
                }
            }
        }
        return this.optionsMap;
    }

    /**
     * Gets all options encoded as a comma-separated String of key=value pairs
     * 
     * @return options
     */
    public final String getOptionsString() {
        if (this.optionsToken == null) {
            if (this.optionsMap.isEmpty()) {
                // Shortcut
                this.optionsToken = "";
            } else {
                // Build a String
                boolean first = true;
                StringBuilder result = new StringBuilder(this.optionsMap.size() * 10);
                for (Map.Entry<String, String> option : this.optionsMap.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        result.append(',');
                    }
                    result.append(option.getKey());
                    if (!option.getValue().isEmpty()) {
                        result.append('=').append(option.getValue());
                    }
                }
                this.optionsToken = result.toString();
            }
        }
        return this.optionsToken;
    }

    @Override
    public final int size() {
        return this.map(false).size();
    }

    @Override
    public final boolean isEmpty() {
        return this.map(false).isEmpty();
    }

    @Override
    public final boolean containsKey(Object key) {
        return this.map(false).containsKey(key);
    }

    @Override
    public final boolean containsValue(Object value) {
        return this.map(false).containsValue(value);
    }

    @Override
    public final String get(Object key) {
        return this.map(false).get(key);
    }

    @Override
    public final String put(String key, String value) {
        return this.map(true).put(key, value);
    }

    @Override
    public final String remove(Object key) {
        return this.map(true).remove(key);
    }

    @Override
    public final void putAll(Map<? extends String, ? extends String> m) {
        this.map(true).putAll(m);
    }

    @Override
    public final void clear() {
        this.map(true).clear();
    }

    @Override
    public final Set<String> keySet() {
        return this.map(false).keySet();
    }

    @Override
    public final Collection<String> values() {
        return this.map(false).values();
    }

    @Override
    public final Set<java.util.Map.Entry<String, String>> entrySet() {
        return this.map(false).entrySet();
    }

    @Override
    public int hashCode() {
        return this.map(false).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RenderOptions) {
            return ((RenderOptions) o).map(false).equals(this.map(false));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + this.getOptionsString() + "]";
    }

    @Override
    public abstract RenderOptions clone();

    /**
     * Looks up the appropriate name of the model in the Minecraft resource pack format
     * 
     * @return model name
     */
    public abstract String lookupModelName();

    /**
     * Parses a list of key=value pairs (delimited by ,) into a block render options mapping
     * 
     * @param blockData of the Block
     * @param token to parse
     * @return block render options
     */
    public static BlockRenderOptions fromString(BlockData blockData, String token) {
        return new BlockRenderOptions(blockData, token);
    }

    /**
     * Retrieves all the render options required for rendering a particular Block.
     * Blocks that require advanced rendering logic, such as water and fences, will have
     * that logic performed here.
     * See also: {@link BlockData#getRenderOptions(block)}
     * 
     * @param block to render
     * @return rendering options
     */
    public static BlockRenderOptions fromBlock(Block block) {
        return WorldUtil.getBlockData(block).getRenderOptions(block);
    }

    /**
     * Retrieves all the render options required for rendering a particular Block.
     * Blocks that require advanced rendering logic, such as water and fences, will have
     * that logic performed here.
     * See also: {@link BlockData#getRenderOptions(World, int, int, int)}
     * 
     * @param world the block is at
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return rendering options
     */
    public static BlockRenderOptions fromBlock(World world, int x, int y, int z) {
        return WorldUtil.getBlockData(world, x, y, z).getRenderOptions(world, x, y, z);
    }
}
