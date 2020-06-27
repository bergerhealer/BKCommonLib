package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.FoodMetaDataHandle;
import com.bergerkiller.generated.net.minecraft.server.InventoryEnderChestHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerInventoryHandle;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;

import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;

/**
 * Contains utility functions for dealing with NBT data such as saving and loading
 */
public class NBTUtil {

    /**
     * Reads a mob effect from an NBT Tag Compound
     *
     * @param compound to read a mob effect from
     * @return Loaded MobEffect
     */
    public static MobEffectHandle loadMobEffect(CommonTagCompound compound) {
        return MobEffectHandle.fromNBT(compound);
    }

    /**
     * Saves entity data to the Tag Compound specified
     *
     * @param entity to save
     * @param compound to save to, use null to save to a new compound
     * @return the compound to which was saved
     */
    public static CommonTagCompound saveEntity(org.bukkit.entity.Entity entity, CommonTagCompound compound) {
        if (compound == null) {
            compound = new CommonTagCompound();
        }
        EntityHandle.fromBukkit(entity).saveToNBT(compound);
        return compound;
    }

    /**
     * Loads an entity with data from the Tag Compound specified
     *
     * @param entity to load
     * @param compound to load from
     */
    public static void loadEntity(org.bukkit.entity.Entity entity, CommonTagCompound compound) {
        EntityHandle.fromBukkit(entity).loadFromNBT(compound);
    }

    /**
     * Saves an itemstack to a Tag Compound
     *
     * @param item ItemStack
     * @param compound TagCompound
     */
    public static void saveItemStack(org.bukkit.inventory.ItemStack item, CommonTagCompound compound) {
        ItemStackHandle stack = ItemStackHandle.fromBukkit(item);
        if (stack != null) {
            stack.saveToNBT(compound);
        }
    }

    /**
     * Creates an inventory from a tag list
     *
     * @param tags Tag list
     * @return Inventory
     */
    public static Inventory createInventory(CommonTagList tags) {
    	//TODO: BROKEN!!!
    	return null;
    	/*
        Inventory inv = new CraftInventoryCustom(null, tags.size());

        for (int i = 0; i < tags.size(); i++) {
            CommonTagCompound tag = (CommonTagCompound) tags.get(i);
            if (!tag.isEmpty()) {
                inv.setItem(i, CraftItemStack.asCraftMirror(ItemStack.createStack(
                        (NBTTagCompound) tag.getHandle())));
            }
        }
        return inv;
        */
    }

    /**
     * Saves food meta data to the NBT Tag Compound specified
     *
     * @param foodMetaData to save
     * @param compound to save to, use null to save to a new compound
     * @return the compound to which was saved
     */
    public static CommonTagCompound saveFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
        if (compound == null) {
            compound = new CommonTagCompound();
        }
        FoodMetaDataHandle.createHandle(foodMetaData).saveToNBT(compound);
        return compound;
    }

    /**
     * Loads Food Meta Data with data from the Tag Compound specified
     *
     * @param foodMetaData to load
     * @param compound to load from
     */
    public static void loadFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
        FoodMetaDataHandle.createHandle(foodMetaData).loadFromNBT(compound);
    }

    /**
     * Saves inventory data to the tag list specified
     *
     * @param inventory to save
     * @param list to save to, use null for a new list
     * @return the saved tag list
     */
    public static CommonTagList saveInventory(org.bukkit.inventory.Inventory inventory, CommonTagList list) {
        final Object inventoryHandle = Conversion.toInventoryHandle.convert(inventory);
        if (inventoryHandle == null) {
            throw new IllegalArgumentException("This kind of inventory lacks a handle to load");
        }
        if (PlayerInventoryHandle.T.isAssignableFrom(inventoryHandle)) {
            if (list == null) {
                list = new CommonTagList();
            }
            PlayerInventoryHandle.createHandle(inventoryHandle).saveToNBT(list);
        } else if (InventoryEnderChestHandle.T.isAssignableFrom(inventoryHandle)) {
            CommonTagList listSaved = InventoryEnderChestHandle.createHandle(inventoryHandle).saveToNBT();
            if (listSaved != null) {
                for (CommonTag savedItem : listSaved.getData()) {
                    list.add(savedItem);
                }
            }
            return list;
        } else {
            throw new IllegalArgumentException("This kind of inventory has an unknown type of handle: " + inventoryHandle.getClass().getName());
        }
        return list;
    }

    /**
     * Loads inventory data from the Tag List specified
     *
     * @param inventory to load
     * @param list to load from
     */
    public static void loadInventory(org.bukkit.inventory.Inventory inventory, CommonTagList list) {
        final Object inventoryHandle = Conversion.toInventoryHandle.convert(inventory);
        if (inventoryHandle == null) {
            throw new IllegalArgumentException("This kind of inventory lacks a handle to save");
        } else if (PlayerInventoryHandle.T.isAssignableFrom(inventoryHandle)) {
            PlayerInventoryHandle.createHandle(inventoryHandle).loadFromNBT(list);
        } else if (InventoryEnderChestHandle.T.isAssignableFrom(inventoryHandle)) {
            InventoryEnderChestHandle.createHandle(inventoryHandle).loadFromNBT(list);
        } else {
            throw new IllegalArgumentException("This kind of inventory has an unknown type of handle: " + inventoryHandle.getClass().getName());
        }
    }

    /**
     * Resets all attributes set for an Entity to the defaults. This should be
     * called prior to loading in new attributes using
     * {@link #loadAttributes(LivingEntity, CommonTagList)}
     *
     * @param livingEntity to reset
     */
    public static void resetAttributes(LivingEntity livingEntity) {
        EntityLivingHandle.fromBukkit(livingEntity).resetAttributes();
    }

    /**
     * Loads the attributes for an Entity, applying the new attributes to the
     * entity
     *
     * @param livingEntity to load
     * @param data to load from
     */
    public static void loadAttributes(LivingEntity livingEntity, CommonTagList data) {
        if (data == null) {
            throw new IllegalArgumentException("Data can not be null");
        }
        EntityLivingHandle.fromBukkit(livingEntity).getAttributeMap().loadFromNBT(data);
    }

    /**
     * Saves the current attributes of an Entity to a new CommonTagList
     *
     * @param livingEntity to save
     * @return CommonTagList containing the saved data
     */
    public static CommonTagList saveAttributes(LivingEntity livingEntity) {
        return EntityLivingHandle.fromBukkit(livingEntity).getAttributeMap().saveToNBT();
    }

    /**
     * Loads the Block State information from a data compound
     *
     * @param blockState to load
     * @param data to load into the blockState
     */
    public static void loadBlockState(BlockState blockState, CommonTagCompound data) {
        TileEntityHandle.fromBukkit(blockState).load(data);
    }

    /**
     * Saves the Block State information to a data compound
     *
     * @param blockState to save
     * @return compound with saved data
     */
    public static CommonTagCompound saveBlockState(BlockState blockState) {
        return saveBlockState(blockState, null);
    }

    /**
     * Saves the Block State information to a data compound
     *
     * @param blockState to save
     * @param data to save the blockState to, null to create a new compound
     * @return compound with saved data
     */
    public static CommonTagCompound saveBlockState(BlockState blockState, CommonTagCompound data) {
        if (data == null) {
            data = new CommonTagCompound();
        }
        TileEntityHandle.fromBukkit(blockState).save(data);
        return data;
    }
}
