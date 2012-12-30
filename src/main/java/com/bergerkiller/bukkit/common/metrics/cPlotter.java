package com.bergerkiller.bukkit.common.metrics;

public class cPlotter extends Metrics.Plotter {
	private String name;
	private int value, last;
	
	public cPlotter(String name) {
		this.name = name;
		value = 0;
		last = 0;
	}
	
    @Override
    public String getColumnName() {
        return this.name;
    }
    
    @Override
    public int getValue() {
        this.last = this.value;
        return this.value;
    }
    
    
    public void setValue(int value) {
    	this.value= value;
    }
    
    public void increment() {
    	value++;
    }
    
    @Override
    public void reset() {
        this.value = this.value - this.last;
    }
}
