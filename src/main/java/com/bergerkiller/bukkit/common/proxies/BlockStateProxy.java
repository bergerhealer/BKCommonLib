package com.bergerkiller.bukkit.common.proxies;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockStateProxy extends ProxyBase<BlockState> implements BlockState {

    static {
        validate(BlockStateProxy.class);
    }

    public BlockStateProxy(BlockState base) {
        super(base);
    }

    @Override
    public World getWorld() {
        return base.getWorld();
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
    public void setData(final MaterialData data) {
        base.setData(data);
    }

    @Override
    public MaterialData getData() {
        return base.getData();
    }

    @Override
    public void setType(final Material type) {
        base.setType(type);
    }

    @Deprecated
    public boolean setTypeId(int type) {
        base.setType(CommonLegacyMaterials.getMaterialFromId(type));
        return true;
    }

    @Override
    public Material getType() {
        return base.getType();
    }

    @Deprecated
    public int getTypeId() {
        return CommonLegacyMaterials.getIdFromMaterial(getType());
    }

    @Override
    public byte getLightLevel() {
        return base.getLightLevel();
    }

    @Override
    public Block getBlock() {
        return base.getBlock();
    }

    @Override
    public boolean update() {
        return base.update();
    }

    @Override
    public boolean update(boolean force) {
        return base.update(force);
    }

    @Override
    public byte getRawData() {
        return base.getRawData();
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
    public void setRawData(byte data) {
        base.setRawData(data);
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

    @Override
    public boolean update(boolean arg0, boolean arg1) {
        return base.update(arg0, arg1);
    }

    //@Override
    public boolean isPlaced() {
        if (BlockStateHandle.T.isPlaced.isAvailable()) {
            return BlockStateHandle.T.isPlaced.invoke(base);
        } else {
            return true;
        }
    }

    //@Override
    public BlockState copy() {
        return this;
    }

    //@Override
    public BlockState copy(Location location) {
        return null;
    }

    @Override
    public org.bukkit.block.data.BlockData getBlockData() {
        return base.getBlockData();
    }

    @Override
    public void setBlockData(org.bukkit.block.data.BlockData arg0) {
        base.setBlockData(arg0);
    }
}
