package com.bergerkiller.bukkit.common.proxies;

import java.util.Collection;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class BlockProxy extends ProxyBase<Block> implements Block {

    static {
        validate(BlockProxy.class);
    }

    public BlockProxy(Block base) {
        super(base);
    }

    @Override
    public World getWorld() {
        return base.getWorld();
    }

    @Override
    public Location getLocation() {
        return base.getLocation();
    }

    @Override
    public Location getLocation(Location loc) {
        return base.getLocation(loc);
    }

    @Override
    public int getX() {
        return base.getX();
    }

    @Override
    public int getY() {
        return base.getY();
    }

    @Override
    public int getZ() {
        return base.getZ();
    }

    @Override
    public Chunk getChunk() {
        return base.getChunk();
    }

    @Override
    public void setData(final byte data) {
        base.setData(data);
    }

    @Override
    public void setData(final byte data, boolean applyPhysics) {
        base.setData(data, applyPhysics);
    }

    @Override
    public byte getData() {
        return base.getData();
    }

    @Override
    public void setType(final Material type) {
        base.setType(type);
    }

    @Override
    public boolean setTypeId(final int type) {
        return base.setTypeId(type);
    }

    @Override
    public boolean setTypeId(final int type, final boolean applyPhysics) {
        return base.setTypeId(type, applyPhysics);
    }

    @Override
    public boolean setTypeIdAndData(final int type, final byte data, final boolean applyPhysics) {
        return base.setTypeIdAndData(type, data, applyPhysics);
    }

    @Override
    public Material getType() {
        return base.getType();
    }

    @Override
    public int getTypeId() {
        return base.getTypeId();
    }

    @Override
    public byte getLightLevel() {
        return base.getLightLevel();
    }

    @Override
    public byte getLightFromSky() {
        return base.getLightFromSky();
    }

    @Override
    public byte getLightFromBlocks() {
        return base.getLightFromBlocks();
    }

    @Override
    public Block getRelative(final int modX, final int modY, final int modZ) {
        return base.getRelative(modX, modY, modZ);
    }

    @Override
    public Block getRelative(BlockFace face) {
        return base.getRelative(face);
    }

    @Override
    public Block getRelative(BlockFace face, int distance) {
        return base.getRelative(face, distance);
    }

    @Override
    public BlockFace getFace(final Block block) {
        return base.getFace(block);
    }

    @Override
    public BlockState getState() {
        return base.getState();
    }

    @Override
    public Biome getBiome() {
        return base.getBiome();
    }

    @Override
    public void setBiome(Biome bio) {
        base.setBiome(bio);
    }

    @Override
    public double getTemperature() {
        return base.getTemperature();
    }

    @Override
    public double getHumidity() {
        return base.getHumidity();
    }

    @Override
    public boolean isBlockPowered() {
        return base.isBlockPowered();
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return base.isBlockIndirectlyPowered();
    }

    @Override
    public boolean isBlockFacePowered(BlockFace face) {
        return base.isBlockFacePowered(face);
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        return base.isBlockFaceIndirectlyPowered(face);
    }

    @Override
    public int getBlockPower(BlockFace face) {
        return base.getBlockPower(face);
    }

    @Override
    public int getBlockPower() {
        return base.getBlockPower();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean isLiquid() {
        return base.isLiquid();
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return base.getPistonMoveReaction();
    }

    @Override
    public boolean breakNaturally() {
        return base.breakNaturally();
    }

    @Override
    public boolean breakNaturally(ItemStack item) {
        return base.breakNaturally(item);
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return base.getDrops();
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack item) {
        return base.getDrops(item);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        base.setMetadata(metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return base.getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return base.hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        base.removeMetadata(metadataKey, owningPlugin);
    }
}
