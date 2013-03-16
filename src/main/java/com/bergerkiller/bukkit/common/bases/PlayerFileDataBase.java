package com.bergerkiller.bukkit.common.bases;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

import net.minecraft.server.v1_5_R1.EntityHuman;
import net.minecraft.server.v1_5_R1.IDataManager;
import net.minecraft.server.v1_5_R1.IPlayerFileData;
import net.minecraft.server.v1_5_R1.NBTTagCompound;
import net.minecraft.server.v1_5_R1.WorldNBTStorage;

public abstract class PlayerFileDataBase implements IPlayerFileData {

	/**
	 * @return 
	 * @deprecated use {@link load(HumanEntity)} instead
	 */
	@Deprecated
	@Override
	public final NBTTagCompound load(EntityHuman arg0) {
		NBTTagCompound tag = (NBTTagCompound) load(CommonNMS.getEntity(arg0, HumanEntity.class)).getHandle();
		return tag;
	}

	/**
	 * @deprecated use {@link save(HumanEntity)} instead
	 */
	@Deprecated
	@Override
	public final void save(EntityHuman arg0) {
		save(CommonNMS.getEntity(arg0, HumanEntity.class));
	}

	@Override
	public String[] getSeenPlayers() {
		IDataManager man = CommonNMS.getNative(WorldUtil.getWorlds().iterator().next()).getDataManager();
		if (man instanceof WorldNBTStorage) {
			return ((WorldNBTStorage) man).getSeenPlayers();
		} else {
			return new String[0];
		}
	}

	/**
	 * Loads the human entity data and applies it
	 * 
	 * @param human to load
	 */
	public abstract CommonTagCompound load(HumanEntity human);

	/**
	 * Saves the human entity data
	 * 
	 * @param human to save
	 */
	public abstract void save(HumanEntity human);
}
