package com.bergerkiller.bukkit.common.internal.blocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.blocks.type.FluidRenderingProvider;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

/**
 * Lookup table for systems that handle additional block rendering options.
 * When rendering actual real-world blocks, this method should be used to get that information.
 * Some blocks are handled on the server-side, but blocks like Water lack this required information.
 */
public abstract class BlockRenderProvider {
    private static final Map<Material, BlockRenderProvider> providers = new HashMap<Material, BlockRenderProvider>();

    static {
        register(new FluidRenderingProvider("blocks/water_overlay", "blocks/water_still", Material.WATER, Material.STATIONARY_WATER));
        register(new FluidRenderingProvider("blocks/lava_still", "blocks/lava_still", Material.LAVA, Material.STATIONARY_LAVA));
    }

    private static void register(BlockRenderProvider provider) {
        for (Material type : provider.getTypes()) {
            providers.put(type, provider);
        }
    }

    /**
     * Gets the render provider for a certain Block Material Type Data
     * 
     * @param blockData to get the provider for
     * @return render provider
     */
    public static BlockRenderProvider get(BlockData blockData) {
        return providers.get(blockData.getType());
    }

    /**
     * Creates the displayed Block Model for certain block render options
     * 
     * @param resources from which model dependencies (textures, other models) can be loaded
     * @param options for rendering the block model
     * @return block model
     */
    public abstract Model createModel(MapResourcePack resources, BlockRenderOptions options);

    /**
     * Adds options specific to the provider
     * 
     * @param options to add to
     * @param world the block is at
     * @param x - coordinate of the Block
     * @param y - coordinate of the Block
     * @param z - coordinate of the Block
     */
    public abstract void addOptions(BlockRenderOptions options, World world, int x, int y, int z);
    
    /**
     * Gets all Material types handled by this provider
     * 
     * @return material types
     */
    public abstract Collection<Material> getTypes();
}
