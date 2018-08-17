package com.bergerkiller.bukkit.common.internal.blocks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.blocks.type.FluidRenderingProvider;
import com.bergerkiller.bukkit.common.internal.blocks.type.GrassRenderingProvider;
import com.bergerkiller.bukkit.common.internal.blocks.type.RedstoneWireRenderingProvider;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapResourcePack.ResourceType;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
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
        String tex_root = Common.evaluateMCVersion(">=",  "1.13") ? "block/" : "blocks/";
        register(new FluidRenderingProvider(tex_root + "water_overlay", tex_root + "water_still", MaterialUtil.ISWATER.getMaterials()));
        register(new FluidRenderingProvider(tex_root + "lava_still", tex_root + "lava_still", MaterialUtil.ISLAVA.getMaterials()));
        register(new GrassRenderingProvider());
        register(new RedstoneWireRenderingProvider());
    }

    /**
     * Registers a new render provider
     * 
     * @param provider to register
     */
    public static void register(BlockRenderProvider provider) {
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

    private final HashMap<String, String> resources = new HashMap<String, String>();

    /**
     * Adds a link to a resource stored in BKCommonLib itself
     * 
     * @param type of resource
     * @param path to the resource
     * @param resource path within BKCommonLib
     */
    protected void linkResource(ResourceType type, String path, String resource) {
        this.resources.put(type.makePath(path), resource);
    }

    /**
     * Retrieves the resource (model json, png texture, etc.) at a particular path.
     * This is called as a fallback when the resource is not found in the resource pack,
     * while loading a model provided by this render provider.
     * 
     * @param type of resource
     * @param path to get the resource at
     * @return resource, null or exception if not found
     */
    public InputStream openResource(ResourceType type, String path) throws IOException {
        if (!this.resources.isEmpty()) {
            String resourcePath = this.resources.get(type.makePath(path));
            if (resourcePath != null) {
                return this.getClass().getResourceAsStream(resourcePath);
            }
        }
        return null;
    }

    /**
     * Creates the displayed Block Model for certain block render options
     * 
     * @param resources from which model dependencies (textures, other models) can be loaded
     * @param options for rendering the block model
     * @return block model, <i>null</i> if not available
     */
    public Model createModel(MapResourcePack resources, BlockRenderOptions options) {
        return null;
    }

    /**
     * Adds options specific to the provider.
     * <b>world can be null when default render options are requested</b>
     * 
     * @param options to add to
     * @param world the block is at
     * @param x - coordinate of the Block
     * @param y - coordinate of the Block
     * @param z - coordinate of the Block
     */
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
    }

    /**
     * Gets all Material types handled by this provider
     * 
     * @return material types
     */
    public abstract Collection<Material> getTypes();
}
