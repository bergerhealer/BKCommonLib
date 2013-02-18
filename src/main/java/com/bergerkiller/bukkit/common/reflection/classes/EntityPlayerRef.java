package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.conversion.HandleConverter;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityPlayerRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("EntityPlayer");
	public static final FieldAccessor<List<?>> chunkQueue = TEMPLATE.getField("chunkCoordIntPairQueue");
	public static final FieldAccessor<Object> playerConnection = TEMPLATE.getField("playerConnection");
	public static final FieldAccessor<Boolean> disconnected = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerConnection"), "disconnected");
	private static final FieldAccessor<Boolean> canInstaBuild = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerAbilities"), "canInstantlyBuild");
	private static final FieldAccessor<Object> abilities = TEMPLATE.getField("abilities");

	public static boolean canInstaBuild(HumanEntity human) {
		return canInstaBuild.get(abilities.get(HandleConverter.toEntityHandle.convert(human)));
	}
}
