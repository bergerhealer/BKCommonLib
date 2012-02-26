package com.bergerkiller.bukkit.common;

import java.util.Iterator;

public class CircularInteger implements Iterable<Integer> {
	
	private int value;
	private final int size;
	public CircularInteger(final int size) {
		this.value = 0;
		this.size = size;
	}
	
	public int next() {
		if (this.value == this.size) {
			return (this.value = 0);
		} else {
			return this.value++;
		}
	}
	public int previous() {
		if (this.value == -1) {
			return (this.value = this.size - 1);
		} else {
			return this.value--;
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		final CircularInteger me = this;
		return new Iterator<Integer>() {

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Integer next() {
				return me.next();
			}

			@Override
			public void remove() {
				me.previous();
			}
			
		};
	}

}
