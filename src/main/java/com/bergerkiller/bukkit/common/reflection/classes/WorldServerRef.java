package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;
import net.minecraft.server.v1_4_R1.IWorldAccess;
import net.minecraft.server.v1_4_R1.IntHashMap;
import net.minecraft.server.v1_4_R1.PlayerChunkMap;
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldServer;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class WorldServerRef
{
	public static final FieldAccessor<PlayerChunkMap> playerManager = new SafeField<PlayerChunkMap>(WorldServer.class, "manager");
	public static final FieldAccessor<List<IWorldAccess>> accessList = new SafeField<List<IWorldAccess>>(World.class, "v");
	public static final FieldAccessor<IntHashMap> entitiesById = new SafeField<IntHashMap>(WorldServer.class, "entitiesById");
}
