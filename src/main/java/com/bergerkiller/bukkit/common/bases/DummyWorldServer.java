package com.bergerkiller.bukkit.common.bases;

import java.io.File;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import net.minecraft.server.v1_4_R1.EnumGamemode;
import net.minecraft.server.v1_4_R1.IChunkLoader;
import net.minecraft.server.v1_4_R1.IChunkProvider;
import net.minecraft.server.v1_4_R1.IDataManager;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.PlayerFileData;
import net.minecraft.server.v1_4_R1.WorldData;
import net.minecraft.server.v1_4_R1.WorldProvider;
import net.minecraft.server.v1_4_R1.WorldServer;
import net.minecraft.server.v1_4_R1.WorldSettings;
import net.minecraft.server.v1_4_R1.WorldType;

/**
 * A dummy world that can be used to operate on or using worlds without referencing it on the server<br>
 * This class can be used to pass worlds into functions where functions require one, to alter internal logic
 */
public class DummyWorldServer extends WorldServer {
	@Override
	protected void a(WorldSettings worldsettings) {
	};

	@Override
	protected void b(WorldSettings worldsettings) {
	};

	@Override
	protected IChunkProvider j() {
		return null;
	}

	@Override
	public void g() {
	};

	@Override
	public void a() {
	};

	public DummyWorldServer() {
		this(new CommonDummyDataManager(), new WorldSettings(0, EnumGamemode.NONE, true, false, WorldType.NORMAL));
	}

	private DummyWorldServer(CommonDummyDataManager datamanager, WorldSettings settings) {
		super(CommonUtil.getMCServer(), datamanager, getDummyName(), 0, settings, CommonUtil.getMCServer().methodProfiler, Environment.NORMAL, null);
		datamanager.initialized = true;
		// dereference this dummy world again...
		WorldUtil.removeWorld(this.getWorld());
		// set some variables to null
		this.chunkProvider = this.chunkProviderServer = null;
		this.generator = null;
		this.entityList = null;
		this.tileEntityList = null;
		this.generator = null;
		WorldServerRef.playerChunkMap.set(this, null);
		this.players = null;
		this.tracker = null;
		this.worldMaps = null;
		this.worldProvider = null;
		this.random = null;
	}

	private static String getDummyName() {
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			String name = "dummy" + i;
			if (Bukkit.getServer().getWorld(name) == null) {
				return name;
			}
		}
		return "";
	}

	private static class CommonDummyDataManager implements IDataManager {
		private boolean initialized = false;

		public void checkAccess() {
			if (this.initialized) {
				throw new IllegalStateException("NoLagg chunks dummy world has been accessed!");
			}
		}

		public UUID getUUID() {
			checkAccess();
			return UUID.randomUUID();
		}

		public void checkSession() {
			checkAccess();
		}

		public IChunkLoader createChunkLoader(WorldProvider arg0) {
			checkAccess();
			return null;
		}

		public File getDataFile(String arg0) {
			checkAccess();
			return null;
		}

		public PlayerFileData getPlayerFileData() {
			checkAccess();
			return null;
		}

		public WorldData getWorldData() {
			checkAccess();
			return null;
		}

		public void saveWorldData(WorldData arg0) {
			checkAccess();
		}

		public void a() {
			checkAccess();
		}

		public String g() {
			checkAccess();
			return null;
		}

		public void saveWorldData(WorldData arg0, NBTTagCompound arg1) {
			checkAccess();
		}
	}
}
