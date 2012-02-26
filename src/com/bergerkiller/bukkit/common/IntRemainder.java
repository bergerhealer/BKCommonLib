package com.bergerkiller.bukkit.common;

import java.util.Arrays;

import net.minecraft.server.MathHelper;

public class IntRemainder {
	
	private double contained = 0;
	private final CircularInteger counter;
	private final int[] values;
	public IntRemainder(double initialvalue, int decimals) {
		if (decimals < 1) {
			throw new IllegalArgumentException("Decimal count needs to be higher than 0");
		}
		this.values = new int[10 * decimals];
		this.counter = new CircularInteger(this.values.length);
		this.set(initialvalue);
	}
	
	public void set(double value) {
		this.contained = value;
		//set floor
		int floor = MathHelper.floor(value);
		Arrays.fill(this.values, floor);
		//get remainder (1.2 -> .2)
		floor = (int) ((value - floor) * this.values.length);
		for (int i = 0; i < floor; i++) {
			this.values[i]++;
		}
	}
	
	public int next() {
		return this.values[this.counter.next()];
	}
	
	public double get() {
		return this.contained;
	}
	
	public int[] getValues() {
		return this.values;
	}

}
