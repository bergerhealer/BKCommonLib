package com.bergerkiller.bukkit.common.internal;

import net.minecraft.server.v1_5_R1.EntityHuman;
import net.minecraft.server.v1_5_R1.IPlayerFileData;
import net.minecraft.server.v1_5_R1.NBTTagCompound;

class CommonPlayerFileData implements IPlayerFileData {
	private final IPlayerFileData previous;

	private CommonPlayerFileData(IPlayerFileData previous) {
		this.previous = previous;
	}

	@Override
	public String[] getSeenPlayers() {
		return previous.getSeenPlayers();
	}

	@Override
	public NBTTagCompound load(EntityHuman arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(EntityHuman arg0) {
		// TODO Auto-generated method stub
		
	}
}
