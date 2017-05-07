package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityLiving;
import com.bergerkiller.reflection.net.minecraft.server.NMSNBT;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;

import java.util.List;

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
    public static Object loadMobEffect(CommonTagCompound compound) {
        return MobEffect.b((NBTTagCompound) compound.getHandle());
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
        ((Entity) Conversion.toEntityHandle.convert(entity)).e((NBTTagCompound) compound.getHandle());
        return compound;
    }

    /**
     * Loads an entity with data from the Tag Compound specified
     *
     * @param entity to load
     * @param compound to load from
     */
    public static void loadEntity(org.bukkit.entity.Entity entity, CommonTagCompound compound) {
        ((Entity) Conversion.toEntityHandle.convert(entity)).f((NBTTagCompound) compound.getHandle());
    }

    /**
     * Saves an itemstack to a Tag Compound
     *
     * @param item ItemStack
     * @param compound TagCompound
     */
    public static void saveItemStack(org.bukkit.inventory.ItemStack item, CommonTagCompound compound) {
        ItemStack stack = (ItemStack) Conversion.toItemStackHandle.convert(item);
        stack.save((NBTTagCompound) compound.getHandle());
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
        ((FoodMetaData) foodMetaData).b((NBTTagCompound) compound.getHandle());
        return compound;
    }

    /**
     * Loads Food Meta Data with data from the Tag Compound specified
     *
     * @param foodMetaData to load
     * @param compound to load from
     */
    public static void loadFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
        ((FoodMetaData) foodMetaData).a((NBTTagCompound) compound.getHandle());
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
        if (inventoryHandle instanceof PlayerInventory) {
            if (list == null) {
                list = new CommonTagList();
            }
            ((PlayerInventory) inventoryHandle).a((NBTTagList) list.getHandle());
        } else if (inventoryHandle instanceof InventoryEnderChest) {
            Object handle = ((InventoryEnderChest) inventoryHandle).h();
            if (list == null) {
                return (CommonTagList) CommonTag.create(handle);
            } else {
                List<?> data = (List<?>) NMSNBT.getData(handle);
                for (Object elem : data) {
                    list.addValue(elem);
                }
                return list;
            }
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
        NBTTagList nbt = (NBTTagList) list.getHandle();
        if (inventoryHandle == null) {
            throw new IllegalArgumentException("This kind of inventory lacks a handle to save");
        } else if (inventoryHandle instanceof PlayerInventory) {
            ((PlayerInventory) inventoryHandle).b(nbt);
        } else if (inventoryHandle instanceof InventoryEnderChest) {
            ((InventoryEnderChest) inventoryHandle).a(nbt);
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
        Object livingHandle = Conversion.toEntityHandle.convert(livingEntity);

        // Clear old attributes and force a re-create
        NMSEntityLiving.attributeMap.set(livingHandle, new AttributeMapServer());
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
        AttributeMapServer map = CommonNMS.getEntityAttributes(livingEntity);
        GenericAttributes.a(map, (NBTTagList) data.getHandle());
    }

    /**
     * Saves the current attributes of an Entity to a new CommonTagList
     *
     * @param livingEntity to save
     * @return CommonTagList containing the saved data
     */
    public static CommonTagList saveAttributes(LivingEntity livingEntity) {
        AttributeMapServer map = CommonNMS.getEntityAttributes(livingEntity);
        return (CommonTagList) CommonTag.create(GenericAttributes.a(map));
    }

    /**
     * Loads the Block State information from a data compound
     *
     * @param blockState to load
     * @param data to load into the blockState
     */
    public static void loadBlockState(BlockState blockState, CommonTagCompound data) {
        NMSTileEntity.load.invoke(Conversion.toTileEntityHandle.convert(blockState), data.getHandle());
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
        NMSTileEntity.save.invoke(Conversion.toTileEntityHandle.convert(blockState), data.getHandle());
        return data;
    }
}
