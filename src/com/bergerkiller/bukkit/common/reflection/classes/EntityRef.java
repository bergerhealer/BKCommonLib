package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.SafeDirectField;
import com.bergerkiller.bukkit.common.reflection.SafeField;

import net.minecraft.server.Entity;

public class EntityRef {
	public static final SafeField<org.bukkit.entity.Entity> bukkitEntity = new SafeField<org.bukkit.entity.Entity>(Entity.class, "bukkitEntity");
	public static final SafeDirectField<Integer, Entity> chunkX = new SafeDirectField<Integer, Entity>() {
		public Integer get(Entity instance) { return instance.ai; }
		public void set(Entity instance, Integer value) { instance.ai = value; }
	};
	public static final SafeDirectField<Integer, Entity> chunkY = new SafeDirectField<Integer, Entity>() {
		public Integer get(Entity instance) { return instance.aj; }
		public void set(Entity instance, Integer value) { instance.aj = value; }
	};
	public static final SafeDirectField<Integer, Entity> chunkZ = new SafeDirectField<Integer, Entity>() {
		public Integer get(Entity instance) { return instance.ak; }
		public void set(Entity instance, Integer value) { instance.ak = value; }
	};
	public static final SafeDirectField<Boolean, Entity> positionChanged = new SafeDirectField<Boolean, Entity>() {
		public Boolean get(Entity instance) { return instance.am; }
		public void set(Entity instance, Boolean value) { instance.am = value; }
	};
}
