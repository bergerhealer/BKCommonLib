package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Collection;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;

public class NBTRef {
	public static final ClassTemplate<?> NBTBase = NMSClassTemplate.create("NBTBase");
	public static final ClassTemplate<?> NBTTagList = NMSClassTemplate.create("NBTTagList");
	public static final ClassTemplate<?> NBTTagCompound = NMSClassTemplate.create("NBTTagCompound");
	public static final SafeMethod<Byte> getTypeId = NBTBase.getMethod("getTypeId");
	public static final FieldAccessor<Byte> nbtListType = NBTTagList.getField("type");
	public static final MethodAccessor<Void> setName = NBTBase.getMethod("setName", String.class);
	public static final MethodAccessor<String> getName = NBTBase.getMethod("getName");
	public static final MethodAccessor<Object> clone = NBTBase.getMethod("clone");
	public static final MethodAccessor<Void> nbtListAdd = NBTTagList.getMethod("add", NBTBase.getType());
	public static final MethodAccessor<Integer> nbtListSize = NBTTagList.getMethod("size");
	public static final MethodAccessor<Object> nbtListGet = NBTTagList.getMethod("get", int.class);
	public static final MethodAccessor<Collection<Object>> nbtCompoundGetValues = NBTTagCompound.getMethod("c");
	public static final MethodAccessor<Void> nbtCompoundRemove = NBTTagCompound.getMethod("remove", String.class);
	public static final MethodAccessor<Void> nbtCompoundSet = NBTTagCompound.getMethod("set", String.class, NBTBase.getType());
	public static final MethodAccessor<Object> nbtCompoundGet = NBTTagCompound.getMethod("get", String.class);
	public static final MethodAccessor<Boolean> nbtCompoundContains = NBTTagCompound.getMethod("hasKey", String.class);
	public static final MethodAccessor<Boolean> nbtCompoundIsEmpty = NBTTagCompound.getMethod("isEmpty");
}
