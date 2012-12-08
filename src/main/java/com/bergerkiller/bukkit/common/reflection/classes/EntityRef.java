package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeDirectField;
import com.bergerkiller.bukkit.common.reflection.SafeField;

import net.minecraft.server.Entity;

public class EntityRef {
	public static final FieldAccessor<org.bukkit.entity.Entity> bukkitEntity = new SafeField<org.bukkit.entity.Entity>(Entity.class, "bukkitEntity");
	public static final FieldAccessor<Integer> chunkX = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Entity) instance).ai; }
		public boolean set(Object instance, Integer value) { ((Entity) instance).ai = value; return true; }
	};
	public static final FieldAccessor<Integer> chunkY = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Entity) instance).aj; }
		public boolean set(Object instance, Integer value) { ((Entity) instance).aj = value; return true; }
	};
	public static final FieldAccessor<Integer> chunkZ = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Entity) instance).ak; }
		public boolean set(Object instance, Integer value) { ((Entity) instance).ak = value; return true; }
	};
	public static final FieldAccessor<Boolean> positionChanged = new SafeDirectField<Boolean>() {
		public Boolean get(Object instance) { return ((Entity) instance).am; }
		public boolean set(Object instance, Boolean value) { ((Entity) instance).am = value; return true; }
	};
}
