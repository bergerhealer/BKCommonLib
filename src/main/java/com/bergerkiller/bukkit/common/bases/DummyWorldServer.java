package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;

import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.WorldServer;

/**
 * A dummy world that can be used to operate on or using worlds without referencing it on the server<br>
 * This class can be used to pass worlds into functions where functions require one, to alter internal logic
 */
public class DummyWorldServer extends WorldServer {
	private static final ClassTemplate<DummyWorldServer> TEMPLATE = ClassTemplate.create(DummyWorldServer.class);
	public DummyChunkProviderServer DUMMYCPS;

	/**
	 * It is impossible to use this constructor, it is merely there to allow extending classes.
	 * Use ClassTemplate.newInstanceNull instead.
	 */
	@Deprecated
	protected DummyWorldServer() {
		super(constrFail(), null, null, 12, null, null, null, null, null);
	}

	private static final MinecraftServer constrFail() {
		throw new UnsupportedOperationException("DummyWorld constructor can not be used - use ClassTemplate.newInstanceNull");
	}

	public static DummyWorldServer newInstance() {
		DummyWorldServer world = TEMPLATE.newInstanceNull();
		world.chunkProvider = world.chunkProviderServer = world.DUMMYCPS = new DummyChunkProviderServer(world);
		return world;
	}
}
