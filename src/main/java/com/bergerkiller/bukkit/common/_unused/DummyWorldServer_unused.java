package com.bergerkiller.bukkit.common._unused;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;

import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

/**
 * A dummy world that can be used to operate on or using worlds without
 * referencing it on the server<br>
 * This class can be used to pass worlds into functions where functions require
 * one, to alter internal logic
 */
public class DummyWorldServer_unused extends WorldServer {

    private static final ClassTemplate<DummyWorldServer_unused> TEMPLATE = ClassTemplate.create(DummyWorldServer_unused.class);
    public DummyChunkProviderServer_unused DUMMYCPS;

    /**
     * It is impossible to use this constructor, it is merely there to allow
     * extending classes. Use ClassTemplate.newInstanceNull instead.
     */
    @Deprecated
    protected DummyWorldServer_unused() {
        super(constrFail(), null, null, 12, null, null, null);
    }

    private static final MinecraftServer constrFail() {
        throw new UnsupportedOperationException("DummyWorld constructor can not be used - use ClassTemplate.newInstanceNull");
    }

    public static DummyWorldServer_unused newInstance() {
        DummyWorldServer_unused world = TEMPLATE.newInstanceNull();
        world.chunkProvider = world.DUMMYCPS = new DummyChunkProviderServer_unused(world);
        NMSWorld.bukkitWorld.set(world, new CraftWorld(world, null, null));
        return world;
    }
}
