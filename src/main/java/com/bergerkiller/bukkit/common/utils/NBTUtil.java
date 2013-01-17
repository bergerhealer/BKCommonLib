package com.bergerkiller.bukkit.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.FoodMetaData;
import net.minecraft.server.v1_4_R1.InventoryEnderChest;
import net.minecraft.server.v1_4_R1.MobEffect;
import net.minecraft.server.v1_4_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.NBTTagDouble;
import net.minecraft.server.v1_4_R1.NBTTagFloat;
import net.minecraft.server.v1_4_R1.NBTTagList;
import net.minecraft.server.v1_4_R1.PlayerInventory;

/**
 * Contains utility functions for dealing with NBT data
 */
public class NBTUtil {

	/**
	 * Obtains a double NBT-Tag List from an array of double values
	 * 
	 * @param values
	 * @return NBT-Tag List with the values
	 */
	public static NBTTagList doubleArrayToList(double... values) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < values.length; ++i) {
			nbttaglist.add(new NBTTagDouble((String) null, values[i]));
		}
		return nbttaglist;
	}

	/**
	 * Obtains a float NBT-Tag List from an array of float values
	 * 
	 * @param values
	 * @return NBT-Tag List with the values
	 */
	public static NBTTagList floatArrayToList(float... values) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < values.length; ++i) {
			nbttaglist.add(new NBTTagFloat((String) null, values[i]));
		}
		return nbttaglist;
	}

	/**
	 * Reads an NBT-Tag Compound from an input stream
	 * 
	 * @param stream to read from
	 * @return NBT Tag Compound
	 * @throws IOException
	 */
	public static NBTTagCompound readCompound(InputStream stream) throws IOException {
		return NBTCompressedStreamTools.a(stream);
	}

	/**
	 * Writes an NBT-Tag Compound to an output stream
	 * 
	 * @param compound to write
	 * @param stream to write to
	 * @throws IOException
	 */
	public static void writeCompound(NBTTagCompound compound, OutputStream stream) throws IOException {
		NBTCompressedStreamTools.a(compound, stream);
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
	 * Saves an UUID to an NBT Tag Compound
	 * 
	 * @param uuid to save
	 * @param compound to save to
	 */
	public static void saveUUID(UUID uuid, NBTTagCompound compound) {
		compound.setLong("WorldUUIDLeast", uuid.getLeastSignificantBits());
		compound.setLong("WorldUUIDMost", uuid.getMostSignificantBits());
	}

	/**
	 * Loads a new UUID object from an NBT Tag Compound
	 * 
	 * @param compound to load from
	 * @return newly loaded UUID
	 */
	public static UUID loadUUID(NBTTagCompound compound) {
		return new UUID(compound.getLong("WorldUUIDMost"), compound.getLong("WorldUUIDLeast"));
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
