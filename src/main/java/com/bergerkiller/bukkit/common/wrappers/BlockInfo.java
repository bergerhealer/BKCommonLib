package com.bergerkiller.bukkit.common.wrappers;

import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.IBlockData;
import org.bukkit.Location;

/**
 * Stores Block material information and method calls
 */
public class BlockInfo extends BasicWrapper {

    private static final BlockInfo NONE = new BlockInfo();
    private static final BlockInfo[] blocks = new BlockInfo[4096];

    static {
        Arrays.fill(blocks, NONE);
        try {
            //TODO: Better way of finding all Block constants in the registry
            for (int i = 0; i < blocks.length; i++) {
                Object handle = CommonNMS.getBlock(i);

                if (handle != null) {
                    blocks[i] = new BlockInfoImpl(handle);
                }
            }
        } catch (Throwable t) {
            CommonPlugin.LOGGER.log(Level.SEVERE, "Unable to initialize BlockInfo API:", t);
        }
    }

    protected BlockInfo() {
    }

    /**
     * Gets the Block information of the block-stored type specified
     *
     * @param block to get the Block information for
     * @return Block information (never null)
     */
    public static BlockInfo get(Block block) {
        return get(MaterialUtil.getTypeId(block));
    }

    /**
     * Gets the Block information of the material type specified
     *
     * @param material to get the Block information for
     * @return Block information (never null)
     */
    public static BlockInfo get(Material material) {
        return get(MaterialUtil.getTypeId(material));
    }

    /**
     * Gets the Block information of the block type Id specified
     *
     * @param typeId to get the Block information for
     * @return Block information (never null)
     */
    public static BlockInfo get(int typeId) {
        return LogicUtil.getArray(blocks, typeId, NONE);
    }

    /**
     * Gets whether this Block material causes suffocation to entities entering
     * it
     *
     * @return True if suffocating, False if not
     */
    public boolean isSuffocating() {
        return false;
    }

    /**
     * Gets whether the block is a Redstone power source
     *
     * @return True if it is a power source, False if not
     */
    public boolean isPowerSource() {
        return false;
    }

    /**
     * Gets whether the block is solid and blocks all light
     *
     * @return True if it is solid, False if not
     */
    public boolean isSolid() {
        return false;
    }

    /**
     * Gets the opacity of the Block
     *
     * @return the opacity
     */
    public int getOpacity() {
        return 0;
    }

    /**
     * Gets the amount of light the Block radiates
     *
     * @return light emission
     */
    public int getLightEmission() {
        return 0;
    }

    /**
     * Gets the damage resilience of a block to damage dealt by a certain entity
     *
     * @param source of the damage
     * @return resilience
     */
    public float getDamageResilience(Entity source) {
        return 0.0f;
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param block to drop at
     * @param yield
     */
    public final void dropNaturally(Block block, float yield) {
        dropNaturally(block, yield, 0);
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param block to drop at
     * @param yield
     * @param chance
     */
    public final void dropNaturally(Block block, float yield, int chance) {
        dropNaturally(block.getWorld(), block.getX(), block.getY(), block.getZ(), MaterialUtil.getRawData(block), yield, 0);
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param yield
     */
    public final void dropNaturally(World world, int x, int y, int z, int data, float yield) {
        dropNaturally(world, x, y, z, data, yield, 0);
    }

    /**
     * Causes the block to drop items as if it was broken naturally
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param yield
     * @param chance
     */
    public void dropNaturally(World world, int x, int y, int z, int data, float yield, int chance) {
    }

    /**
     * Ignites the block (for example, ignites TNT)
     *
     * @param block to ignite at
     */
    public final void ignite(Block block) {
        ignite(block.getWorld(), block.getLocation());
    }

    /**
     * Ignites the block (for example, ignites TNT)
     *
     * @param world the block is in
     * @param loc - location of the block
     */
    public void ignite(World world, Location loc) {
    }

    /**
     * Destroys the block, spawning item drops naturally in the process
     *
     * @param block to destroy
     * @param yield (e.g. 20.0f)
     */
    public final void destroy(Block block, float yield) {
        destroy(block.getWorld(), block.getX(), block.getY(), block.getZ(), MaterialUtil.getRawData(block), yield);
    }

    /**
     * Destroys the block, spawning item drops naturally in the process
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param yield (e.g. 20.0f)
     */
    public final void destroy(World world, int x, int y, int z, float yield) {
        destroy(world, x, y, z, CommonNMS.getNative(world).getType(new BlockPosition(x, y, z)), yield);
    }

    /**
     * Destroys the block, spawning item drops naturally in the process
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param data of the block
     * @param yield (e.g. 20.0f)
     */
    @SuppressWarnings("deprecation")
    public final void destroy(World world, int x, int y, int z, int data, float yield) {
        dropNaturally(world, x, y, z, data, yield);
//		net.minecraft.server.World nativeWorld = CommonNMS.getNative(world);
//		net.minecraft.server.Block nativeBlock = nativeWorld.getType(x, y, z);
//		nativeWorld.setTypeId(x, y, z, nativeBlock, 0);
        //DAT NMS IS WUT SUX

        world.getBlockAt(x, y, z).setTypeId(0);
    }
    
    /**
     * Destroys the block, spawning item drops naturally in the process
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param data of the block
     * @param yield (e.g. 20.0f)
     */
    @SuppressWarnings("deprecation")
    public final void destroy(World world, int x, int y, int z, IBlockData data, float yield) {
        dropNaturally(world, x, y, z, data.getBlock().toLegacyData(data), yield);

        world.getBlockAt(x, y, z).setTypeId(0);
    }
}
