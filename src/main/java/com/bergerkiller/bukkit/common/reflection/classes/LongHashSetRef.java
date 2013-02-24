package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class LongHashSetRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("LongHashSet"));
	public static final SafeConstructor<?> constructor1 = TEMPLATE.getConstructor();
	public static final SafeConstructor<?> constructor2 = TEMPLATE.getConstructor(int.class);
	public static final MethodAccessor<Boolean> add2 = TEMPLATE.getMethod("add", int.class, int.class);
	public static final MethodAccessor<Boolean> add1 = TEMPLATE.getMethod("add", long.class);
	public static final MethodAccessor<Boolean> contains2 = TEMPLATE.getMethod("contains", int.class, int.class);
	public static final MethodAccessor<Boolean> contains1 = TEMPLATE.getMethod("contains", long.class);
	public static final MethodAccessor<Void> remove2 = TEMPLATE.getMethod("remove", int.class, int.class);
	public static final MethodAccessor<Boolean> remove1 = TEMPLATE.getMethod("remove", long.class);
	public static final MethodAccessor<Void> clear = TEMPLATE.getMethod("clear");
	public static final MethodAccessor<long[]> toArray = TEMPLATE.getMethod("toArray");
	public static final MethodAccessor<Long> popFirst = TEMPLATE.getMethod("popFirst");
	public static final MethodAccessor<long[]> popAll = TEMPLATE.getMethod("popAll");
	public static final MethodAccessor<Integer> hash = TEMPLATE.getMethod("hash", long.class);
	public static final MethodAccessor<Void> rehash0 = TEMPLATE.getMethod("rehash");
	public static final MethodAccessor<Void> rehash1 = TEMPLATE.getMethod("rehash", int.class);
	public static final MethodAccessor<Boolean> isEmpty = TEMPLATE.getMethod("isEmpty");
	public static final MethodAccessor<Integer> size = TEMPLATE.getMethod("size");
	public static final MethodAccessor<Object> iterator = TEMPLATE.getMethod("iterator");
}
