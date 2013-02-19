package com.bergerkiller.bukkit.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bergerkiller.bukkit.common.nbt.NBTTagInfo;
import com.bergerkiller.bukkit.common.reflection.classes.NBTRef;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.FoodMetaData;
import net.minecraft.server.v1_4_R1.InventoryEnderChest;
import net.minecraft.server.v1_4_R1.MobEffect;
import net.minecraft.server.v1_4_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.NBTTagList;
import net.minecraft.server.v1_4_R1.PlayerInventory;

/**
 * Contains utility functions for dealing with NBT data
 */
public class NBTUtil {
	/**
	 * Creates an NBT Tag handle to store the data specified in<br>
	 * All primitive types, including byte[] and int[], and list/maps are supported
	 * 
	 * @param name of the handle
	 * @param data to store in this handle initially
	 * @return new handle
	 */
	public static Object createHandle(String name, Object data) {
		return NBTTagInfo.findInfo(data).createHandle(name, data);
	}

	/**
	 * Obtains the raw data from an NBT Tag handle<br>
	 * NBTTagList and NBTTagCompound return a List and Map of NBT Tags respectively
	 * 
	 * @param nbtTagHandle to get the value of
	 * @return the NBTTag data
	 */
	public static Object getData(Object nbtTagHandle) {
		return NBTTagInfo.findInfo(nbtTagHandle).getData(nbtTagHandle);
	}

	/**
	 * Gets the type Id of the tag used to identify it
	 * 
	 * @param nbtTagHandle to read from
	 * @return tag type id
	 */
	public static byte getTypeId(Object nbtTagHandle) {
		if (nbtTagHandle == null) {
			return (byte) 0;
		}
		return NBTRef.getTypeId.invoke(nbtTagHandle).byteValue();
	}

	/**
	 * Reads an NBTTagCompound handle from an input stream
	 * 
	 * @param stream to read from
	 * @return NBTTagCompound
	 * @throws IOException
	 */
	public static Object readCompound(InputStream stream) throws IOException {
		return NBTCompressedStreamTools.a(stream);
	}

	/**
	 * Writes an NBTTagCompound to an output stream
	 * 
	 * @param compound to write
	 * @param stream to write to
	 * @throws IOException
	 */
	public static void writeCompound(Object compound, OutputStream stream) throws IOException {
		NBTCompressedStreamTools.a((NBTTagCompound) compound, stream);
	}

	/**
	 * Reads a mob effect from an NBT Tag Compound
	 * 
	 * @param compound to read a mob effect from
	 * @return Loaded MobEffect
	 */
	public static MobEffect loadMobEffect(NBTTagCompound compound) {
		return MobEffect.b(compound);
	}

	/**
	 * Saves entity data to a new NBT Tag Compound
	 * 
	 * @param entity to save
	 */
	public static NBTTagCompound saveToNBT(Entity entity) {
		NBTTagCompound compound = new NBTTagCompound();
		entity.d(compound);
		return compound;
	}

	/**
	 * Saves entity data to the NBT Tag Compound specified
	 * 
	 * @param entity to save
	 * @param compound to save to
	 */
	public static void saveToNBT(Entity entity, NBTTagCompound compound) {
		entity.d(compound);
	}

	/**
	 * Loads entity data from the NBT Tag Compound specified
	 * 
	 * @param entity to load
	 * @param compound to load from
	 */
	public static void loadFromNBT(Entity entity, NBTTagCompound compound) {
		entity.e(compound);
	}

	/**
	 * Saves food meta data to the NBT Tag Compound specified
	 * 
	 * @param foodMetaData to save
	 * @param compound to save to
	 */
	public static void saveToNBT(FoodMetaData foodMetaData, NBTTagCompound compound) {
		foodMetaData.b(compound);
	}

	/**
	 * Loads the food meta data using the NBT Tag Compound specified
	 * 
	 * @param foodMetaData to load
	 * @param compound to load from
	 */
	public static void loadFromNBT(FoodMetaData foodMetaData, NBTTagCompound compound) {
		foodMetaData.a(compound);
	}

	/**
	 * Saves inventory data to an NBT Tag List
	 * 
	 * @param inventory to save
	 * @return Saved NBT Tag List data
	 */
	public static NBTTagList saveToNBT(PlayerInventory inventory) {
		return inventory.a(new NBTTagList());
	}

	/**
	 * Loads an NBT Tag List into an inventory
	 * 
	 * @param inventory to load
	 * @param list to load from
	 */
	public static void loadFromNBT(PlayerInventory inventory, NBTTagList list) {
		inventory.b(list);
	}

	/**
	 * Saves inventory data to an NBT Tag List
	 * 
	 * @param inventory to save
	 * @return Saved NBT Tag List data
	 */
	public static NBTTagList saveToNBT(InventoryEnderChest inventory) {
		return inventory.g();
	}

	/**
	 * Loads an NBT Tag List into an inventory
	 * 
	 * @param inventory to load
	 * @param list to load from
	 */
	public static void loadFromNBT(InventoryEnderChest inventory, NBTTagList list) {
		inventory.a(list);
	}
}
