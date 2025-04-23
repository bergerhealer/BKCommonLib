package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerInventoryHandle;
import com.bergerkiller.generated.net.minecraft.world.food.FoodMetaDataHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.InventoryEnderChestHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityHandle;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
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
     * Migrates the player profile data so that it can run on the current version of the server.
     * This performs data migrations (DFU).
     * Should be called before calling {@link #loadEntity(Entity, CommonTagCompound)} when loading
     * data for players.
     *
     * @param playerProfileData Player Profile NBT Data
     * @return Migrated NBT Data. Can be the same as the input tag.
     */
    public static CommonTagCompound migratePlayerProfileData(CommonTagCompound playerProfileData) {
        return PlayerFileDataHandler.INSTANCE.migratePlayerData(playerProfileData);
    }

    // Method is gone as of 1.20.5 because of data components
    //
    // /**
    //  * Saves an itemstack to a Tag Compound
    //  *
    //  * @param item ItemStack
    //  * @param compound TagCompound
    //  */
    // public static void saveItemStack(org.bukkit.inventory.ItemStack item, CommonTagCompound compound) {
    //     ItemStackHandle stack = ItemStackHandle.fromBukkit(item);
    //     if (stack != null) {
    //        stack.saveToNBT(compound);
    //     }
    // }

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
     * Saves the equipment slots of an entity as a Tag Compound. Only works on Minecraft 1.21.5
     * and later where this is used. Prior, this data was saved in the Inventory, which is saved with
     * {@link #saveInventory(Inventory, CommonTagList)} instead.<br>
     * <br>
     * Will return {@link CommonTagCompound#EMPTY} if the entity has no equipment or is null, or this is
     * a Minecraft server version before 1.21.5. Check with {@link CommonTagCompound#isEmpty()}.
     *
     * @param equipment to save, can be null
     * @return Saved equipment data tag, read-only. Empty tag if not supported or older Minecraft version.
     */
    public static CommonTagCompound saveEquipment(org.bukkit.inventory.EntityEquipment equipment) {
        if (equipment == null ){
            return CommonTagCompound.EMPTY;
        }

        Entity holder = equipment.getHolder();
        if (!(holder instanceof LivingEntity)) {
            throw new UnsupportedOperationException("Cannot save equipment of a non-living entity: " + holder);
        }

        EntityLivingHandle handle = EntityLivingHandle.fromBukkit((LivingEntity) holder);
        CommonTagCompound nbt = handle.saveEquipment();
        if (nbt == null) {
            nbt = CommonTagCompound.EMPTY;
        }
        return nbt;
    }

    /**
     * Loads the equipment slots of an entity from the Tag Compound specified. Only works on Minecraft 1.21.5
     * and later where this is used. Prior, this data was saved in the Inventory, which is loaded with
     * {@link #loadInventory(Inventory, CommonTagList)} instead.<br>
     * <br>
     * Specifying {@link CommonTagCompound#EMPTY} will clear the entity's equipment slots. A null equipment
     * input acts as a no-op. Handled here since LivingEntity getEquipment() is allowed to return null
     * for some reason.
     *
     * @param equipment to load, can be null
     * @param data tag compound to load from
     */
    public static void loadEquipment(org.bukkit.inventory.EntityEquipment equipment, CommonTagCompound data) {
        if (equipment == null) {
            return;
        }

        Entity holder = equipment.getHolder();
        if (!(holder instanceof LivingEntity)) {
            throw new UnsupportedOperationException("Cannot load equipment for a non-living entity: " + holder);
        }

        EntityLivingHandle handle = EntityLivingHandle.fromBukkit((LivingEntity) holder);
        handle.loadEquipment(data);
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
        TileEntityHandle.fromBukkit(blockState).load(BlockStateHandle.T.getBlockData.invoke(blockState), data);
    }

    /**
     * Saves the Block State information to a data compound
     *
     * @param blockState to save
     * @return compound with saved data
     */
    public static CommonTagCompound saveBlockState(BlockState blockState) {
        return TileEntityHandle.fromBukkit(blockState).save();
    }

    /**
     * Saves the Block State information to a data compound
     *
     * @param blockState to save
     * @param data to save the blockState to, null to create a new compound
     * @return compound with saved data
     * @deprecated Not reliable, use {@link #saveBlockState(BlockState)} instead
     */
    @Deprecated
    public static CommonTagCompound saveBlockState(BlockState blockState, CommonTagCompound data) {
        CommonTagCompound savedData = TileEntityHandle.fromBukkit(blockState).save();
        if (data != null) {
            data.putAll(savedData);
            return data;
        } else {
            return savedData;
        }
    }
}
