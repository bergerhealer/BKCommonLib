package com.bergerkiller.bukkit.common.wrappers;

import java.util.Iterator;

import com.bergerkiller.bukkit.common.reflection.classes.LongHashSetRef;

public class LongHashSet extends BasicWrapper implements Iterable<Long> {

	public LongHashSet() {
		this.setHandle(LongHashSetRef.constructor1.newInstance());
	}

	public LongHashSet(int size) {
		this.setHandle(LongHashSetRef.constructor2.newInstance(size));
	}

	public LongHashSet(Object handle) {
		this.setHandle(handle);
	}

	public Iterator<Long> iterator() {
		return LongHashSetRef.iterator.invoke(handle);
	}

	public boolean add(int lsw, int msw) {
		return LongHashSetRef.add2.invoke(handle, lsw, msw);
	}

	public boolean add(long value) {
		return LongHashSetRef.add1.invoke(handle, value);
	}

	public boolean contains(int lsw, int msw) {
		return LongHashSetRef.contains2.invoke(handle, lsw, msw);
	}

	public boolean contains(long value) {
		return LongHashSetRef.contains1.invoke(handle, value);
	}

	public void remove(int msw, int lsw) {
		LongHashSetRef.remove2.invoke(handle, msw, lsw);
	}

	public boolean remove(long value) {
		return LongHashSetRef.remove1.invoke(handle, value);
	}

	public void clear() {
		LongHashSetRef.clear.invoke(handle);
	}

	public long[] toArray() {
		return LongHashSetRef.toArray.invoke(handle);
	}

	public long popFirst() {
		return LongHashSetRef.popFirst.invoke(handle);
	}

	public long[] popAll() {
		return LongHashSetRef.popAll.invoke(handle);
	}

	public int hash(long value) {
		return LongHashSetRef.hash.invoke(handle, value);
	}

	public void rehash() {
		LongHashSetRef.rehash0.invoke(handle);
	}

	public void rehash(int newCapacity) {
		LongHashSetRef.rehash1.invoke(handle, newCapacity);
	}

	public boolean isEmpty() {
		return LongHashSetRef.isEmpty.invoke(handle);
	}

	public int size() {
		return LongHashSetRef.size.invoke(handle);
	}
}