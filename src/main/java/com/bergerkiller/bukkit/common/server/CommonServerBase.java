package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;

public abstract class CommonServerBase implements CommonServer {

	@Override
	public Collection<String> getLoadableWorlds() {
		File[] files = Bukkit.getWorldContainer().listFiles();
		Collection<String> rval = new ArrayList<String>(files.length);
		for (String worldName : Bukkit.getWorldContainer().list()) {
			if (isLoadableWorld(worldName)) {
				rval.add(worldName);
			}
		}
		return rval;
	}

	@Override
	public File getWorldFolder(String worldName) {
		return new File(Bukkit.getWorldContainer(), worldName);
	}

	@Override
	public boolean isLoadableWorld(String worldName) {
		if (Bukkit.getWorld(worldName) != null) {
			return true;
		}
		File worldFolder = getWorldFolder(worldName);
		return worldFolder.isDirectory() && new File(worldFolder, "level.dat").exists();
	}

	@Override
	public File getWorldRegionFolder(String worldName) {
		File mainFolder = getWorldFolder(worldName);
		// Overworld
		File tmp = new File(mainFolder, "region");
		if (tmp.exists()) {
			return tmp;
		}
		// Nether
		tmp = new File(mainFolder, "DIM-1" + File.pathSeparator + "region");
		if (tmp.exists()) {
			return tmp;
		}
		// The End
		tmp = new File(mainFolder, "DIM1" + File.pathSeparator + "region");
		if (tmp.exists()) {
			return tmp;
		}
		// Unknown???
		return null;
	}
}
