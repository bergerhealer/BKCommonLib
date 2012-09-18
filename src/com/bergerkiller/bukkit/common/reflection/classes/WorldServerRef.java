package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import net.minecraft.server.IWorldAccess;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import com.bergerkiller.bukkit.common.reflection.SafeField;

public class WorldServerRef {
	public static SafeField<PlayerManager> playerManager = new SafeField<PlayerManager>(WorldServer.class, "manager");
	public static final SafeField<List<IWorldAccess>> accessList = new SafeField<List<IWorldAccess>>(World.class, "x");
}
