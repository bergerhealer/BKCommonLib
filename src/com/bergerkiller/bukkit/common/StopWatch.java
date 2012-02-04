package com.bergerkiller.bukkit.common;

public class StopWatch {
	public static final StopWatch instance = new StopWatch();
	
	private long prevtime;
	private long prevdur;
	
	public StopWatch start() {
		this.prevtime = System.nanoTime();
		return this;
	}
	public StopWatch clear() {
		this.prevtime = 0;
		this.prevdur = 0;
		return this;
	}
	public double get() {
		return (double) prevdur / 1E6D;
	}
	public double get(int scale) {
		return (double) prevdur / 1E6D / (double) scale;
	}
	public StopWatch set(long elapsednanotime, double strength) {
		elapsednanotime += (1.0 - strength) * (this.prevdur - elapsednanotime);
		this.prevdur = elapsednanotime;
		return this;
	}
	public StopWatch set(long elapsednanotime) {
		return this.set(elapsednanotime, 1.0);
	}
	
	public StopWatch next() {
		return this.next(1.0);
	}
	public StopWatch stop() {
		return this.stop(1.0);
	}
	public StopWatch next(double strength) {
		return this.set(this.prevdur - prevtime + System.nanoTime(), strength).start();
	}
	public StopWatch stop(double strength) {
		return this.set(System.nanoTime() - this.prevtime, strength).start();
	}
	
	
	public StopWatch log(final String name) {
		System.out.println(name + ": " + this.get() + " ms");
		return this;
	}

}
