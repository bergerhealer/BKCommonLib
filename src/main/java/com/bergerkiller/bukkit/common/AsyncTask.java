package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * A wrapper class around a Thread that can:<br>
 * - Create an infinite loop that can be stopped externally<br>
 * - Provide an error-free sleep function<br>
 */
public abstract class AsyncTask implements Runnable {
	private boolean running = false;
	private boolean stoprequested = false;
	private boolean looped = false;
	private final Thread thread;

	/**
	 * Initializes a new nameless Async task
	 */
	public AsyncTask() {
		this(null);
	}

	/**
	 * Initializes a new named Async task
	 * 
	 * @param name of the Async task
	 */
	public AsyncTask(final String name) {
		this(name, 0);
	}

	/**
	 * Initializes a new named and prioritized Async task
	 * 
	 * @param name of the Async task
	 * @param priority for the Async task
	 */
	public AsyncTask(final String name, final int priority) {
		final AsyncTask task = this;
		this.thread = new Thread() {
			public void run() {
				task.running = true;
				try {
					if (task.looped) {
						while (!task.stoprequested) {
							task.run();
						}
					} else {
						task.run();
					}
				} catch (Throwable t) {
					CommonUtil.printFilteredStackTrace(t);
				}
				task.running = false;
			}
		};
		if (priority >= 1 && priority <= 10) {
			this.thread.setPriority(priority);
		}
		if (name != null) {
			this.thread.setName(name);
		}
	}

	/**
	 * A sleep method that handles an interruption automatically
	 * 
	 * @param msdelay to sleep
	 */
	public static void sleep(long msdelay) {
		try {
			Thread.sleep(msdelay);
		} catch (InterruptedException ex) {
		}
	}

	/**
	 * Checks if this Async task is running
	 * 
	 * @return True if running, False if not
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Checks if this Async task is requested to stop
	 * 
	 * @return True if it should stop, False if not
	 */
	public boolean isStopRequested() {
		return this.stoprequested;
	}

	/**
	 * Starts this Async task (once, not looped)
	 * 
	 * @return This Async task
	 */
	public AsyncTask start() {
		return this.start(false);
	}

	/**
	 * Starts this Async task
	 * 
	 * @param looped state, True to infinitely loop the run method of this Thread
	 * @return This Async task
	 */
	public AsyncTask start(boolean looped) {
		this.stoprequested = false;
		this.looped = looped;
		try {
			this.thread.start();
		} catch (IllegalThreadStateException ex) {
			ex.printStackTrace();
		}
		return this;
	}

	/**
	 * Stops an Async task
	 * 
	 * @param task to stop, if null it is ignored
	 * @return True if stopped, False if not
	 */
	public static boolean stop(AsyncTask task) {
		if (task == null) {
			return false;
		}
		task.stop();
		return true;
	}

	/**
	 * Stops this Async task
	 * 
	 * @return This Async task
	 */
	public AsyncTask stop() {
		this.stoprequested = true;
		return this;
	}

	/**
	 * Waits until this Async task finished executing
	 * 
	 * @return This Async task
	 */
	public final AsyncTask waitFinished() {
		while (this.running) {
			sleep(20);
		}
		return this;
	}
}
