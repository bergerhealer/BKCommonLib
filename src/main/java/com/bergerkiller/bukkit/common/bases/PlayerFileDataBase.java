package com.bergerkiller.bukkit.common.bases;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.utils.NativeUtil;

import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.PlayerFileData;

public abstract class PlayerFileDataBase implements PlayerFileData {

	/**
	 * @deprecated use {@link load(HumanEntity)} instead
	 */
	@Deprecated
	@Override
	public final void load(EntityHuman arg0) {
		load(NativeUtil.getEntity(arg0, HumanEntity.class));
	}

	/**
	 * @deprecated use {@link save(HumanEntity)} instead
	 */
	@Deprecated
	@Override
	public final void save(EntityHuman arg0) {
		save(NativeUtil.getEntity(arg0, HumanEntity.class));
	}

	/**
	 * Loads the human entity data and applies it
	 * 
	 * @param human to load
	 */
	public abstract void load(HumanEntity human);

	/**
	 * Saves the human entity data
	 * 
	 * @param human to save
	 */
	public abstract void save(HumanEntity human);
}
