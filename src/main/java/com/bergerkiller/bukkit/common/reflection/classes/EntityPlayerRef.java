package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class EntityPlayerRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("EntityPlayer");
	public static final FieldAccessor<List<?>> chunkQueue = TEMPLATE.getField("chunkCoordIntPairQueue");
	public static final FieldAccessor<Object> playerConnection = TEMPLATE.getField("playerConnection");
	public static final FieldAccessor<Boolean> disconnected = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerConnection"), "disconnected");
	private static final Class<?> CraftHuman = CommonUtil.getCBClass("entity.CraftHumanEntity");
	private static final ClassTemplate<?> humanTemplate = ClassTemplate.create(CraftHuman);
	private static final FieldAccessor<Object> abilities = humanTemplate.getField("abilities");
	private static final FieldAccessor<Boolean> canInstaBuild = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerAbilities"), "canInstantlyBuild");

	public static final boolean canInstaBuild(HumanEntity human) {
		Object eh = NativeUtil.getNative(human);
		if(eh == null) {
			return false;
		}
		Object ab = abilities.get(eh);
		return canInstaBuild.get(ab);
	}
}
