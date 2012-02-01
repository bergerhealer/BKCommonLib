package com.bergerkiller.bukkit.common;

public class StopWatch {
	public static final StopWatch instance = new StopWatch();
	
	private long prevtime;
	private long prevdur;
	
	public StopWatch start() {
		this.prevtime = System.nanoTime();
		return this;
	}
	public double get() {
		return (double) prevdur / 1E6;
	}
	public StopWatch next() {
		this.prevdur += System.nanoTime() - prevtime;
		return this.start();
	}
	public StopWatch stop() {
		this.prevdur = System.nanoTime() - prevtime;
		return this.start();
	}
	public StopWatch log(final String name) {
		System.out.println(name + ": " + this.get() + " ms");
		return this;
	}

}
