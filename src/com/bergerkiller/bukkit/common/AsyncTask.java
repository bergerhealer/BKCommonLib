package com.bergerkiller.bukkit.common;

public abstract class AsyncTask extends ParameterWrapper implements Runnable {
	
	public AsyncTask() {
		this(null);
	}
	public AsyncTask(final String name) {
		this(name, 0);
	}
	public AsyncTask(final String name, final int priority, Object... arguments) {
		super(arguments);
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
					t.printStackTrace();
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
	
	private boolean running = false;
	private boolean stoprequested = false;
	private boolean looped = false;
	private final Thread thread;
	
	public static void sleep(long msdelay) {
		try {
			Thread.sleep(msdelay);
		} catch (InterruptedException ex) {}
	}
		
	public boolean isRunning() {
		return this.running;
	}
	public boolean isStopRequested() {
		return this.stoprequested;
	}
	
	public AsyncTask start() {
		return this.start(false);
	}
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
	public static boolean stop(AsyncTask task) {
		if (task == null) return false;
		task.stop();
		return true;
	}
	public AsyncTask stop() {
		this.stoprequested = true;
		return this;
	}

	public final AsyncTask waitFinished() {
		while (this.running) {
			sleep(20);
		}
		return this;
	}
}
