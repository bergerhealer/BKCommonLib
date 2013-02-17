package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityPlayerRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("EntityPlayer");
	public static final FieldAccessor<List<?>> chunkQueue = TEMPLATE.getField("chunkCoordIntPairQueue");
	public static final FieldAccessor<Object> playerConnection = TEMPLATE.getField("playerConnection");
	public static final FieldAccessor<Boolean> disconnected = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerConnection"), "disconnected");
	
	private static final Class<?> CraftPlayer = CommonUtil.getCBClass("entity.CraftPlayer");
	private static final MethodAccessor<Object> playerHandle = new SafeMethod<Object>(CraftPlayer, "getHandle");
	
	private static final Class<?> CraftHuman = CommonUtil.getCBClass("entity.CraftHumanEntity");
	private static final ClassTemplate<?> humanTemplate = ClassTemplate.create(CraftHuman);
	private static final MethodAccessor<Object> humanHandle = humanTemplate.getMethod("getHandle");
	private static final FieldAccessor<Object> abilities = humanTemplate.getField("abilities");
	private static final FieldAccessor<Boolean> canInstaBuild = new SafeField<Boolean>(CommonUtil.getNMSClass("PlayerAbilities"), "canInstantlyBuild");
	
	public static final boolean canInstaBuild(HumanEntity human) {
		Object eh = getEntityHuman(human);
		
		if(eh == null)
			return  false;
		
		Object ab = abilities.get(eh);
		return canInstaBuild.get(ab);
	}
	
	public static final Object getEntityPlayer(Player player) {
		Object cp = CommonUtil.tryCast(player, CraftPlayer);
		if(cp != null) {
			return playerHandle.invoke(cp);
		} else
			return null;
	}
	
	public static final Object getEntityHuman(HumanEntity human) {
		Object cp = CommonUtil.tryCast(human, CraftHuman);
		if(cp != null) {
			return humanHandle.invoke(cp);
		} else
			return null;
	}
}
