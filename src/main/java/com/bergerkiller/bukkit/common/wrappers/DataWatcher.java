package com.bergerkiller.bukkit.common.wrappers;

import java.util.List;

import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;

/**
 * This class is a wrapper of the DataWatcher class from CraftBukkit
 * DataWatcher ref reflects the correct methods to rite dan read values
 * 
 * @author lenis0012
 * @category Wrappers
 */
public class DataWatcher {
	private Object handle;
	
	public DataWatcher() {
		this.handle = DataWatcherRef.TEMPLATE.newInstance();
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
	
	/**
	 * Get the vanilla object
	 * 
	 * @return Vanilla object
	 */
	public Object getHandle() {
		return handle;
	}
}
