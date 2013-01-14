package com.bergerkiller.bukkit.common;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Task implements Runnable {
	private final JavaPlugin plugin;
	private int id = -1;

	public Task(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the Plugin this Task belongs to
	 * 
	 * @return Plugin
	 */
	public final JavaPlugin getPlugin() {
		return this.plugin;
	}

	/**
	 * Checks if this Task is currently being executed
	 * 
	 * @return True if running, False if not
	 */
	public boolean isRunning() {
		return this.id != -1 && Bukkit.getServer().getScheduler().isCurrentlyRunning(this.id);
	}

	/**
	 * Checks whether this Task is still queued
	 * 
	 * @return True if still queued, False if not
	 */
	public boolean isQueued() {
		return Bukkit.getServer().getScheduler().isQueued(this.id);
	}

	/**
	 * Stops a Task
	 * 
	 * @param task to stop, ignored if null
	 * @return True if stopped, False if not
	 */
	public static boolean stop(Task task) {
		if (task == null)
			return false;
		task.stop();
		return true;
	}

	/**
	 * Stops this Task from executing
	 * 
	 * @return This Task
	 */
	public Task stop() {
		if (this.id != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.id);
			this.id = -1;
		}
		return this;
	}

	/**
	 * Starts this Task the very next tick
	 * 
	 * @return This Task
	 */
	public Task start() {
		this.id = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this);
		return this;
	}

	/**
	 * Starts this Task after the delay specified
	 * 
	 * @param delay in ticks
	 * @return This Task
	 */
	public Task start(long delay) {
		this.id = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this, delay);
		return this;
	}

	/**
	 * Starts this Task after the delay specified, and keeps running it at the interval
	 * 
	 * @param delay in ticks
	 * @param interval in ticks
	 * @return This Task
	 */
	public Task start(long delay, long interval) {
		this.id = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, delay, interval);
		return this;
	}
}
