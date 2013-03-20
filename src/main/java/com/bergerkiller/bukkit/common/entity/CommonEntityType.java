package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.classes.WorldRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Stores all internal information about an Entity
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonEntityType {
	public final ClassTemplate<?> nmsType;
	public final ClassTemplate<?> nmsHookType;
	public final ClassTemplate<?> commonType;
	public final ClassTemplate<?> bukkitType;
	private final SafeConstructor<?> nmsConstructor;
	private final SafeConstructor<?> nmsHookConstructor;
	private final SafeConstructor<?> commonConstructor;
	public final EntityType entityType;
	public final int networkUpdateInterval;
	public final int networkViewDistance;
	public final boolean networkIsMobile;

	public CommonEntityType(EntityType entityType, String nmsName, int networkViewDistance, int networkUpdateInterval, boolean networkIsMobile) {
		// Properties first
		this.networkUpdateInterval = networkUpdateInterval;
		this.networkViewDistance = networkViewDistance;
		this.networkIsMobile = networkIsMobile;
		this.entityType = entityType;

		// Default 'UNKNOWN' instance
		if (LogicUtil.nullOrEmpty(nmsName)) {
			this.nmsType = NMSClassTemplate.create("Entity");
			this.nmsHookType = new ClassTemplate();
			this.commonType = ClassTemplate.create(CommonEntity.class);
			this.bukkitType = ClassTemplate.create(Entity.class);
			this.nmsConstructor = null;
			this.nmsHookConstructor = null;
			this.commonConstructor = this.commonType.getConstructor(Entity.class);
			return;
		}

		// Obtain Bukkit type
		this.bukkitType = ClassTemplate.create(entityType.getEntityClass());

		// Obtain Common class type and constructor
		Class<?> type = CommonUtil.getClass(Common.COMMON_ROOT + ".entity.Common" + nmsName);
		if (type == null) {
			this.commonType = ClassTemplate.create(CommonEntity.class);
			this.commonConstructor = this.commonType.getConstructor(Entity.class);
		} else {
			this.commonType = ClassTemplate.create(type);
			this.commonConstructor = this.commonType.getConstructor(this.bukkitType.getType());
		}

		// Obtain NMS class type and constructor
		type = CommonUtil.getClass(Common.NMS_ROOT + ".Entity" + nmsName);
		if (type == null) {
			this.nmsType = new ClassTemplate();
			this.nmsConstructor = null;
		} else {
			this.nmsType = ClassTemplate.create(type);
			if (entityType == EntityType.PLAYER) {
				this.nmsConstructor = null;
			} else {
				this.nmsConstructor = this.nmsType.getConstructor(WorldRef.TEMPLATE.getType());
			}
		}

		// Obtain NMS Hook class type and constructor
		type = CommonUtil.getClass(Common.COMMON_ROOT + ".entity.nms.NMS" + nmsName);
		if (type == null) {
			this.nmsHookType = new ClassTemplate();
			this.nmsHookConstructor = null;
		} else {
			this.nmsHookType = ClassTemplate.create(type);
			this.nmsHookConstructor = this.nmsHookType.getConstructor(WorldRef.TEMPLATE.getType());
		}
	}

	public boolean hasNMSEntity() {
		return nmsConstructor != null;
	}

	public boolean hasHookEntity() {
		return nmsHookConstructor != null;
	}

	public <T extends Entity> CommonEntity<T> createCommonEntity(T entity) {
		return (CommonEntity<T>) this.commonConstructor.newInstance(entity);
	}

	public Object createNMSEntity() {
		return nmsConstructor.newInstance(new Object[] {null});
	}

	public NMSEntityHook createNMSHookEntity() {
		return (NMSEntityHook) nmsHookConstructor.newInstance(new Object[] {null});
	}
}
