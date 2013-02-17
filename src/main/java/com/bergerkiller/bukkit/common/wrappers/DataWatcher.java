package com.bergerkiller.bukkit.common.wrappers;

import java.util.List;

import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;

public class DataWatcher {
	private Object handle;
	
	public DataWatcher() {
		this.handle = DataWatcherRef.TEMPLATE.newInstance();
	}
	
	public void write(int index, Object value) {
		DataWatcherRef.write.invoke(handle, index, value);
	}
	
	public void watch(int index, Object value) {
		DataWatcherRef.watch.invoke(handle, index, value);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getAllWatched() {
		return DataWatcherRef.returnAllWatched.invoke(handle);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> unwatchAndGetAllWatched() {
		return DataWatcherRef.unwatchAndReturnAllWatched.invoke(handle);
	}
	
	public Object getHandle() {
		return handle;
	}
}
