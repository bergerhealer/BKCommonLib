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
		for (File worldFolder : Bukkit.getWorldContainer().listFiles()) {
			if (isLoadableWorld(worldFolder)) {
				rval.add(worldFolder.getName());
			}
		}
		return rval;
	}

	@Override
	public File getWorldFolder(String worldName) {
		return new File(Bukkit.getWorldContainer(), worldName);
	}

	@Override
	public boolean isLoadableWorld(File worldFolder) {
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
