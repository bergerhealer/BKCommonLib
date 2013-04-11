package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.craftbukkit.v1_5_R2.util.FlatMap;
import org.bukkit.craftbukkit.v1_5_R2.util.LongHashSet;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.SafeDirectMethod;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

public class LongHashSetRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("util.LongHashSet"));
	public static final SafeConstructor<?> constructor1;
	public static final SafeConstructor<?> constructor2;
	public static final MethodAccessor<Boolean> add2 = TEMPLATE.getMethod("add", int.class, int.class);
	public static final MethodAccessor<Boolean> add1 = TEMPLATE.getMethod("add", long.class);
	public static final MethodAccessor<Boolean> contains2 = TEMPLATE.getMethod("contains", int.class, int.class);
	public static final MethodAccessor<Boolean> contains1 = TEMPLATE.getMethod("contains", long.class);
	public static final MethodAccessor<Void> remove2 = TEMPLATE.getMethod("remove", int.class, int.class);
	public static final MethodAccessor<Boolean> remove1;
	public static final MethodAccessor<Void> clear = TEMPLATE.getMethod("clear");
	public static final MethodAccessor<long[]> toArray = TEMPLATE.getMethod("toArray");
	public static final MethodAccessor<Long> popFirst = TEMPLATE.getMethod("popFirst");
	public static final MethodAccessor<long[]> popAll = TEMPLATE.getMethod("popAll");
	public static final MethodAccessor<Integer> hash = TEMPLATE.getMethod("hash", long.class);
	public static final MethodAccessor<Void> rehash0 = TEMPLATE.getMethod("rehash");
	public static final MethodAccessor<Void> rehash1 = TEMPLATE.getMethod("rehash", int.class);
	public static final MethodAccessor<Boolean> isEmpty = TEMPLATE.getMethod("isEmpty");
	public static final MethodAccessor<Integer> size = TEMPLATE.getMethod("size");
	public static final MethodAccessor<Iterator<Long>> iterator = TEMPLATE.getMethod("iterator");

	static {
		if (Common.IS_SPIGOT_SERVER) {
			// Use the fixed version, Spigots LongHashSet is TERRIBLY inefficient
			final ClassTemplate<LongHashSetFix> fixTemplate = ClassTemplate.create(LongHashSetFix.class);
			constructor1 = new SafeConstructor<LongHashSetFix>(null) {
				@Override
				public boolean isValid() {
					return true;
				}

				@Override
				public LongHashSetFix newInstance(Object... parameters) {
					LongHashSetFix instance = fixTemplate.newInstanceNull();
					instance.base = new HashSet<Long>();
					return instance;
				}
			};
			constructor2 = new SafeConstructor<LongHashSetFix>(null) {
				@Override
				public boolean isValid() {
					return true;
				}

				@Override
				public LongHashSetFix newInstance(Object... parameters) {
					LongHashSetFix instance = fixTemplate.newInstanceNull();
					instance.base = new HashSet<Long>((Integer) parameters[0]);
					return instance;
				}
			};
			// Remove long method - is private in Spigots implementation!
			final MethodAccessor<Boolean> removeInternal = TEMPLATE.getMethod("remove", long.class);
			final FieldAccessor<FlatMap<Object>> flat = TEMPLATE.getField("flat");
			final MethodAccessor<Void> flat_putter = new SafeMethod<Void>(FlatMap.class, "put", long.class, Object.class);
			remove1 = new SafeDirectMethod<Boolean>() {
				@Override
				public Boolean invoke(Object instance, Object... args) {
					if (instance instanceof LongHashSetFix) {
						return ((LongHashSetFix) instance).base.remove(args[0]);
					} else {
						// First put it into the flat instance
						flat_putter.invoke(flat.get(instance), args[0], null);
						// Then call remove(long)
						return removeInternal.invoke(instance, args);
					}
				}
			};
		} else {
			constructor1 = TEMPLATE.getConstructor();
			constructor2 = TEMPLATE.getConstructor(int.class);
			remove1 = TEMPLATE.getMethod("remove", long.class);
		}
	}

	private static final class LongHashSetFix extends LongHashSet {
		public Set<Long> base;

		@Override
		public boolean add(int msw, int lsw) {
			return add(MathUtil.longHashToLong(msw, lsw));
		}

		@Override
		public boolean add(long value) {
			return base.add(value);
		}

		@Override
		public void clear() {
			base.clear();
		}

		@Override
		public boolean contains(int msw, int lsw) {
			return contains(MathUtil.longHashToLong(msw, lsw));
		}

		@Override
		public boolean contains(long value) {
			return base.contains(value);
		}

		@Override
		public boolean isEmpty() {
			return base.isEmpty();
		}

		@Override
		public Iterator<Long> iterator() {
			return base.iterator();
		}

		@Override
		public long[] popAll() {
			long[] rval = toArray();
			clear();
			return rval;
		}

		@Override
		public long popFirst() {
			final Iterator<Long> iter = iterator();
			if (!iter.hasNext()) {
				return 0;
			}
			final Long value = iter.next();
			iter.remove();
			return value;
		}

		@Override
		public void remove(int msw, int lsw) {
			remove(MathUtil.longHashToLong(msw, lsw));
		}

		@Override
		public boolean remove(long value) {
			return base.remove(value);
		}

		@Override
		public int size() {
			return base.size();
		}

		@Override
		public long[] toArray() {
			long[] rval = new long[base.size()];
			int i = 0;
			for (Long value : base) {
				rval[i++] = value;
			}
			return rval;
		}
	}
}
