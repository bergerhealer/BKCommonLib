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
		// Bukkit
		this.entityType = entityType;
		this.bukkitType = new ClassTemplate(entityType.getEntityClass());
		// Class names
		final Class<?> hookType = CommonUtil.getClass(Common.COMMON_ROOT + ".entity.nms.NMS" + nmsName);
		final Class<?> commonType = CommonUtil.getClass(Common.COMMON_ROOT + ".entity.Common" + nmsName);
		// Classes
		this.nmsType = NMSClassTemplate.create("Entity" + nmsName);
		this.nmsHookType = new ClassTemplate(hookType);
		this.commonType = new ClassTemplate(LogicUtil.fixNull(commonType, CommonEntity.class));
		// Constructors
		this.nmsConstructor = this.nmsType.getConstructor(WorldRef.TEMPLATE.getType());
		this.commonConstructor = this.commonType.getConstructor(this.bukkitType.getType());
		if (hookType == null) {
			this.nmsHookConstructor = null;
		} else {
			this.nmsHookConstructor = this.nmsHookType.getConstructor(WorldRef.TEMPLATE.getType());
		}
		// Network
		this.networkUpdateInterval = networkUpdateInterval;
		this.networkViewDistance = networkViewDistance;
		this.networkIsMobile = networkIsMobile;
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
