package com.bergerkiller.bukkit.common.wrappers;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * This class is a wrapper of the DataWatcher class from CraftBukkit
 * DataWatcher ref reflects the correct methods to rite dan read values
 * 
 * @author lenis0012
 * @category Wrappers
 */
public class DataWatcher extends BasicWrapper<Object> {

	public DataWatcher() {
		setHandle(DataWatcherRef.TEMPLATE.newInstance());
	}

	/**
	 * Write a new value to the watched objects
	 * 
	 * @param index Object index
	 * @param value Value
	 */
	public void write(int index, Object value) {
		DataWatcherRef.write.invoke(handle, index, value);
	}
	
	/**
	 * Read a byte from the watched objects
	 * 
	 * @param index Object index
	 * @return Byte
	 */
	public byte getByte(int index) {
		return read(index, Byte.class);
	}
	
	/**
	 * Read an integer from the watched objects
	 * 
	 * @param index Object index
	 * @return Integer
	 */
	public int getInt(int index) {
		return read(index, Integer.class);
	}
	
	/**
	 * Read a CraftBukkit itemstack from the watched objects
	 * And convert it to a Bukkit ItemStack
	 * 
	 * @param index Object index
	 * @return Bukkit ItemStack
	 */
	public ItemStack getItemStack(int index) {
		Object it = read(index, CommonUtil.getNMSClass("ItemStack"));
		return ParseUtil.convert(it, ItemStack.class);
	}
	
	/**
	 * Read a short from the watched objects
	 * 
	 * @param index Object index
	 * @return Short
	 */
	public short getShort(int index) {
		return read(index, Short.class);
	}
	
	/**
	 * Read string from the watched objects
	 * 
	 * @param index Object index
	 * @return String
	 */
	public String getString(int index) {
		return read(index, String.class);
	}
	
	/**
	 * Read an object from the watched objects and convert it
	 * 
	 * @param index Object index
	 * @param def Defination
	 * @return Object
	 */
	public <T> T read(int index, T def) {
		return ParseUtil.convert(read(index), def);
	}
	
	/**
	 * Read an object from the watched objects and convert it
	 * 
	 * @param index Object index
	 * @param type Object type
	 * @param def Defination
	 * @return Object
	 */
	public <T> T read(int index, Class<T> type, T def) {
		return ParseUtil.convert(read(index), type, def);
	}
	
	/**
	 * Read an object from the watched objects and convert it
	 * 
	 * @param index Object index
	 * @param type Object type
	 * @return Object
	 */
	public <T> T read(int index, Class<T> type) {
		return ParseUtil.convert(read(index), type);
	}
	
	/**
	 * Read an object from the watched objects
	 * 
	 * @param index Object index
	 * @return Object
	 */
	public Object read(int index) {
		return DataWatcherRef.read.invoke(handle, index);
	}
	
	/**
	 * Watch an object
	 * 
	 * @param index Object index
	 * @param value Value
	 */
	public void watch(int index, Object value) {
		DataWatcherRef.watch.invoke(handle, index, value);
	}

	/**
	 * Get all watched objects
	 * 
	 * @return Watched objects
	 */
	public List<Object> getAllWatched() {
		return DataWatcherRef.returnAllWatched.invoke(handle);
	}

	/**
	 * Get all watched objects and unwatch them
	 * 
	 * @return Watched objects
	 */
	public List<Object> unwatchAndGetAllWatched() {
		return DataWatcherRef.unwatchAndReturnAllWatched.invoke(handle);
	}
}
